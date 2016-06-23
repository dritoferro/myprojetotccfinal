/*
 * File: FTable.java
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
 * $Id: FTable.java,v 1.33 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.friendlysnmp.event.FTableGetListener;
import org.friendlysnmp.event.FTableSetListener;
import org.friendlysnmp.event.FTableValidationListener;
import org.friendlysnmp.persist.PersistEntryTable;
import org.friendlysnmp.persist.PersistStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.MOServerLookupEvent;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOChangeEvent;
import org.snmp4j.agent.mo.MOChangeListener;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOMutableColumn;
import org.snmp4j.agent.mo.MOMutableTableModel;
import org.snmp4j.agent.mo.MOMutableTableRow;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.agent.mo.MOTableModel;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.RowStatusEvent;
import org.snmp4j.agent.mo.snmp.RowStatusListener;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

/**
 * This class hides access to <code>org.snmp4j.agent.mo.MOTable</code> 
 * <br>
 * The objects of this class are generated by FriendlyPro tool. 
 * 
 * @version $Revision: 1.33 $
 */
public class FTable extends FValueNode {
    /**
     * Logger object
     */
    private static final Logger logger = LoggerFactory.getLogger(FTable.class);
    
    /**
     * Underlying SNMP4j managed object.
     */
    private MOTable moTable;
    
    /**
     * Underlying SNMP4j managed object table model.
     */
    private MOTableModel moTableModel;
    
    /**
     * Volatile flag of the table.
     */
    private boolean volatileTable;
    
    /**
     * Columns in the table.
     */
    private FColumn[] columnAll;
    
    /**
     * Last GET request was received at this timestamp. 
     */
    private long lastGetTime;
    
    /**
     * Table GET listeners.
     */
    private Set<FTableGetListener> hsGetListener;
    
    /**
     * Collection of table SET listeners. 
     */
    private Set<FTableSetListener> hsSetListener;
    
    /**
     * Collection of table VALIDATION listeners. 
     */
    private Set<FTableValidationListener> hsValidationListener;
    
    /**
     * RowStatus column or null if column of this type does not exist.
     */
    private FColumn colRowStatus;

    /**
     * Collection with valid columns.
     */
    private Set<FColumn> hsValidColumns;
    
    //--------------------------------separator--------------------------------
    static int ______SYSTEM;

    /**
     * Constructor.
     * 
     * @param name node name as it is declared in a MIB file
     * @param moTable table managed object
     */
    public FTable(String name, MOTable moTable, AgentWorker agent, 
            FColumn ... column)
    {
        super(name, moTable.getOID());
        this.moTable = moTable;
        this.columnAll = column;
        setVolatilePrivate(false);
        moTable.addMOChangeListener(new ThisMOChangeListener());
        moTableModel = moTable.getModel();
        hsGetListener = new CopyOnWriteArraySet<FTableGetListener>();
        hsSetListener = new CopyOnWriteArraySet<FTableSetListener>();
        hsValidationListener = new CopyOnWriteArraySet<FTableValidationListener>();
        hsValidColumns = new HashSet<FColumn>(moTable.getColumnCount());
        
        // Initialize columns with default values and RowStatus listener
        // implemented in private inner class. 
        // The MOTable.addMOTableRowListener(..) listener 
        // org.snmp4j.agent.mo.MOTableRowListener cannot be used because it
        // receives all SET events coming from (1)application code and 
        // (2)SET events from MIB manager. Only SET events from MIB manager 
        // should be processed for validation and data modification.
        // 
        // This initialization is redundant for tables without RowStatus column.
        // Populating table on GET request requires rows creation. 
        // If RowStatus column default is not set i.e. the value is 'null',  
        // the method RowStatus.rowChange() put veto on row creation: 
        //    event.setVetoStatus(PDU.inconsistentName);
        try {
            for (int i = 0;  i < getColumnCount();  i++) {
                MOColumn moColumn = moTable.getColumn(i);
                if (moColumn instanceof RowStatus) {
                    colRowStatus = columnAll[i];
                    RowStatus rs = (RowStatus)moColumn;
                    rs.setDefaultValue(FConverter.toVariable(RowStatus.active, moColumn));
                    rs.addRowStatusListener(new ThisRowStatusListener());
                } else {
                    int syntax = moColumn.getSyntax();
                    setDefaultValue(FConverter.defaultJavaValue(syntax), i);
                }
            }
        } catch (FException e) {
            agent.exceptionThrown(this, e);
        }
        setAgent(agent); // hook up GET event to the table
        logger.debug("Created table: " + getName());
    } // FTable()
    
