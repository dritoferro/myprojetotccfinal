/*
 * File: FScalarValidationListener.java
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
 * $Id: FScalarValidationListener.java,v 1.8 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp.event;

import org.friendlysnmp.FScalar;
import org.friendlysnmp.ValueValidation;

/**
 * The listener interface for receiving SET validation events from MIB 
 * browser. The class that is interested in processing events of this type 
 * implements this interface, and the object created with that class is 
 * registered with {@link FScalar} object. The return value is an enum 
 * {@link ValueValidation}. Application performs new value validation   
 * and accepts it by returning ValueValidation.SUCCESS or rejects it by 
 * returning any other ValueValidation.XXXX value. 
 * The reject reason is sent to MIB browser.
 * 
 * @version $Revision: 1.8 $
 * @see FScalar
 */
public interface FScalarValidationListener {

    /**
     * Invoked when a validation event is fired.
     * 
     * @param scalar scalar node object which was changed by MIB browser.
     * @param objNewValue new value assigned by MIB browser to scalar.
     * @return validation result
     */
    public ValueValidation validate(FScalar scalar, Object objNewValue);
    
} // interface FScalarValidationListener
