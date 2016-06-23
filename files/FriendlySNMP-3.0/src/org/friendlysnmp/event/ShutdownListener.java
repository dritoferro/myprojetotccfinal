/*
 * File: ShutdownListener.java
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
 * $Id: ShutdownListener.java,v 1.8 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

/**
 * Objects implementing this interface should be registered with FriendlyAgent
 * for receiving shutdown event from MIB browser. MIB browser displays
 * shutdownApp scalar value as 'running' if there is at least a single 
 * ShutdownListener registered and 'disabled' if there are no ShutdownListener 
 * registered.  
 * 
 * @version $Revision: 1.8 $
 */
public interface ShutdownListener {

    /**
     * This method is called on registered object with request from MIB 
     * browser to shutdown the application. The shutdown request is set to
     * FRIENDLY-SNMP-MIB scalar <i>shutdownApp</i>.  
     * The application may return <code>false</code> 
     * as a signal that shutdown is rejected. Otherwise the application
     * should call <code>System.exit()</code> and return value does not matter. 
     * <p>
     * This call is made on MIB browser SET request validation and returns
     * <code>ValueValidation.NO_ACCESS</code> if application rejects shutdown.
     * 
     * @return return false to reject shutdown request
     */
    public boolean shutdownRequest();
    
} // interface ShutdownListener
