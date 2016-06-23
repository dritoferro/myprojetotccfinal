/*
 * File: PersistPolicy.java
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
 * $Id: PersistPolicy.java,v 1.3 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import org.friendlysnmp.FException;

/**
 * Persistence policy. 
 */
public enum PersistPolicy { 
    /**
     * Save persistence storage on each change. This mode is safe because
     * in case the application dies it has persistence storage saved. 
     * Drawback is a performance hit. 
     */
    ON_CHANGE, 
    /**
     * Saves persistence storage on method 
     * <code>PersistStorage.shutdown()</code> call.
     */
    ON_EXIT;
    /**
     * Finds <code>PersistPolicy</code> enum object from its string value. 
     * Used to map <code>PersistPolicy</code> object to properties value. 
     * 
     * @param s string value
     * @return <code>PersistPolicy</code> object
     * @throws FException if <code>PersistPolicy</code> object is not found
     */
    public static PersistPolicy find(String s) throws FException { 
        if (s != null) {
            s = s.trim();
            for (PersistPolicy p : PersistPolicy.values()) {
                if (p.name().equalsIgnoreCase(s)) {
                    return p;
                }
            }
        }
        throw new FException("Not valid '%s' persistency policy", s);
    }
} // enum PersistPolicy
