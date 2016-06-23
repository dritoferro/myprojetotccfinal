/*
 * File: FTableGetListener.java
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
 * $Id: FTableGetListener.java,v 1.7 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FTable;

/**
 * Implement this interface and register it with {@link FTable} object
 * to receive GET events from MIB browser.
 *  
 * @version $Revision: 1.7 $
 * @see FTable
 */
public interface FTableGetListener {

    /**
     * Register object of this class with {@link FTable} and this method 
     * will be called on GET event from MIB browser.
     * 
     * @param table value which was requested by MIB browser.
     */
    public void get(FTable table);
    
} // interface FTableGetListener
