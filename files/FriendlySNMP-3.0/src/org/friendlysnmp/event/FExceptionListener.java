/*
 * File: FExceptionListener.java
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
 * $Id: FExceptionListener.java,v 1.8 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FException;

/**
 * Objects implementing this interface should be registered with FriendlyAgent
 * for receiving FException objects thrown while processing SNMP requests. 
 * This processing may happen in the FriendlySNMP library itself or in the 
 * outside code in listeners processing these requests. Most probably these 
 * types of exceptions are happening while development and they signal problems 
 * in a code.The bulk part of them belongs to two types:
 * <ol>
 * <li> Values conversions Java-to-Variable and back. Usually this exception 
 * is a result of mismatch "syntax" stored in the SNMP managed object and 
 * a type of assigned value. The mismatch may be caused by wrong value restored 
 * from persistent storage or assigned in the code.
 * <li> Some generic <tt>Throwable</tt> is caught in the code. 
 * </ol>
 * 
 * @version $Revision: 1.8 $
 */
public interface FExceptionListener {

    /**
     * This method is called on registered object with an exception caught
     * in the library. See details in this class JavaDoc.
     * 
     * @param msg message with some explanation about the exception, or null
     * @param e exception
     */
    public void exceptionThrown(String msg, FException e);
    
} // interface FExceptionListener