    @Override
    public void setAgent(AgentWorker agent) {
        super.setAgent(agent);
        ServerLookupAdapter listener = new ServerLookupAdapter() {
            @Override
            public void queryEvent(MOServerLookupEvent event) {
                fireGetEvent();
            }
        };
        agent.getServer().addLookupListener(listener, moTable);
    } // setAgent()
    
    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.FValueNode#setVolatilePrivate(boolean)
     */
    @Override
    protected void setVolatilePrivate(boolean volatileTable) {
        this.volatileTable = volatileTable;
    } // setVolatilePrivate()

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.FValueNode#isVolatile()
     */
    @Override
    public boolean isVolatile() {
        return volatileTable;
    }
    
    public boolean isAccessibleForWrite() {
        if (moTableModel instanceof MOMutableTableModel) {
            // The MOMutableTableModel defines the interface for mutable
            // table models. Mutable table models support row creation 
            // and deletion through SNMP SET operations.
            return true;
        }
        for (int i = 0;  i < getColumnCount();  i++) {
            MOColumn moColumn = moTable.getColumn(i);
            if (moColumn instanceof MOMutableColumn) {
                // Objects represented by MOMutableColumn can be modified  
                // via SNMP SET operations.
                return true;
            }
        }        
        return false;
    }
    
    /**
     * Returns SNMP4J underlying managed object.
     * 
     * @return the <code>org.snmp4j.agent.mo.MOTable</code> object this class
     * is representing.
     */
    public MOTable getMOTable() {
        return moTable;
    } // getMOTable()

    //--------------------------------separator--------------------------------
    static int ______ROWS;

    /**
     * Returns rows count.
     * 
     * @return rows count
     */
    public int getRowCount() {
        return moTableModel.getRowCount();
    } // getRowCount()
    
    /**
     * Parses cell ID to find row ID in it.
     * 
     * @param oidCell cell OID
     * @return row ID
     */
    private FID getRowID(OID oidCell) {
        // See getColumn(OID) comments for oidCell structure.
        int[] cellAll = oidCell.getValue();// returns without clone() !!!
        int offset = getOIDSize() + 1;
        int size = cellAll.length - offset;
        return new FID(new OID(cellAll, offset, size));
    } // getRowID()
    
    /**
     * Finds row ID by row index.
     * 
     * @param indexRow row index
     * @return row ID
     * @throws FException
     */
    public FID getRowID(int indexRow) throws FException {
        FID[] idAll = getRowIDs();
        try {
            return idAll[indexRow];
        } catch (Exception e) {
            throwFException("Wrong index " + indexRow
                    + " (bounds: 0.." + (idAll.length - 1) + ")", e);
            return null; // to keep compiler happy
        }
    } // getRowID()
    
    /**
     * Returns array of rowIDs.
     * 
     * @return array of rowIDs
     */
    private FID[] getRowIDs() {
        List<FID> lst = new ArrayList<FID>();
        Iterator<?> itRow = moTableModel.iterator();
        while (itRow.hasNext()) {
            MOTableRow moRow = (MOTableRow)itRow.next();
            OID oid = moRow.getIndex();
            lst.add(new FID(oid));
        }
        return lst.toArray(new FID[lst.size()]);
    } // getRowIDs()
    
