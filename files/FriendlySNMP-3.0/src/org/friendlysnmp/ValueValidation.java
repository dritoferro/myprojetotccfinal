/*
 * File: ValueValidation.java
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
 * $Id: ValueValidation.java,v 1.12 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import org.snmp4j.mp.SnmpConstants;

/**
 * SET operation validation result which is sent to MIB manager by agent.
 * See error codes RFC-3416 and descriptions at
 * <a href="http://msdn2.microsoft.com/en-us/library/aa378974.aspx"
 * target="_blank">here</a>.
 * <p>
 * Error codes are declared in {@link SnmpConstants}
 * 
 * @version $Revision: 1.12 $
 */
public enum ValueValidation {
    /** 
     * The agent reports that no errors occurred during transmission. 
     */
    SUCCESS(SnmpConstants.SNMP_ERROR_SUCCESS), // 0
    
    /**
     * The agent could not place the results of the requested 
     * SNMP operation in a single SNMP message.
     */
    TOO_BIG(SnmpConstants.SNMP_ERROR_TOO_BIG), // 1
    
    /**
     * The requested SNMP operation identified an unknown variable.
     */
    NO_SUCH_NAME(SnmpConstants.SNMP_ERROR_NO_SUCH_NAME), // 2
    
    /**
     * The requested SNMP operation tried to change a variable but 
     * it specified either a syntax or value error.
     */
    BAD_VALUE(SnmpConstants.SNMP_ERROR_BAD_VALUE), // 3
    
    /**
     * The requested SNMP operation tried to change a variable 
     * that was not allowed to change, according to the community 
     * profile of the variable.
     */
    READ_ONLY(SnmpConstants.SNMP_ERROR_READ_ONLY), // 4
    
    /**
     * An error other than one of those listed here occurred during 
     * the requested SNMP operation.
     */
    GENERAL_ERROR(SnmpConstants.SNMP_ERROR_GENERAL_ERROR), // 5
    
    /**
     * The specified SNMP variable is not accessible.
     */
    NO_ACCESS(SnmpConstants.SNMP_ERROR_NO_ACCESS), // 6
    
    /**
     * The value specifies a type that is inconsistent with the type 
     * required for the variable.
     */
    WRONG_TYPE(SnmpConstants.SNMP_ERROR_WRONG_TYPE), // 7
    
    /**
     * The value specifies a length that is inconsistent with the 
     * length required for the variable.
     */
    WRONG_LENGTH(SnmpConstants.SNMP_ERROR_WRONG_LENGTH), // 8
    
    /**
     * The value contains an Abstract Syntax Notation One (ASN.1) 
     * encoding that is inconsistent with the ASN.1 tag of the field.
     */
    WRONG_ENCODING(SnmpConstants.SNMP_ERROR_WRONG_ENCODING), // 9
    
    /**
     * The value cannot be assigned to the variable.
     */
    WRONG_VALUE(SnmpConstants.SNMP_ERROR_WRONG_VALUE), // 10
    
    /**
     * The variable does not exist, and the agent cannot create it.
     */
    NO_CREATION(SnmpConstants.SNMP_ERROR_NO_CREATION), // 11
    
    /**
     * The value is inconsistent with values of other managed objects.
     */
    INCONSISTENT_VALUE(SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE), // 12
    
    /**
     * Assigning the value to the variable requires allocation of 
     * resources that are currently unavailable.
     */
    RESOURCE_UNAVAILABLE(SnmpConstants.SNMP_ERROR_RESOURCE_UNAVAILABLE),// 13
    
    /**
     * No validation errors occurred, but no variables were updated.
     */
    COMMIT_FAILED(SnmpConstants.SNMP_ERROR_COMMIT_FAILED), // 14
    
    /**
     * No validation errors occurred. Some variables were updated 
     * because it was not possible to undo their assignment.
     */
    UNDO_FAILED(SnmpConstants.SNMP_ERROR_UNDO_FAILED), // 15
    
    /**
     * An authorization error occurred.
     */
    AUTHORIZATION_ERROR(SnmpConstants.SNMP_ERROR_AUTHORIZATION_ERROR), // 16
    
    /**
     * The variable exists but the agent cannot modify it.
     */
    NOT_WRITEABLE(SnmpConstants.SNMP_ERROR_NOT_WRITEABLE), // 17
    
    /**
     * The variable does not exist; the agent cannot create it 
     * because the named object instance is inconsistent with the 
     * values of other managed objects.
     */
    INCONSISTENT_NAME(SnmpConstants.SNMP_ERROR_INCONSISTENT_NAME), // 18
    ;
    
    /**
     * Validity value 
     */
    int snmpValidity;
    
    /**
     * Constructor
     * 
     * @param snmpValidity validity value
     */
    ValueValidation(int snmpValidity) {
        this.snmpValidity = snmpValidity;
    }
    /**
     * Returns validity integer value
     * @return validity integer value
     */
    int toInt() {
        return snmpValidity;
    }
    
    public final static ValueValidation find(int code) {
        for (ValueValidation vv : ValueValidation.values()) {
            if (code == vv.snmpValidity) {
                return vv;
            }
        }
        return null;
    }
    
} // enum ValueValidation
