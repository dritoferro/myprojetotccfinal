/*
 * File: FHandler.java
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
 * $Id: FHandler.java,v 1.13 2014/01/15 23:46:32 mg Exp $
 */
package org.friendlysnmp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.friendlysnmp.mib.BaseMib;

/**
 * Base class for handlers which are dealing with XxxMibFriend classes.
 *  
 * @version $Revision: 1.13 $
 */
abstract public class FHandler {
    
    public enum AgentStartType { STARTED, RESTARTED }
    
    protected AgentWorker agent;
    protected BaseMib mibBase;
    
    public void setAgent(AgentWorker agent) {
        this.agent = agent;
    }
    
    public void registerMib(BaseMib mibBase) throws FException {
        if (this.mibBase != null) {
            throw new FException("MIB is already set: %s",  
                      this.mibBase.getClass().getName());
        }
        this.mibBase = mibBase;
    }
    
    /**
     * The method is called by SNMP agent when it's initialized.
     * Default implementation does nothing.
     * Override this method in derived class, for example, 
     * to initialize static scalars and tables.  
     * 
     * @throws FException
     */
    public void init() throws FException {
    }
    
    /**
     * The method is called by SNMP agent when it's started or restarted.
     * Default implementation does nothing.
     * Override this method in derived class, for example,
     * to start threads.  
     */
    public void start(AgentStartType startType) {
    }
    
    /**
     * The method is called by SNMP agent when it's stopped.
     * Default implementation does nothing. 
     * Override this method in derived class, for example,
     * to stop threads.  
     */
    public void stop() {
    }
    
    /**
     * The method is called by SNMP agent when it's shutdown.
     * Default implementation does nothing. 
     * Override this method in derived class has, for example,
     * to shutdown threads.  
     */
    public void shutdown() {
    }
    
    /**
     * This method is a shortcut for <code>Integer.valueOf(n).equals(obj)</code> 
     * 
     * @param obj any object, possibly null
     * @param value int value
     * @return result of comparison
     */
    public static final boolean isIntegerEqual(Object obj, int value) {
        return Integer.valueOf(value).equals(obj);
    } // isIntegerEqual()
    
    /**
     * Current timestamp in string format.
     * 
     * @return current timestamp
     */
    public static final String getTimestampNow() {
        final SimpleDateFormat FMT = new SimpleDateFormat("MMM d, yyyy HH:mm:ss.SSS");
        return FMT.format(Calendar.getInstance().getTime());
    } // getTimestampNow()
    
} // class FHandler