    /**
     * Finds row index specified by row ID. Returns -1 if row does not exist.
     * 
     * @param idRow row ID
     * @return index of specified by row ID.
     */
    public int getRowIndex(FID idRow) {
        FID[] idAll = getRowIDs();
        for (int i = 0;  i < idAll.length;  i++) {
            if (idRow.equals(idAll[i])) {
                return i;
            }
        }
        return -1;
    } // getRowIndex()
    
    /**
     * Returns SNMP4J table row object.
     * 
     * @param idRow row ID.
     * @return SNMP4J table row object.
     * @throws FException
     */
    private MOTableRow getMORow(FID idRow) throws FException {
        OID oidRow = idRow.getOID();
        Iterator<?> itRow = moTableModel.iterator();
        while (itRow.hasNext()) {
            MOTableRow moRow = (MOTableRow)itRow.next();
            OID oid = moRow.getIndex();
            if (oidRow.equals(oid)) {
                return moRow;
            }
        }
        throwFException("Not valid row ID: " + idRow);
        return null; // to keep compiler happy
    } // getMORow()
    
    /**
     * Row ID is generated by increasing the max first integer and 
     * setting others (if exist) to 1.
     * 
     * @return row ID for newly created row
     * @throws FException
     */
    public FID addRowNext() throws FException {
        FID[] idAll = getRowIDs();
        FID idMax = null;
        for (FID id : idAll) {
            if (idMax == null) {
                idMax = id;
            } else {
                idMax = FID.max(idMax, id);
            }
            
        }
        if (idMax == null) {
            return addRow(1);
        }
        FID oidNext = idMax.next();
        return addRow(oidNext.getInt());
    } // addRowNext()    

    /**
     * Adds row with specified row ID.
     * 
     * @param id list of row integer indices.
     * @return row ID of newly created row (the same as parameter).
     * @throws FException
     */
    public FID addRow(int ... id) throws FException {
        // Parameter "id" is int[]
        if (id.length == 0) {
            throwFException("Cannot create FID for 0-length IDs array.");
        }
        return addRow(new FID(id));
    }    
    
    /**
     * Adds row with specified row ID
     * 
     * @param id list of row integer indices
     * @return row ID of newly created row (the same as parameter)
     * @throws FException
     */
    public FID addRow(FID id) throws FException {
        FID[] idAll = getRowIDs();
        for (FID idRow : idAll) {
            if (id.equals(idRow)) {
                throwFException(String.format(
                        "Cannot add row. Row ID %s already exist.", idRow));
            }
        }
        MOTableRow moRow = moTable.createRow(id.getOID());
        if (moRow == null) {
            throwFException(String.format("Row %s cannot be created.", id));
        }
        boolean added = moTable.addRow(moRow);
        if (!added) {
            throwFException(String.format("Row %s cannot be added.", id)); 
        }
        logger.debug(String.format("Added row: %s for %s", id, getName()));
        return id;
    } // addRow()

    /**
     * Deletes row from managed objects table.
     * 
     * @param idRow row ID (not row index!) to delete from managed objects table
     * @return row ID of suggested new row selection
     * @throws FException if the row cannot be found or cannot be removed
     */
    public FID deleteRow(FID idRow) throws FException {
        logger.debug("Deleting row " + idRow + " in " + getName());
        FID[] idAll = getRowIDs();
        MOTableRow moRow = getMORow(idRow);
        OID oid = moRow.getIndex(); // int[1] = { 5 }
        MOTableRow rowDeleted = moTable.removeRow(oid);
        if (rowDeleted == null) {
            throwFException(String.format("Row %s cannot be deleted", idRow));
        }
        //logger.debug("Deleted row " + idRow + " in " + getName() + ", OID=" + oid);
        int size = idAll.length;
        if (size <= 1) {
            return null;
        }
        int indexDel = -1;
        for (int i = 0;  i < size;  i++) {
            if (idRow.equals(idAll[i])) {
                indexDel = i; // found just deleted row ID
                break;
            }
        }
        indexDel++;
        if (indexDel >= size) {
            indexDel = 0;
        }
        return idAll[indexDel];
    } // deleteRow()
    
