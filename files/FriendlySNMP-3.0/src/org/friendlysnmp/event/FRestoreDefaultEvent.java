/*
 * File: FRestoreDefaultEvent.java
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
 * $Id: FRestoreDefaultEvent.java,v 1.6 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.FValueNode;

public class FRestoreDefaultEvent {

    private FValueNode node;
    
    public FRestoreDefaultEvent(FValueNode node) {
        this.node = node;
    }
    
    public FValueNode getNode() {
        return node;
    }
    
    public FScalar getScalar() {
        return (node instanceof FScalar ? (FScalar)node : null);
    }
    
    public FTable getTable() {
        return (node instanceof FTable ? (FTable)node : null);
    }
    
} // class FRestoreDefaultEvent
