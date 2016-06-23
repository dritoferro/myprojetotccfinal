/*
 * File: PersistEntryTable.java
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
 * $Id: PersistEntryTable.java,v 1.16 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FID;
import org.friendlysnmp.FTable;
import org.friendlysnmp.ThrowableFormatter;
import org.friendlysnmp.ValueSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence storage entry class.
 * 
 * @version $Revision: 1.16 $
 */
public class PersistEntryTable extends PersistEntryNode {
    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(PersistEntryTable.class);

    /**
     * Generated UID. This UID should be changed only after 
     * serialized member variables modification. 
     * 
     * This UID is valid for serialized member variables: 
     * <br>(1) FColumn[] a_Column;
     * <br>(2) ValueSyntax[] a_Syntax;
     * <br>(3) PersistEntryTableRow[] a_Row;
     */
    private static final long serialVersionUID = -3534719879354645217L;
    
    private FColumn[] columnAll;
    private ValueSyntax[] syntaxAll;
    private PersistEntryTableRow[] rowAll;
    
    /**
     * Default ctor for deserialization
     */
    public PersistEntryTable() {
        super();
    }
    
    public PersistEntryTable(FTable table) throws FException {
        super(table);
        columnAll = table.getColumns();
        int size = columnAll.length;
        syntaxAll = new ValueSyntax[size];
        for (int i = 0;  i < size;  i++) {
            syntaxAll[i] = table.getColumnSyntax(columnAll[i]);
        }
        rowAll = new PersistEntryTableRow[table.getRowCount()];
        for (int r = 0;  r < rowAll.length;  r++) {
            FID idRow = table.getRowID(r);
            rowAll[r] = new PersistEntryTableRow(idRow.getOID(), columnAll.length);
            for (int c = 0;  c < columnAll.length;  c++) {
                rowAll[r].objAll[c] = table.getValueAt(idRow, columnAll[c]);
            }
        }
    }
    
    /**
     * Loading persistent value into {@link FTable}. 
     * The {@link FTable} removes this value from persistent storage
     * in case of value syntax mismatch.
     * 
     * @param table table object which loads value from persistent storage
     * @return flag true if the value was loaded
     * @throws FException attempt to load wrong table
     */
    public boolean loadPersist2Table(FTable table) {
        if (!id.equals(table.getFID())) {
            logger.error(String.format(
                "Cannot load table %s - mismatch FID %s", table, id));
            return false;
        }
        
        if (!table.isAccessibleForWrite()) {
            logger.error(String.format(
                "Failure to load persistent table %s (not read-write)", table));
            return false;
        }
        
        if (table.isVolatile()) {
            logger.error(String.format(
                "Failure to load persistent table %s (volatile)", 
                table.getFIDtoString()));
            return false;
        }
        
        int n1 = table.getColumnCount();
        int n2 = getColumnCount();
        if (n1 != n2) {
            logger.error(String.format(
                "Failure to load persistent table %s " +
                "(mismatch columns count: %d <-> %d)", 
                 table.getFIDtoString(), n1, n2));
            return false;
        }
        
        for (int c = 0;  c < getColumnCount();  c++) {
            if (!table.getColumn(c).equals(columnAll[c])) {
                logger.error(String.format(
                    "Failure to load persistent table %s " +
                    "(mismatch column-%s %s <-> %s)",
                    table.getFIDtoString(), c, table.getColumn(c), columnAll[c]));
                return false;
            }
            ValueSyntax syntax = table.getColumnSyntax(c); 
            if (!syntax.equals(syntaxAll[c])) {
                logger.error(String.format(
                     "Failure to load persistent table %s " +
                     "(mismatch column-%s syntax %s %s <-> %s %s)", 
                     table.getFIDtoString(), c, 
                     table.getColumn(c), syntax, columnAll[c], syntaxAll[c]));
                return false;
            }
        }

        // Ready to load table content after all validations
        try {
            table.deleteAll();
            for (int r = 0;  r < rowAll.length;  r++) {
                FID idRow = table.addRow(rowAll[r].getOID().getValue());
                for (int c = 0;  c < columnAll.length;  c++) {
                    table.setValueAt(rowAll[r].getValue(c), idRow, columnAll[c]);
                }
            }
            logger.debug("Loaded persistent table " + table.getFIDtoString());
            return true; // single valid return
        } catch (FException e) {
            logger.error(ThrowableFormatter.format(
                    "Failure to load persistent scalar " + table.getFIDtoString(), e)); 
            return false;
        }
    } // loadPersist2Table()

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PersistEntryTable) {
            PersistEntryTable that = (PersistEntryTable)obj;
            if (!this.id.equals(that.id)) {
                return false;
            }
            if (this.columnAll.length != that.columnAll.length) {
                return false;
            }
            for (int i = 0;  i < columnAll.length;  i++) {
                if (!this.columnAll[i].equals(that.columnAll[i])) {
                    return false;
                }
            }
            if (this.rowAll.length != that.rowAll.length) {
                return false;
            }
            for (int i = 0;  i < rowAll.length;  i++) {
                if (!this.rowAll[i].equals(that.rowAll[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public int getColumnCount() {
        return columnAll.length;
    }
    
    public FColumn getColumn(int index) {
        return columnAll[index];
    }
    
    public ValueSyntax getColumnSyntax(int index) {
        return syntaxAll[index];
    }
    
    public int getRowCount() {
        return rowAll.length;
    }
    
    public PersistEntryTableRow[] getRows() {
        return rowAll.clone();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(id);
        sb.append(" Rows=").append(rowAll.length);
        sb.append(" Cols=").append(columnAll.length);
        for (int i = 0;  i < columnAll.length;  i++) {
            sb.append("\n  Column-").append(i + 1).append(": ");
            sb.append(columnAll[i]);
            sb.append(" Syntax: ").append(syntaxAll[i]);
        }
        return sb.toString();
    }
    
    @Override
    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        super.readExternal(in);
        columnAll = (FColumn[])in.readObject();
        syntaxAll = (ValueSyntax[])in.readObject();
        rowAll = (PersistEntryTableRow[])in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        super.writeExternal(out);
        out.writeObject(columnAll);
        out.writeObject(syntaxAll);
        out.writeObject(rowAll);
    }
    
} // class PersistEntryTable