    /**
     * Deletes all rows.
     * 
     * @throws FException
     */
    public void deleteAll() throws FException {
        logger.debug("Deleting ALL rows");
        FID[] idAll = getRowIDs();
        for (FID idRow : idAll) {
            MOTableRow rowDeleted = moTable.removeRow(idRow.getOID());
            if (rowDeleted == null) {
                throwFException(String.format("Row %s cannot be deleted", idRow));
            }
        }
    } // deleteAll()

    //--------------------------------separator--------------------------------
    static int ______COLUMNS;

    /** 
     * Returns number of columns.
     *  
     * @return number of columns. 
     */
    public int getColumnCount() { 
        return moTable.getColumnCount() ; 
    } // getColumnCount()
    
    public FColumn[] getColumns() {
        return columnAll.clone();
    }
    
    public FColumn getColumn(int index) {
        return columnAll[index];
    }
    
    /**
     * Parses cell OID to find column.
     * 
     * @param oidCell cell id
     * @return column index
     */
    private FColumn getColumn(OID oidCell) {
        // oidCell == oidTable.columnID.oidRow
        //
        // oidCell : 1.3.6.1.4.1.29091.1.1.2.3.1.3.4.5.6
        //           <-------- oidTable -------> | <--->
        //                          Column ID ---+   |
        //                             oidRow -------+ (index1.index2.index3)
        int[] cellAll = oidCell.getValue(); // returns without clone() !!!
        // Column ID is an integer in oidCell located just after this table OID.
        // The size of this table OID is returned by getOIDSize().
        int columnId = cellAll[getOIDSize()];
        for (FColumn col : columnAll) {
            if (columnId == col.getId_InMIB()) {
                return col;
            }
        }
        return null;
    } // getColumn()
    
    public ValueSyntax getColumnSyntax(int index) {
        return getColumnSyntax(columnAll[index]);
    }
    
    public ValueSyntax getColumnSyntax(FColumn c) {
        int syntax = moTable.getColumn(c.getIndex_InTable()).getSyntax();
        return ValueSyntax.find(syntax);
    }
    
    private void checkColumnValidity(FColumn col) throws FException {
        if (hsValidColumns.contains(col)) {
            return;
        }
        for (FColumn c : columnAll) {
            if (c.equals(col)) {
                int index = c.getIndex_InTable();
                if (index < 0  ||  index >= columnAll.length) {
                    throwFException(String.format(
                        "Column %s is out of range 0..%d", 
                        c, (columnAll.length - 1)));
                }
                hsValidColumns.add(col);
                return;
            }
        }
        throwFException(String.format("The column %s is not valid", col));
    } // checkColumnValidity()
    
    //--------------------------------separator--------------------------------
    static int ______CELL_VALUES;

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.FValueNode#loadPersistValue()
     */
    @Override
    protected void loadPersistValue() {
        PersistStorage storage = getPersistStorage();
        PersistEntryTable entry = storage.getPersistTable(getFID());
        if (entry != null) {
            try {
                persistLoaded = entry.loadPersist2Table(this); // does not throw
                if (!persistLoaded) {
                    storage.remove(entry.getFID()); // throws
                }
            } catch (FException e) {
                logger.error(ThrowableFormatter.format(
                        "Failure to load persistent table " + getFID(), e));
            }
        }
    } // loadPersistValue()

    /**
     * Sets default cell values for a new row. These values are used for new
     * rows. Default values make sense and is actually set only for read-write 
     * columns.
     * 
     * @param obj comma separated list of values
     * @throws FException
     */
    public void setDefaultValues(Object... obj) throws FException {
        int count = getColumnCount();
        for (int i = 0;  i < count;  i++) {
            if (i < obj.length) {
                setDefaultValue(obj[i], i);
            }
        }
    } // setDefaultValues()

