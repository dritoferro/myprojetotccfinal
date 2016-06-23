/*
 * File: Storage.java
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
 * $Id: Storage.java,v 1.12 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.target;

import org.friendlysnmp.FException;
import org.snmp4j.agent.mo.snmp.StorageType;

/**
 * See description field from SNMPv2-TC or RFC-2579 for 
 * Textual Convention "StorageType".
 * <p>
 * This enumeration maps to SNMP4J values. 
 */
public enum Storage {
    // Enum spelling is exact Upper Case of MIB definitions!!
    /** Storage of <i>other</i> type */
    OTHER      (StorageType.other,       "other"),      // 1 
    /** Storage of <i>volatile</i> type */
    VOLATILE   (StorageType.volatile_,   "volatile"),   // 2
    /** Storage of <i>nonVolatile</i> type */
    NONVOLATILE(StorageType.nonVolatile, "nonVolatile"),// 3
    /** Storage of <i>permanent</i> type */
    PERMANENT  (StorageType.permanent,   "permanent"),  // 4
    /** Storage of <i>readOnly</i> type */
    READONLY   (StorageType.readOnly,    "readOnly");   // 5
    
    /** Storage type */
    int storageType;
    
    /** String presentation */
    String toString;
    
    /**
     * @param storageType storage type
     * @param toString value to display (match to MIB definition)
     */
    private Storage(int storageType, String toString) {
        this.storageType = storageType;
        this.toString = toString;
    }
    
    /**
     * Finds <code>Storage</code> enum object from its string value. 
     * Used to map <code>Storage</code> object to properties value. 
     * 
     * @param s string value
     * @return <code>Storage</code> object
     * @throws FException if <code>Storage</code> object is not found
     */
    static Storage find(String s) throws FException { 
        if (s != null) {
            s = s.trim();
            for (Storage st : Storage.values()) {
                if (st.name().equalsIgnoreCase(s)) {
                    return st;
                }
            }
        }
        throw new FException("Not valid '%s' storage type", s);
    }
    
    /**
     * {@inheritDoc}
     * @see java.lang.Enum#toString()
     */
    public String toString() {
        return toString;
    }
    
} // enum Storage
