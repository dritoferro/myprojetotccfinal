/*
 * File: TableRowAction.java
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
 * $Id: TableRowAction.java,v 1.4 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

/**
 * Table rows actions performed by MIB manager.
 */
public enum TableRowAction { 
    /** 
     * New row is created.
     */
    ROW_CREATE, 
    
    /** 
     * Old row is deleted. 
     */
    ROW_DELETE, 
    
    /** 
     * Old row is modified.
     */
    ROW_CHANGE 
}
