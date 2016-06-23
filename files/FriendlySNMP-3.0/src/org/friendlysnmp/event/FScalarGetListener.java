/*
 * File: FScalarGetListener.java
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
 * $Id: FScalarGetListener.java,v 1.7 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FScalar;

/**
 * Implement this interface and register it with {@link FScalar} object
 * to receive GET events from MIB browser.
 *  
 * @version $Revision: 1.7 $
 * @see FScalar
 */
public interface FScalarGetListener {

    /**
     * Register object of this class with {@link FScalar} and this method 
     * will be called on GET event from MIB browser.
     * 
     * @param scalar value which was requested by MIB browser.
     */
    public void get(FScalar scalar);
    
} // interface FScalarGetListener
