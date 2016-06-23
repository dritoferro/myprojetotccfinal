/*
 * File: CoreExceptionsHandler.java
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
 * $Id: CoreExceptionsHandler.java,v 1.20 2014/01/22 23:28:02 mg Exp $
 */
package org.friendlysnmp.plugin.core;

import java.util.ArrayList;
import java.util.List;

import org.friendlysnmp.AgentWorker.ExceptionType;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FHandler;
import org.friendlysnmp.FID;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.TableRowAction;
import org.friendlysnmp.ThrowableFormatter;
import org.friendlysnmp.ValueValidation;
import org.friendlysnmp.event.FRestoreDefaultEvent;
import org.friendlysnmp.event.FRestoreDefaultListener;
import org.friendlysnmp.event.FScalarGetListener;
import org.friendlysnmp.event.FScalarSetListener;
import org.friendlysnmp.event.FScalarValidationListener;
import org.friendlysnmp.event.FTableGetListener;
import org.friendlysnmp.event.FTableSetListener;
import org.friendlysnmp.event.UncaughtExceptionListener;
import org.friendlysnmp.mib.BaseMib;
import org.friendlysnmp.plugin.core.FriendlySnmpMib.FriendlyRowKeepDeleteActionTC;
import org.friendlysnmp.plugin.core.FriendlySnmpMib.FriendlyViewPolicyTC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class provides application information to SNMP objects  
 * declared in FRIENDLY-SNMP-MIB.
 * 
 * @version $Revision: 1.20 $
 */
