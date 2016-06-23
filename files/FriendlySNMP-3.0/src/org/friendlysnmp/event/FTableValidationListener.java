/*
 * File: FTableValidationListener.java
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
 * $Id: FTableValidationListener.java,v 1.9 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FColumn;
import org.friendlysnmp.FID;
import org.friendlysnmp.FTable;
import org.friendlysnmp.TableRowAction;
import org.friendlysnmp.ValueValidation;

/**
 * The listener interface for receiving SET validation events from MIB 
 * browser. The class that is interested in processing events of this type 
 * implements this interface, and the object created with that class is 
 * registered with {@link FTable} object. The return value is an enum 
 * {@link ValueValidation}. Application performs new value validation   
 * and accepts it by returning ValueValidation.SUCCESS or rejects it by 
 * returning any other ValueValidation.XXXX value. 
 * The reject reason is sent to MIB browser.
 * 
 * @version $Revision: 1.9 $
 * @see FTable
 */
public interface FTableValidationListener {

    /**
     * Invoked when a validation event is fired.
     * 
     * @param table table node object which was changed by MIB browser.
     * @param objNewValue new value to validate.
     * @param idRow row ID.
     * @param col column. 
     * @param action one of CHANGE, DELETE, CREATE.
     * 
     * @return validation result
     */
    public ValueValidation validate(FTable table, Object objNewValue, 
            FID idRow, FColumn col, TableRowAction action);
    
} // interface FTableValidationListener
