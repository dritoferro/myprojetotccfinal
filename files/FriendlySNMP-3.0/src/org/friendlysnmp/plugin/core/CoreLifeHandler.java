/*
 * File: CoreLifeHandler.java
 * 
 * Copyright (C) 2014 FriendlySNMP.org; All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301  USA
 * 
 * $Id: CoreLifeHandler.java,v 1.17 2014/01/22 23:28:02 mg Exp $
 */
package org.friendlysnmp.plugin.core;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.friendlysnmp.FException;
import org.friendlysnmp.FHandler;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.ValueValidation;
import org.friendlysnmp.event.FScalarGetListener;
import org.friendlysnmp.event.FScalarValidationListener;
import org.friendlysnmp.event.ShutdownListener;
import org.friendlysnmp.mib.BaseMib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.mp.SnmpConstants;

/**
 * The class provides application information to SNMP objects  
 * declared in FRIENDLY-SNMP-MIB.
 * 
 * @version $Revision: 1.17 $
 */
public class CoreLifeHandler extends FHandler {
    /**
     * Logger object
     */
    private static final Logger logger = LoggerFactory.getLogger(CoreLifeHandler.class);

    /**
     * "Magic" word in MIB browser SET command to shutdown application.
     */
    private static final String VALUE_SHUTDOWN = "shutdown";
    
    /**
     * FriendlySNMP MIB
     */
    private FriendlySnmpMibFriend mib;
    
    /**
     * Collection of shutdown listeners
     */
    private Set<ShutdownListener> hsShutdown;
    
    /**
     * Constructor
     */
    public CoreLifeHandler() {
        hsShutdown = new CopyOnWriteArraySet<ShutdownListener>();
    } // CoreLifeHandler()
    
    @Override
    public void registerMib(BaseMib mibBase) throws FException { 
        super.registerMib(mibBase);
        mib = (FriendlySnmpMibFriend)mibBase;
        
        // Shutdown
        FScalar scalar = mib.getShutdownApp();
        scalar.setVolatile(true);
        mib.getShutdownApp().addGetListener(new FScalarGetListener() {
            @Override
            public void get(FScalar scalar) {
                getShutdownApp();
            }
        });
        mib.getShutdownApp().addValidationListener(new FScalarValidationListener() {
            @Override
            public ValueValidation validate(FScalar scalar, Object objNewValue) {
                // Validation event is propagated to the application
                // and if accepted the application might call System.exit().
                // This scalar does not have SET listener.
                return validateShutdownApp(objNewValue);
            }
        });
    } // registerMib()
    
    /**
     * Sends startup notification and starts heartbeat thread.  
     */
    @Override
    public void start(AgentStartType startType) {
        if (startType == AgentStartType.STARTED) {
            agent.getNotification(SnmpConstants.coldStart).sendNotification();
        }
        if (startType == AgentStartType.RESTARTED) {
            agent.getNotification(SnmpConstants.warmStart).sendNotification();
        }
    } // start()

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.FHandler#stop()
     */
    @Override
    public void stop() {
        logger.debug("stop");
        mib.getAppStop().sendNotification();
    } // stop()

    /**
     * Sends shutdown notification and shutdowns heartbeat thread.  
     */
    @Override
    public void shutdown() {
        logger.debug("shutdown");
        mib.getAppShutdown().sendNotification();
    } // shutdown()
    
    //--------------------------------separator--------------------------------
    static int ______SHUTDOWN;

    /**
     * Adds <code>ShutdownListener</code>
     * 
     * @param l <code>ShutdownListener</code> object
     */
    public void addShutdownListener(ShutdownListener l) {
        hsShutdown.add(l);
    } // addShutdownListener()
    
    /**
     * Removes <code>ShutdownListener</code>
     * 
     * @param l <code>ShutdownListener</code> object
     */
    public void removeShutdownListener(ShutdownListener l) {
        hsShutdown.remove(l);
    } // removeShutdownListener()
    
    /**
     * Sets <code>shutdownApp</code> scalar value
     */
    private void getShutdownApp() {
        mib.getShutdownApp().setValueEx(hsShutdown.size() == 0 ? "disabled" : "running");
    } // getShutdownApp()
    
    /**
     * Validates <code>shutdownApp</code> scalar value. The application
     * registered <code>ShutdownListener</code> is responsible to call 
     * <code>System.exit()</code> or reject shutdown request.
     * 
     * @param objNewValue
     * @return validation result
     */
    private ValueValidation validateShutdownApp(Object objNewValue) {
        if (!VALUE_SHUTDOWN.equals(objNewValue)) {
            return ValueValidation.BAD_VALUE;
        }
        if (hsShutdown.size() == 0) {
            return ValueValidation.NO_ACCESS;
        }
        boolean shutdownAccepted = false;
        for (ShutdownListener l : hsShutdown) {
            if (l.shutdownRequest()) {
                // Expected application call System.exit() at this
                // moment and code below is not executed.
                shutdownAccepted = true;
            }
        }
        return shutdownAccepted ? 
            ValueValidation.GENERAL_ERROR // application forget to System.exit() 
          : ValueValidation.NO_ACCESS;    // application rejects shutdown request
    } // validateShutdownApp()
    
} // class CoreLifeHandler
