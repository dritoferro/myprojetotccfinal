/*
 * File: FTableSetListener.java
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
 * $Id: FTableSetListener.java,v 1.8 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FColumn;
import org.friendlysnmp.FID;
import org.friendlysnmp.FTable;
import org.friendlysnmp.TableRowAction;

/**
 * Implement this interface and register it with {@link FTable} object
 * to receive SET events from MIB browser.
 * 
 * @version $Revision: 1.8 $
 * @see FTable
 */
public interface FTableSetListener {

    /**
     * Register object of this class with {@link FTable} and this method 
     * will be called on SET event from MIB browser.
     * 
     * @param table MIB table changed by MIB browser
     * @param idRow changed row ID
     * @param col column
     * @param action one of CHANGE, DELETE, CREATE
     */
    public void set(FTable table, FID idRow, FColumn col, TableRowAction action);
    
} // interface FTableSetListener
