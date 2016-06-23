/*
 * File: PersistEntryTableRow.java
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
 * $Id: PersistEntryTableRow.java,v 1.8 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.snmp4j.smi.OID;

/**
 * Persistence storage entry class.
 * 
 * @version $Revision: 1.8 $
 */
public class PersistEntryTableRow extends PersistExternalizable {
    /**
     * Generated UID. This UID should be changed only after 
     * serialized member variables modification. 
     * 
     * This UID is valid for serialized member variables: 
     * <br>(1) OID oid;
     * <br>(2) Object[] a_Obj;
     */
    private static final long serialVersionUID = -3181520600674342138L;
    
    private OID oid;
    Object[] objAll; // direct access from PersistEntryTable
    
    /**
     * Default ctor for deserialization
     */
    public PersistEntryTableRow() {
        super(true);
    }
    
    public PersistEntryTableRow(OID oid, int size) {
        super(false);
        this.oid = oid;
        objAll = new Object[size];
    }
    
    public OID getOID() {
        return new OID(oid);
    }
    
    public Object getValue(int index) {
        return objAll[index];
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PersistEntryTableRow) {
            PersistEntryTableRow that = (PersistEntryTableRow)obj;
            if (!this.oid.equals(that.oid)) {
                return false;
            }
            if (this.objAll.length != that.objAll.length) {
                return false;
            }
            for (int i = 0;  i < objAll.length;  i++) {
                if (this.objAll[i] == null) {
                    if (that.objAll[i] == null) {
                        continue;
                    }
                    return false;
                }
                if (this.objAll[i].equals(that.objAll[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        oid = new OID((int[])in.readObject());
        objAll = (Object[])in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        out.writeObject(oid.getValue()); // as int[]
        out.writeObject(objAll);
    }
    
} // class PersistEntryTableRow
