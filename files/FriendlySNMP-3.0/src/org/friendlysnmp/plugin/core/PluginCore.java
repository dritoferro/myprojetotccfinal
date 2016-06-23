/*
 * File: PluginCore.java
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
 * $Id: PluginCore.java,v 1.16 2014/01/18 19:37:46 mg Exp $
 */
package org.friendlysnmp.plugin.core;

import org.friendlysnmp.FException;
import org.friendlysnmp.event.ShutdownListener;
import org.friendlysnmp.plugin.FPlugin;

/**
 * Base class for core plugin classes.
 */
public class PluginCore extends FPlugin {

    /** CoreLifeHandler object. */
    private CoreLifeHandler handlerLife;
    
    /** CoreExceptionsHandler object. */
    private CoreExceptionsHandler handlerExceptions;
    
    /**
     * Constructor.
     */
    public PluginCore() {
        super(new FriendlySnmpMibFriend());
    }

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.plugin.FPlugin#initPlugin()
     */
    @Override
    public void initPlugin() throws FException {
        mibBase.addHandler(new CoreAppInfoHandler());
        mibBase.addHandler(handlerLife = new CoreLifeHandler());
        mibBase.addHandler(new CorePersistenceHandler());
        mibBase.addHandler(new CoreDeadlockHandler());
        mibBase.addHandler(handlerExceptions = new CoreExceptionsHandler());
    } 
    
    /**
     * Adds <code>ShutdownListener</code>.
     * 
     * @param l <code>ShutdownListener</code> object.
     */
    public void addShutdownListener(ShutdownListener l) {
        handlerLife.addShutdownListener(l);
    } // addShutdownListener()
    
    /**
     * Removes <code>ShutdownListener</code>.
     * 
     * @param l <code>ShutdownListener</code> object.
     */
    public void removeShutdownListener(ShutdownListener l) {
        handlerLife.removeShutdownListener(l);
    } // removeShutdownListener()
    
    /**
     * Call this method to report a caught exception in the application 
     * to the MIB browser.
     * 
     * @param comment error message.
     * @param e exception.
     */
    public void reportException(String comment, Throwable e) {
        handlerExceptions.reportException(comment, e);
    } // reportException()
    
} // class PluginCore
