/*
 * File: FColumn.java
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
 * $Id: FColumn.java,v 1.10 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.friendlysnmp.persist.PersistExternalizable;

public class FColumn extends PersistExternalizable {

    /**
     * Generated UID. This UID should be changed only after 
     * serialized member variables modification. 
     * 
     * This UID is valid for three serialized member variables: 
     * <br>(1) String name; 
     * <br>(2) int colIndex;
     * <br>(3) int colId;
     */
    private static final long serialVersionUID = 1400345900860319849L;

    /**
     * Name of the column.
     */
    private String name;
    
    /**
     * Column index. This is an absolute sequential index of the column
     * started from 0 to the last column of the table.
     * This value is used to access columns in the table object. 
     * <p>
     * This value is declared in the Java MIB file like
     * <code>public static final int idxSomeColumn</code>.
     */
    private int colIndex;
    
    /**
     * Column ID. This value is declared in a MIB file and used 
     * to generate OID of the column and cell. 
     * <p>
     * This value is declared in the Java MIB file like
     * <code>public static final int colSomeColumn</code>.
     */
    private int colId;

    public FColumn() {
        super(true);
    }
    
    /**
     * @param name column name as declared in MIB.
     * @param colIndex Column index - sequential index from 0 to the last column.
     * @param colId Column ID as declared in MIB.
     */
    public FColumn(String name, int colIndex, int colId) {
        super(false);
        this.name = name;
        this.colIndex = colIndex;
        this.colId = colId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getIndex_InTable() {
        return colIndex;
    }
    
    public int getId_InMIB() {
        return colId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FColumn) {
            FColumn that = (FColumn)obj;
            if (this.name.equals(that.name)  &&
                this.colIndex == that.colIndex  &&  this.colId == that.colId) 
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("FColumn: %s(Idx=%s Col=%d)", name, colIndex, colId);
    }

    @Override
    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        name = (String)in.readObject();
        colIndex = in.readInt();
        colId = in.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        out.writeObject(name);
        out.writeInt(colIndex);
        out.writeInt(colId);
    }
    
} // class FColumn
