/*
 * File: FID.java
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
 * $Id: FID.java,v 1.15 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.friendlysnmp.persist.PersistExternalizable;
import org.snmp4j.smi.OID;

/**
 * Immutable wrapper which hides <code>org.snmp4j.smi.OID</code> mutable
 * implementation.
 *  
 * @version $Revision: 1.15 $
 */
public class FID extends PersistExternalizable implements Comparable<FID> {

    /**
     * Generated UID. This UID should be changed only after 
     * serialized member variables modification. 
     * 
     * This UID is valid for two serialized member variables: 
     * <br>(1) String name; 
     * <br>(2) OID oid;
     */
    private static final long serialVersionUID = -6245309814941267942L;

    /**
     * Default name is <i>&lt;nodef&gt;</i>.
     */
    public static final String DEFAULT_NAME = "<nodef>";
    
    /**
     * Actual hidden <code>org.snmp4j.smi.OID</code> associated with this object.
     */
    private OID oid;
    
    /**
     * Name of SNMP object associated with this object. 
     */
    private String name;
    
    /**
     * Default ctor for deserialization
     */
    public FID() {
        super(true);
    }
    
    /**
     * Constructor. The name will be {@link #DEFAULT_NAME}
     * 
     * @param id comma separated list of integers
     */
    public FID(int ... id) {
        this(new OID(id), DEFAULT_NAME);
    }
    
    /**
     * Constructor. The name will be {@link #DEFAULT_NAME}
     * 
     * @param oid OID
     */
    public FID(OID oid) {
        this(oid, DEFAULT_NAME);
    }
    
    /**
     * Constructor. 
     * 
     * @param oid OID
     * @param name name
     */
    public FID(OID oid, String name) {
        super(false);
        this.oid = oid;
        this.name = name;
    }
    
    /**
     * Clones this object.
     * 
     * @return cloned <code>FID</code> object
     */
    public FID cloneFID() {
        return new FID(oid, name);
    }
    
    /**
     * Returns copy of internal OID
     * 
     * @return copy of internal OID
     */
    public OID getOID() {
        return new OID(oid);
    }
    
    /**
     * Returns array of integers presenting internal OID
     * 
     * @return array of integers presenting internal OID
     */
    public int[] getInt() {
        return getOID().getValue();
    }
    
    /**
     * Returns array of bytes presenting internal OID
     * 
     * @return array of bytes presenting internal OID
     */
    public byte[] getBytes() {
        return getOID().toByteArray();
    }
    
    /**
     * Returns name
     * 
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Generates next ID by increasing the max first integer and 
     * setting others (if exist) to 1.
     * 
     * @return next ID
     */
    public FID next() {
        OID oidNext = new OID(oid);
        int[] intAll = oidNext.getValue(); // returns without clone!
        intAll[0]++;
        for (int i = 1;  i < intAll.length;  i++) {
            intAll[i] = 1;
        }
        return new FID(oidNext);
    }
    
    /**
     * Compares two objects and returns max object. 
     * 
     * @param a first object
     * @param b second object
     * @return max object
     */
    public static final FID max(FID a, FID b) {
        OID oidA = a.oid;
        OID oidB = b.oid;
        OID oidMax = OID.max(oidA, oidB);
        return (oidMax == oidA ? a : b);
    }
    
    /**
     * Returns size of internal OID
     * 
     * @return size of internal OID
     */
    public int size() {
        return oid.size();
    }
    
    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FID) {
            FID that = (FID)obj; 
            return this.oid.equals(that.oid);
        }
        return false;
    }
    
    /**
     * Returns a hash code value for the object.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return oid.hashCode();
    }
    
    /**
     * Returns internal OID <code>toString()</code>
     * 
     * @return internal OID <code>toString()</code> 
     */
    public String getOIDtoString() {
        return oid.toString();
    } // getOIDtoString()
    
    /**
     * Returns internal OID + name string.
     * 
     * @return string in format "OID + name" convenient for logging. 
     */
    public String getFIDtoString() {
        return String.format("%s '%s'", oid.toString(), getName());
    } // getFIDtoString()
    
    
    /**
     * Returns a string representation of the object.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // Derived class may override
        return getFIDtoString();
    }
    
    /**
     * Implementing {@link Comparable} interface.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(FID that) {
        // implements Comparable
        return this.oid.compareTo(that.oid);
    }

    @Override
    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        name = (String)in.readObject();
        oid = new OID((int[])in.readObject());
    }

    @Override
    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        out.writeObject(name);
        out.writeObject(oid.getValue()); // as int[]
    }
    
} // class FID
