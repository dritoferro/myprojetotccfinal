/*
 * File: ValueSyntax.java
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
 * $Id: ValueSyntax.java,v 1.9 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

import org.snmp4j.smi.SMIConstants;

/**
 * Enumeration defines the values for SMI syntax types.
 * SMI syntax value types are declared in {@link SMIConstants}
 * 
 * @version $Revision: 1.9 $
 */
public enum ValueSyntax {
    
    INTEGER32         ("Integer32",        SMIConstants.SYNTAX_INTEGER32),    // 2
    INTEGER           ("Integer",          SMIConstants.SYNTAX_INTEGER),      // 2
    OCTET_STRING      ("OctetString",      SMIConstants.SYNTAX_OCTET_STRING), // 4
    BITS              ("Bits",             SMIConstants.SYNTAX_BITS),         // 4
    NULL              ("Null",             SMIConstants.SYNTAX_NULL),         // 5
    OBJECT_IDENTIFIER ("ObjectIdentifier", SMIConstants.SYNTAX_OBJECT_IDENTIFIER), // 6
    IPADDRESS         ("IpAddress",        SMIConstants.SYNTAX_IPADDRESS),    // 64
    COUNTER32         ("Counter32",        SMIConstants.SYNTAX_COUNTER32),    // 65
    UNSIGNED_INTEGER32("UnsignedInteger32",SMIConstants.SYNTAX_UNSIGNED_INTEGER32), // 66
    GAUGE32           ("Gauge32",          SMIConstants.SYNTAX_GAUGE32),      // 66
    TIMETICKS         ("TimeTicks",        SMIConstants.SYNTAX_TIMETICKS),    // 67
    OPAQUE            ("Opaque",           SMIConstants.SYNTAX_OPAQUE),       // 68
    COUNTER64         ("Counter64",        SMIConstants.SYNTAX_COUNTER64),    // 70
    ;
    
    private String display;
    private int syntax;
    
    /**
     * Constructor.
     * 
     * @param display
     * @param syntax
     */
    ValueSyntax(String display, int syntax) {
        this.display = display;
        this.syntax = syntax;
    }
    /**
     * Returns syntax integer value.
     * @return syntax integer value.
     */
    public int toInt() {
        return syntax;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ValueSyntax[] all = values();
        for (ValueSyntax vs : all) {
            if (syntax == vs.syntax) {
                if (sb.length() > 0) {
                    sb.append(" / ");
                }
                sb.append(vs.display);
            }
        }
        return sb.toString();
    }
    
    public final static ValueSyntax find(int syntax) {
        for (ValueSyntax vv : ValueSyntax.values()) {
            if (syntax == vv.syntax) {
                return vv;
            }
        }
        return null;
    }
    
} // enum ValueSyntax
