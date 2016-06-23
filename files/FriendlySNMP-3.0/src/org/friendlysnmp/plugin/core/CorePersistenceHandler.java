/*
 * File: CorePersistenceHandler.java
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
 * $Id: CorePersistenceHandler.java,v 1.16 2014/01/22 23:28:02 mg Exp $
 */
package org.friendlysnmp.plugin.core;

import org.friendlysnmp.FColumn;
import org.friendlysnmp.FConstant;
import org.friendlysnmp.FException;
import org.friendlysnmp.FHandler;
import org.friendlysnmp.FID;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.TableRowAction;
import org.friendlysnmp.ValueValidation;
import org.friendlysnmp.event.FTableGetListener;
import org.friendlysnmp.event.FTableSetListener;
import org.friendlysnmp.event.FTableValidationListener;
import org.friendlysnmp.mib.BaseMib;
import org.friendlysnmp.persist.PersistEntryScalar;
import org.friendlysnmp.persist.PersistEntryTable;
import org.friendlysnmp.persist.PersistEntryTableRow;
import org.friendlysnmp.persist.PersistStorage;
import org.friendlysnmp.plugin.core.FriendlySnmpMib.FriendlyRowKeepDeleteActionTC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

/**
 * The class provides application information to SNMP objects  
 * declared in FRIENDLY-SNMP-MIB.
 * 
 * @version $Revision: 1.16 $
 */
public class CorePersistenceHandler extends FHandler {
    /**
     * Logger object.
     */
    private static final Logger logger = LoggerFactory.getLogger(CorePersistenceHandler.class);
    
