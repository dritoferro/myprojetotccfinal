/*
 * File: FRestoreDefaultListener.java
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
 * $Id: FRestoreDefaultListener.java,v 1.8 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FException;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;

/**
 * Implement this interface and register it with {@link FScalar} object
 * to receive event to restore default value. This event is a result
 * of this scalar value deletion from persistent storage. 
 * 
 * @version $Revision: 1.8 $
 * @see FScalar
 */
public interface FRestoreDefaultListener {

    /**
     * Register object of this class with {@link FScalar} or {@link FTable} 
     * node to receive event on removing persistency entry
     * for this node in the MIB browser or setVolatile(true) method call.
     * 
     * @param ev object with details about this event
     */
    public void restoreDefault(FRestoreDefaultEvent ev) throws FException;
    
} // interface FRestoreDefaultListener