public class CoreExceptionsHandler extends FHandler 
implements UncaughtExceptionListener 
{
    /** Logger object */
    private static final Logger logger = LoggerFactory.getLogger(CoreExceptionsHandler.class);
    
    /** FriendlySNMP MIB */
    private FriendlySnmpMibFriend mib;
    
    /** Currently viewed exception */
    private ExceptionInfo excCurView;
    
    /** Collection of recorded exception */
    private List<ExceptionInfo> lstInfo;
    
    /** Maximum ExceptionInfo object ID */
    private int maxID;
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.FHandler#init()
     */
    @Override
    public void init() {
        agent.addUncaughtExceptionListener(this);
        lstInfo = new ArrayList<ExceptionInfo>();
    } // init()
    
    @Override
    public void registerMib(BaseMib mibBase) throws FException { 
        super.registerMib(mibBase);
        mib = (FriendlySnmpMibFriend)mibBase;
        
        // RW
        // No GET/SET listeners, keep value in scalar
        FScalar scalar = mib.getExceptionViewPolicy();
        scalar.setVolatile(false); // loads persistent value (if exist)
        if (!scalar.isPersistLoaded()) {
            setDefaultExceptionViewPolicy();
        }
        scalar.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultExceptionViewPolicy();
            }
        });
        
        // RW
        scalar = mib.getExceptionViewFixedIndex();
        scalar.setVolatile(true);
        scalar.addGetListener(new FScalarGetListener() {
            @Override
            public void get(FScalar scalar) {
                scalar.setValueEx(excCurView == null ? 0 : excCurView.id);
            }
        });
        scalar.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                excCurView = getExceptionInfo((Integer)scalar.getValue());
            }
        });
        scalar.addValidationListener(new FScalarValidationListener() {
            @Override
            public ValueValidation validate(FScalar scalar, Object objNewValue) {
                return validateViewFixedIndex((Integer)objNewValue);
            }
        });
        
        // RW Table with list of exceptions
        mib.getExceptionsListEntry().setVolatile(true); // no persist on 'delete'
        mib.getExceptionsListEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadExceptionsListTable(table);
            }
        });
        mib.getExceptionsListEntry().addSetListener(new FTableSetListener() {
            @Override
            public void set(FTable table, FID idRow, FColumn col, TableRowAction action) {
                removeException(table, idRow, col, action);
            }
        });
        
        // RO Table Content
        mib.getExceptionViewEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadExceptionViewTable(table);
            }
        });
    } // registerMib()

    private void setDefaultExceptionViewPolicy() throws FException {
        mib.getExceptionViewPolicy().setValue(FriendlyViewPolicyTC.next);
    }
    
    /**
     * Loads table with exceptions list.
     */
    private synchronized void loadExceptionsListTable(FTable table) {
        try {
            table.deleteAll();
            for (ExceptionInfo exc : lstInfo) {
                FID idRow = table.addRow(exc.id);
                table.setValueAt(exc.count, 
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListCount);
                table.setValueAt(exc.type.getType(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListType);
                table.setValueAt(exc.lastTimeThrown, 
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListLastOccur);
                String msg = exc.e.getMessage();
                table.setValueAt(msg == null ? "" : msg, // NPE has null
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListMessage);
                table.setValueAt(exc.e.getClass().getName(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListClass);
                table.setValueAt(exc.t.getName(), 
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListThread);
                table.setValueAt(FriendlyRowKeepDeleteActionTC.keep, 
                        idRow, FriendlySnmpMibFriend.COLUMN_ExceptionsListAction);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadExceptionsListTable()
    
    /**
     * Loads exceptions content table data
     */
    private synchronized void loadExceptionViewTable(FTable table) {
        int size = lstInfo.size();
        // Load table with current exception 
        try {
            table.deleteAll();
            if (excCurView == null  &&  size > 0) {
                excCurView = lstInfo.get(0);
            }            
            if (excCurView == null) {
                return;
            }
            logger.debug("Current load: " + excCurView);
            addRow("Exception ID: " + excCurView.id);
            addRow("Last time thrown: " + excCurView.lastTimeThrown);
            addRow("Count thrown: " + excCurView.count);
            addRow("Type: " + excCurView.type);
            for (String s : excCurView.contentAll) {
                addRow(s);
            }
        } catch (FException e) {
            mib.exceptionThrown(table, e);
        }
        // Prepare next exception to view on next GET request
        int viewPolicy = (Integer)mib.getExceptionViewPolicy().getValue();
        if (viewPolicy == FriendlyViewPolicyTC.next  &&  size > 0) {
            int curIndex = 0;
            for (int i = 0;  i < size;  i++) {
                ExceptionInfo exc = lstInfo.get(i);
                if (exc.equals(excCurView)) {
                    curIndex = i; // current index
                    break;
                }                
            }
            curIndex++;
            if (curIndex >= size) {
                curIndex = 0;
            }
            excCurView = lstInfo.get(curIndex);
        }
        logger.debug("Future load: " + excCurView);
    } // loadExceptionViewTable()

    /**
     * Adds exception content table row
     * 
     * @param line content line
     * 
     * @throws FException
     */
    private void addRow(String line) throws FException {
        FTable table = mib.getExceptionViewEntry();
        FID idRow = table.addRowNext();
        table.setValueAt(line, 
                idRow, FriendlySnmpMibFriend.COLUMN_ExceptionLine);
    } // addContentLine()
    
    /**
     * Removes exception from exceptions list table
     * 
     * @param table exceptions list table
     * @param idRow row index
     * @param indexCol column index (expected exceptionListAction only)
     * @param action action (expected CHANGE only)
     */
    private synchronized void removeException(
            FTable table, FID idRow, FColumn col, TableRowAction action) 
    {
        // Do not expect any other action
        if (action == TableRowAction.ROW_CHANGE  &&  
            col.equals(FriendlySnmpMibFriend.COLUMN_ExceptionsListAction)) 
        {
            int id = idRow.getInt()[0];
            ExceptionInfo exc = getExceptionInfo(id);
            lstInfo.remove(exc);
            if (excCurView == exc  ||  lstInfo.size() == 0) {
                excCurView = null;
            }
        }
    } // removeException()   
    
    /**
     * Validates view fixed index exception SET request
     * 
     * @param id selected exception ID
     * 
     * @return validation result
     */
    private synchronized ValueValidation validateViewFixedIndex(int id) {
        for (ExceptionInfo exc : lstInfo) {
            if (exc.id == id) {
                return ValueValidation.SUCCESS;
            }
        }
        return ValueValidation.BAD_VALUE;
    } // validateViewFixedIndex()
    
    /**
     * Returns exception information for the specified ID
     * 
     * @param id exception ID
     * 
     * @return exception information object with specified ID
     */
    private synchronized ExceptionInfo getExceptionInfo(int id) {
        for (ExceptionInfo exc : lstInfo) {
            if (exc.id == id) {
                return exc;
            }
        }
        return null;
    } // getExceptionInfo()
    
    /**
     * Adds exception to the list
     * 
     * @param comment exception comment
     * @param e exception
     */
    public void reportException(String comment, Throwable e) {
        addException(comment, e, Thread.currentThread(), ExceptionType.CAUGHT);
    } // reportException()
    
    /**
     * Adds exception to the list
     * 
     * @param comment exception comment
     * @param e exception
     * @param type exception type
     */
    private synchronized void addException(String comment,  
            Throwable e, Thread t, ExceptionType type) 
    {
        // The new exception might be a repeat of the recorded exception
        if (e == null) {
            return;
        }
        excCurView = null;
        ExceptionInfo excNew = new ExceptionInfo(comment, e, t, type);
        for (ExceptionInfo exc : lstInfo) {
            if (exc.equals(excNew)) {
                excCurView = exc;
                // Increase counter for already recorded repeated exception
                excCurView.lastTimeThrown = excNew.lastTimeThrown;
                excCurView.count++;
                break;
            }
        }
        if (excCurView == null) {
            // Record new exception
            lstInfo.add(excNew);
            excCurView = excNew;
        }
        switch (type) {
            case CAUGHT:
                mib.getAppExceptionCaught().sendNotification(excCurView.contentAll);
                break;
            case UNCAUGHT:
                mib.getAppExceptionUncaught().sendNotification(excCurView.contentAll);
                break;
        }
    } // addException()
    
    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.event.UncaughtExceptionListener#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // implements UncaughtExceptionListener
        addException("", e, t, ExceptionType.UNCAUGHT);
    } // uncaughtException()

    /**
     * Inner class with exception information
     */
    private class ExceptionInfo {
        /** Unique exception ID */
        int id;
        /** Counter */
        int count;
        /** Last thrown */
        String lastTimeThrown;
        /** Actual exception object (from ctor) */
        Throwable e;
        /** Thread where the exception was thrown */
        Thread t;
        /** Exception type */
        ExceptionType type;
        /** Comment */
        String comment;
        /** Exception trace stack populated from exception */
        String[] contentAll;
        /**
         * Constructor
         * 
         * @param comment exception comment (OK for null)
         * @param e exception
         * @param t thread with exception
         * @param type exception type
         */
        ExceptionInfo(String comment, Throwable e, Thread t, ExceptionType type) {
            this.id = ++maxID;
            this.e = e;
            this.t = t;
            this.count = 1;
            this.type = type;
            this.lastTimeThrown = getTimestampNow();
            this.comment = (comment == null ? "" : comment);
            loadContent();
        } // ExceptionInfo()
        
        /**
         * Loads exception content. This method extracts exception stack
         * trace and cache it.
         */
        void loadContent() {
            List<String> lst = new ArrayList<String>();
            String output = ThrowableFormatter.format(comment, e, t);
            String[] lineAll = output.split("\n");
            for (String s : lineAll) {
               lst.add(s.trim());
            }
            contentAll = lst.toArray(new String[lst.size()]);
        } // loadContent()
        
        /**
         * Indicates whether some other object is "equal to" this one.
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ExceptionInfo) {
                ExceptionInfo that = (ExceptionInfo)obj;
                if (this.type != that.type) { 
                    return false; 
                }
                if (this.comment != that.comment) { 
                    return false; 
                }
                if (this.contentAll.length != that.contentAll.length) { 
                    return false; 
                }
                for (int i = 0;  i < contentAll.length;  i++) {
                    if (!this.contentAll[i].equals(that.contentAll[i])) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } // equals()

        /**
         * Returns a hash code value for the object.
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return e.hashCode(); // only for FindBugs 
        } // hashCode()        
        
        /**
         * Returns a string representation of the object.
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return String.format(
                "ExceptionInfo=[id=%d, Count=%d, Class=%s]",
                id, count, e.getClass().getName());
        }
    } // inner class ExceptionInfo

} // class CoreExceptionsHandler