    /**
     * Persistence storage object. 
     */
    private PersistStorage persist;

    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.FHandler#init()
     */
    @Override
    public void init() {
        persist = agent.getPersistStorage();
    } // init()
    
    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.FHandler#registerMib(org.friendlysnmp.mib.BaseMib)
     */
    @Override
    public void registerMib(BaseMib mibBase) throws FException { 
        super.registerMib(mibBase);
        FriendlySnmpMibFriend mib = (FriendlySnmpMibFriend)mibBase;

        // Table with Scalars
        mib.getPersistScalarEntry().setVolatile(true);
        mib.getPersistScalarEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadPersistScalarsTable(table);
            }
        });
        mib.getPersistScalarEntry().addValidationListener(new FTableValidationListener() {
            @Override
            public ValueValidation validate(FTable table, Object objNewValue,
                    FID idRow, FColumn col, TableRowAction action) 
            {
                return validateRemovePersistScalarRow(
                        table, objNewValue, idRow, col, action);
            }
        });
        mib.getPersistScalarEntry().addSetListener(new FTableSetListener() {
            @Override
            public void set(FTable table, FID idRow, FColumn col, TableRowAction action) {
                removePersistScalarRow(table, idRow, col, action);
            }
        });
        
        // Tables with Tables
        mib.getPersistTableEntry().setVolatile(true);
        mib.getPersistTableEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadPersistTablesTable(table);
            }
        });
        mib.getPersistTableEntry().addValidationListener(new FTableValidationListener() {
            @Override
            public ValueValidation validate(FTable table, Object objNewValue,
                    FID idRow, FColumn col, TableRowAction action) 
            {
                return validateRemovePersistTableRow(
                        table, objNewValue, idRow, col, action);
            }
        });
        mib.getPersistTableEntry().addSetListener(new FTableSetListener() {
            @Override
            public void set(FTable table, FID idRow, FColumn col, TableRowAction action) {
                removePersistTableRow(table, idRow, col, action);
            }
        });

        // Table with Cells
        mib.getPersistCellEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadPersistCellsTable(table);
            }
        });
    } // registerMib()
    
    /**
     * Loads persistent scalars table.
     */
    private void loadPersistScalarsTable(FTable table) {
        try {
            table.deleteAll();
            PersistEntryScalar[] a_PS = persist.getPersistScalars();
            for (PersistEntryScalar entry : a_PS) {
                FID idRow = table.addRowNext();
                table.setValueAt(entry.getFID().getName(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarName);
                table.setValueAt(entry.getFID().getOIDtoString(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarOID);
                table.setValueAt(entry.getScalarSyntax().toString(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarSyntax);
                table.setValueAt(entry.getScalarValue().toString(), 
                    idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarValue);
                table.setValueAt(FriendlyRowKeepDeleteActionTC.keep, 
                    idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarAction);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadPersistScalarsTable()

    /**
     * Validates action on scalars persistency table.
     * 
     * @param table persistency table.
     * @param objNewValue new value.
     * @param idRow row index.
     * @param indexCol column index (expected persistScalarAction only).
     * @param action action (expected CHANGE only).
     * @return failed validation: (1) remove persistent value
     * for boots counter reset or (2)KEEP action is requested.
     */
    private ValueValidation validateRemovePersistScalarRow(
            FTable table, Object objNewValue,
            FID idRow, FColumn col, TableRowAction action) 
    {
        if (action == TableRowAction.ROW_CHANGE  &&
            col.equals(FriendlySnmpMibFriend.COLUMN_PersistScalarAction)) 
        {
            try {
                if (Integer.valueOf(FriendlyRowKeepDeleteActionTC.keep).equals(objNewValue)) {
                    return ValueValidation.INCONSISTENT_VALUE; 
                }
                if (Integer.valueOf(FriendlyRowKeepDeleteActionTC.delete).equals(objNewValue)) {
                    Object objOID = table.getValueAt(
                              idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarOID);
                    OID oidEgineBoots = agent.getSnmpEngineBootsFID().getOID();
                    OID oid = new OID((String)objOID);
                    if (oidEgineBoots.equals(oid)  &&  
                        !agent.getConfig().getConfigBoolean(FConstant.KEY_ALLOW_BOOTS_COUNT_RESET))
                    {
                        return ValueValidation.READ_ONLY; 
                    }
                }
            } catch (FException e) {
                mibBase.exceptionThrown(table, e);
            }
        }
        return ValueValidation.SUCCESS;
    } // validateRemovePersistScalarRow()
    
    /**
     * Removes scalar in the persistency storage
     * 
     * @param table persistency table
     * @param idRow row index
     * @param indexCol column index (expected persistScalarAction only)
     * @param action action (expected CHANGE only)
     */
    private void removePersistScalarRow(FTable table, FID idRow, FColumn col, TableRowAction action) {
        if (action == TableRowAction.ROW_CHANGE  &&
            col.equals(FriendlySnmpMibFriend.COLUMN_PersistScalarAction)) 
        {
            try {
                Object obj = table.getValueAt(
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarAction);
                if (Integer.valueOf(FriendlyRowKeepDeleteActionTC.delete).equals(obj)) {
                    Object objOID = table.getValueAt(
                            idRow, FriendlySnmpMibFriend.COLUMN_PersistScalarOID);
                    OID oid = new OID((String)objOID);
                    FID id = new FID(oid);
                    persist.remove(id);
                    FScalar node = agent.getScalar(oid);
                    if (node != null) {
                        node.fireRestoreDefaultEvent();
                    }
                }
            } catch (FException e) {
                mibBase.exceptionThrown(table, e);
            }
        }
    } // removePersistScalarRow()   
    
    /**
     * Loads persistent tables table
     */
    private void loadPersistTablesTable(FTable table) {
        try {
            table.deleteAll();
            PersistEntryTable[] ptAll = persist.getPersistTables();
            if (logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (PersistEntryTable entry : ptAll) {
                    sb.append("\nLoading ").append(entry);
                }
                logger.debug(sb.toString());
            }
            for (PersistEntryTable entry : ptAll) {
                FID idRow = table.addRowNext();
                table.setValueAt(entry.getFID().getName(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistTableName);
                table.setValueAt(entry.getFID().getOIDtoString(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistTableOID);
                table.setValueAt(entry.getColumnCount(), 
                    idRow, FriendlySnmpMibFriend.COLUMN_PersistTableColumnCount);
                table.setValueAt(entry.getRowCount(), 
                    idRow, FriendlySnmpMibFriend.COLUMN_PersistTableRowCount);
                table.setValueAt(FriendlyRowKeepDeleteActionTC.keep, 
                    idRow, FriendlySnmpMibFriend.COLUMN_PersistTableAction);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadPersistTablesTable()
    
    /**
     * Validates action on tables persistency table.
     * 
     * @param table persistency table.
     * @param objNewValue new value.
     * @param idRow row index.
     * @param indexCol column index (expected persistScalarAction only).
     * @param action action (expected CHANGE only).
     * @return failed validation: KEEP action is requested.
     */
    private ValueValidation validateRemovePersistTableRow(
            FTable table, Object objNewValue,
            FID idRow, FColumn col, TableRowAction action) 
    {
        if (action == TableRowAction.ROW_CHANGE  &&
            col.equals(FriendlySnmpMibFriend.COLUMN_PersistTableAction)) 
        {
            if (Integer.valueOf(FriendlyRowKeepDeleteActionTC.keep).equals(objNewValue)) {
                return ValueValidation.INCONSISTENT_VALUE; 
            }
        }
        return ValueValidation.SUCCESS;
    } // validateRemovePersistTableRow()
    
    /**
     * Removes table in the persistency storage
     * 
     * @param table persistency table
     * @param idRow row index
     * @param indexCol column index (expected persistTableAction only)
     * @param action action (expected CHANGE only)
     */
    private void removePersistTableRow(FTable table, FID idRow, FColumn col, TableRowAction action) {
        if (action == TableRowAction.ROW_CHANGE  && 
            col.equals(FriendlySnmpMibFriend.COLUMN_PersistTableAction)) 
        {
            try {
                Object objOID = table.getValueAt(
                        idRow, FriendlySnmpMibFriend.COLUMN_PersistTableOID);
                OID oid = new OID((String)objOID);
                FID id = new FID(oid);
                persist.remove(id);
                FTable node = agent.getTable(oid);
                if (node != null) {
                    node.fireRestoreDefaultEvent();
                }
            } catch (FException e) {
                mibBase.exceptionThrown(table, e);
            }
        }
    } // removePersistTableRow()   
    
    /**
     * Loads persistent cells table
     */
    private void loadPersistCellsTable(FTable table) {
        try {
            table.deleteAll();
            PersistEntryTable[] ptAll = persist.getPersistTables();
            for (PersistEntryTable entry : ptAll) {
                PersistEntryTableRow[] a_Row = entry.getRows();
                int columnCount = entry.getColumnCount();
                
                for (int r = 0;  r < a_Row.length;  r++) {
                    for (int c = 0;  c < columnCount;  c++) {
                        FID idRow = table.addRowNext();
                        table.setValueAt(entry.getFID().getName(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellTableName);
                        table.setValueAt(entry.getFID().getOIDtoString(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellTableOID);
                        table.setValueAt(a_Row[r].getOID().toString(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellRowOID);
                        FColumn fc = entry.getColumn(c);
                        table.setValueAt(fc.getName(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellColumnName);
                        table.setValueAt(fc.getId_InMIB(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellColumnOID);
                        table.setValueAt(fc.getIndex_InTable(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellColumnIndex);
                        table.setValueAt(entry.getColumnSyntax(c).toString(), 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellColumnSyntax);
                        Object obj = a_Row[r].getValue(c);
                        table.setValueAt(obj == null ? "<null>" : obj, 
                                idRow, FriendlySnmpMibFriend.COLUMN_PersistCellValue);
                    }
                }
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadPersistCellsTable()
    
} // class CorePersistenceHandler