    /**
     * Sets default value for the specified column. This value will be used
     * when a new row is created. Default values make sense and is actually set 
     * only for read-write columns.
     * 
     * @param obj default value
     * @param indexCol column index
     * @throws FException
     */
    private void setDefaultValue(Object obj, int indexCol) throws FException {
        MOColumn moColumn = moTable.getColumn(indexCol);
        if (moColumn instanceof MOMutableColumn) {
            Variable var = FConverter.toVariable(obj, moColumn);
            ((MOMutableColumn)moColumn).setDefaultValue(var);
        }
    } // setDefaultValue()
    
    /** 
     * Checks the editable flag for the specified cell.
     *  
     * @param idRow the row index 
     * @param col column 
     * @return boolean value true if cell is editable
     * @throws FException
     */
    public boolean isCellEditable(FID idRow, FColumn col) throws FException { 
        checkColumnValidity(col);
        MOColumn moColumn = moTable.getColumn(col.getIndex_InTable()); 
        if (!moColumn.getAccess().isAccessibleForWrite()) { 
            return false; 
        }
        MOTableRow moRow = getMORow(idRow); 
        return (moRow instanceof MOMutableTableRow); 
    } // isCellEditable()
    
    /**
     * Gets the value of specified cell.
     * 
     * @param idRow ID of the row (this is not an row index!)
     * @param col column
     * @return requested value of the cell
     * @throws FException for not valid idRow or column parameter.
     */
    public Object getValueAt(FID idRow, FColumn col) throws FException {
        checkColumnValidity(col);
        MOTableRow moRow = getMORow(idRow); 
        Variable var = moRow.getValue(col.getIndex_InTable());
        Object obj = FConverter.toJava(var);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("RowID=%s %s Value: '%s'", idRow, col, obj));
        }
        return obj;
    } // getValueAt()
    
    /**
     * Sets the value for the specified cell.
     * 
     * @param obj new value, which is set to the cell.
     * @param idRow ID of the row (this is not a row index!).
     * @param col column.
     * @throws FException for not valid idRow or col parameter or 
     * while object conversion. 
     */
    public void setValueAt(Object obj, FID idRow, FColumn col) throws FException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "RowID=%s %s Value: '%s' %s", 
                    idRow, col, obj, (obj == null ? "" : obj.getClass())));
        }
        checkColumnValidity(col);
        int indexCol = col.getIndex_InTable();
        MOColumn moColumn = moTable.getColumn(indexCol); 
        try {
            Variable var = FConverter.toVariable(obj, moColumn); 
            MOTableRow moRow = getMORow(idRow); 
            ((MOMutableTableRow)moRow).setValue(indexCol, var); 
        } catch (Exception e) {
            // ClassCastException, FException
            throwFException(String.format(
                    "Failure to set value '%s' for R=%s %s", obj, idRow, col), e);
        } 
    } // setValueAt()
    
    //--------------------------------separator--------------------------------
    static int ______EVENTS;

    public void addGetListener(FTableGetListener l) {
        hsGetListener.add(l);
    } // addGetListener()
    
    public void removeGetListener(FTableGetListener l) {
        hsGetListener.remove(l);
    } // removeGetListener()
    
    /**
     * Registers the given observer to begin receiving "changed value" events
     * when changes are made to the managed object.
     *
     * @param l the observer to register
     */
    public void addSetListener(FTableSetListener l) {
        hsSetListener.add(l);
    } // addSetListener()
    
    /**
     * Unregisters the given observer from the notification list so it will 
     * no longer receive change updates.
     * 
     * @param l the observer to unregister
     */
    public void removeSetListener(FTableSetListener l) {
        hsSetListener.remove(l);
    } // removeSetListener()
    
    /**
     * Registers the given observer to begin receiving "validation" events
     * when changes are made to the managed object.
     *
     * @param l the observer to register
     */
    public void addValidationListener(FTableValidationListener l) {
        hsValidationListener.add(l);
    } // addValidationListener()
    
    /**
     * Unregisters the given observer to stop receiving "validation" events
     * when changes are made to the managed object.
     *
     * @param l the observer to register
     */
    public void removeValidationListener(FTableValidationListener l) {
        hsValidationListener.remove(l);
    } // removeValidationListener()

    /**
     * Fires GET event
     */
    public synchronized void fireGetEvent() {
        long now = System.currentTimeMillis();
        long ignoreUpdate = agent.getIgnoreUpdateMs();
        if (now - lastGetTime > ignoreUpdate  &&  hsGetListener.size() > 0) {
            lastGetTime = now;
            logger.debug("FIRE GET request for " + getName());
            for (FTableGetListener l : hsGetListener) {
                l.get(this);
            }
        }
    } // fireGetEvent()
    
    /**
     * Fires SET event
     * 
     * @param oidCell cell ID
     * @param action action
     */
    private synchronized void fireSetEvent(OID oidCell, TableRowAction action) {
        if (logger.isDebugEnabled()) {
            logger.debug("FIRE SET event for " + this);
        }
        // 1. Fire "set" event to registered listeners
        if (hsSetListener.size() > 0) {
            FColumn col = getColumn(oidCell);
            FID idRow = getRowID(oidCell);
            for (FTableSetListener l : hsSetListener) {
                l.set(this, idRow, col, action);
            }
        }
        // 2. Persistence
        if (!volatileTable) {
            try {
                agent.getPersistStorage().put(this);
                if (logger.isDebugEnabled()) {
                    logger.debug("Table: " + this);
                }
            } catch (FException e) {
                agent.exceptionThrown("Failure to persist table " + getFIDtoString(), e);
            }
        }
    } // fireSetEvent()

    /**
     * Fires validation event.
     * 
     * @param oidCell cell OID
     * @param objNewValue new value
     * @param action action
     * @return validation result
     */
    private synchronized ValueValidation fireValidationEvent( 
            OID oidCell, Object objNewValue, TableRowAction action) 
    {
        FColumn col = getColumn(oidCell);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "Validation: CellOID=%s Action=%s NewValue=%s Table=%s %s",
                    oidCell, action, objNewValue, getFIDtoString(), col
            ));
        }
        FID idRow = getRowID(oidCell);
        int indexRow = getRowIndex(idRow); // -1 for not existent row
        switch (action) {
            case ROW_CHANGE:
                // TODO MIB Explorer has a nasty "feature". 
                // New row with multiple new cell values + RowStatus.CREATE 
                // generates events in a wrong order. For example, new row
                // with new values in Cell-1, Cell-2, Cell-RowStatus generates 
                // events:
                //   - Event-1 to change Cell-1.
                //   - Event-2 to change Cell-2.
                //   - Event-3 to create new row.
                // CHANGE events 1 and 2 cannot be applied to a new _future_ row
                // and these cell values are lost. Also, validator may fail
                // for not existent row.
                // At the same time these CHANGE events cannot be rejected 
                // because event-3 will fail.
                // As a result have to comment out the following:
                //if (indexRow < 0) {
                //    return ValueValidation.NO_SUCH_NAME;
                //}
                break;
            case ROW_DELETE:
                if (indexRow < 0) {
                    return ValueValidation.NO_SUCH_NAME;
                }
                break;
            case ROW_CREATE:
                break;
        }
        
        for (FTableValidationListener l : hsValidationListener) {
            ValueValidation validation = l.validate(this, 
                    objNewValue, idRow, col, action);
            if (validation != ValueValidation.SUCCESS) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format(
                      "Validation failed: %s OID=%s Action=%s NewValue=%s Table=%s", 
                      validation, oidCell, action, objNewValue, getFIDtoString()));
                }
                return validation;
            }
        }
        return ValueValidation.SUCCESS;
    } // fireValidationEvent()    
    
    /**
     * Implementation of <code>RowStatusListener</code>
     */
    private class ThisRowStatusListener implements RowStatusListener {
        /**
         * Implementation of <code>RowStatusListener</code>
         * @param e row status event
         */
        public void rowStatusChanged(RowStatusEvent e) {
            // 'Change' status is handled in ThisMOChangeListener
            // Make oidCell:
            OID oidCell = getFID().getOID(); // basic OID of the table
            oidCell.append(colRowStatus.getId_InMIB()); // + column ID
            oidCell.append(e.getRow().getIndex());      // + row index
            // See getColumn() comments for oidCell structure.
            if (e.isDeniable()) {
                // This is 'prepare' stage.
                ValueValidation validation = ValueValidation.SUCCESS;
                switch (e.getNewStatus()) {
                    case RowStatus.notExistant: // 0
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                               "Validation Event 'notExistant' for row oid %s in table %s", 
                               oidCell, getFIDtoString()));
                        }
                        validation = ValueValidation.WRONG_VALUE;
                        break;
                    case RowStatus.active: // 1
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "Validation Event 'active' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        validation = ValueValidation.SUCCESS;
                        break;
                    case RowStatus.notInService: // 2
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "Validation Event 'notInService' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        validation = ValueValidation.WRONG_VALUE;
                        break;
                    case RowStatus.notReady: // 3
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "Validation Event 'notReady' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        validation = ValueValidation.WRONG_VALUE;
                        break;
                    case RowStatus.createAndGo: // 4
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "Validation Event 'createAndGo' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        validation = fireValidationEvent(oidCell, null, TableRowAction.ROW_CREATE);
                        break;
                    case RowStatus.createAndWait: // 5
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "Validation Event 'createAndWait' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        validation = ValueValidation.WRONG_VALUE;
                        break;
                    case RowStatus.destroy: // 6
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "Validation Event 'destroy' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        validation = fireValidationEvent(oidCell, null, TableRowAction.ROW_DELETE);
                        break;
                }
                e.setDenyReason(validation.toInt());
            } else {
                // This is 'commited' stage.
                switch (e.getNewStatus()) {
                    // Only supported "case" entries:
                    case RowStatus.createAndGo:
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "SET Event 'createAndGo' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        fireSetEvent(oidCell, TableRowAction.ROW_CREATE);
                        break;
                    case RowStatus.destroy:
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                "SET Event 'destroy' for row oid %s in table %s", 
                                oidCell, getFIDtoString()));
                        }
                        fireSetEvent(oidCell, TableRowAction.ROW_DELETE);
                        break;
                }
            }
        }
    } // inner class ThisRowStatusListener
    
    private class ThisMOChangeListener implements MOChangeListener {
        
        /**
         * Returns OID of the modified cell or null for RowStatus updates. 
         * @param e change event
         * @return OID of the modified cell
         */
        private OID getModifiedCell(MOChangeEvent e) {
            ManagedObject mo = e.getChangedObject();
            MOScope scope = mo.getScope();
            OID oidCell = mo.find(scope);
            FColumn col = getColumn(oidCell);
            if (col == null) {
                return null; // should never happen
            }
            MOColumn moColumn = moTable.getColumn(col.getIndex_InTable());
            return (moColumn instanceof RowStatus ? null : oidCell); 
        }        
        
        @Override
        public void beforePrepareMOChange(MOChangeEvent e) {
            OID oidCell = getModifiedCell(e);
            if (oidCell != null) {
                Variable varNew = e.getNewValue();
                Object objNew = FConverter.toJava(varNew);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format(
                        "Event CHANGE 'beforeMOChange': %s; new value: %s", 
                        oidCell, objNew));
                }
                ValueValidation v = fireValidationEvent(oidCell, objNew, TableRowAction.ROW_CHANGE);
                e.setDenyReason(v.toInt());
            }
        }
        
        @Override
        public void afterPrepareMOChange(MOChangeEvent e) { }
        
        @Override
        public void beforeMOChange(MOChangeEvent e) {
            // Frank: Too late to deny "e" in beforeMOChange(). 
            //        It's correct to deny "e" in beforePrepareMOChange() 
        }
        
        @Override
        public void afterMOChange(MOChangeEvent e) {
            OID oidCell = getModifiedCell(e);
            if (oidCell != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Event CHANGE 'afterMOChange': " + oidCell);
                }
                fireSetEvent(oidCell, TableRowAction.ROW_CHANGE);
            }
        }
    } // inner class ThisMOChangeListener

} // class FTable
