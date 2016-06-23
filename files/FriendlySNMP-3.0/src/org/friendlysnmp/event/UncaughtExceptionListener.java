/*
 * File: UncaughtExceptionListener.java
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
 * $Id: UncaughtExceptionListener.java,v 1.8 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.UncaughtExceptionHandler;

/**
 * The listener interface for receiving uncaught exceptions.
 * The class that is interested in processing uncaught exceptions events 
 * must implement this interface and registered it with an agent.
 * 
 * @version $Revision: 1.8 $
 */
public interface UncaughtExceptionListener {
    
    /**
     * Invoked from {@link UncaughtExceptionHandler} 
     * when an uncaught exception is discovered.
     * 
     * @param t thread were exception was discovered
     * @param e exception
     */
    public void uncaughtException(Thread t, Throwable e);
    
} // interface UncaughtExceptionListener
