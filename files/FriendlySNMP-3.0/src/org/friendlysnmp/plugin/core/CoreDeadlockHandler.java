/*
 * File: CoreDeadlockHandler.java
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
 * $Id: CoreDeadlockHandler.java,v 1.18 2014/01/22 23:28:02 mg Exp $
 */
package org.friendlysnmp.plugin.core;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.friendlysnmp.FConstant;
import org.friendlysnmp.FException;
import org.friendlysnmp.FHandler;
import org.friendlysnmp.FID;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.ValueValidation;
import org.friendlysnmp.event.FRestoreDefaultEvent;
import org.friendlysnmp.event.FRestoreDefaultListener;
import org.friendlysnmp.event.FScalarGetListener;
import org.friendlysnmp.event.FScalarSetListener;
import org.friendlysnmp.event.FScalarValidationListener;
import org.friendlysnmp.event.FTableGetListener;
import org.friendlysnmp.mib.BaseMib;
import org.friendlysnmp.plugin.core.FriendlySnmpMib.FriendlyViewPolicyTC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class provides application information to SNMP objects  
 * declared in FRIENDLY-SNMP-MIB.
 * 
 * @version $Revision: 1.18 $
 */
public class CoreDeadlockHandler extends FHandler {
    /** Logger object */
    private static final Logger logger = LoggerFactory.getLogger(CoreDeadlockHandler.class);

    /** Friendly core MIB */
    private FriendlySnmpMibFriend mib;
    
    /** Thread management bean */
    private ThreadMXBean mxbThread;
    
    /** Deadlock detection thread */
    private DeadlockMonitorThread threadDeadlockMonitor;
    
    /** Deadlock check interval (in milliseconds) */
    private long deadlockCheckIntervalMls;
    
    /** Collection of discovered deadlocks */
    private List<DeadlockInfo> lstDeadlockInfo;
    
    /** Collection of discovered deadlocked threads */
    private List<ThreadInfo> lstThreadInfo;
    
    /** Currently viewed exception */
    private ThreadInfo tiCurView;
    
    /** Maximum DeadlockInfo object ID */
    private int maxID;
    
    /**
     * Constructor
     */
    public CoreDeadlockHandler() {
        mxbThread = ManagementFactory.getThreadMXBean();
        lstDeadlockInfo = new ArrayList<DeadlockInfo>();
        lstThreadInfo = new ArrayList<ThreadInfo>();
    } // CoreDeadlockHandler()

    @Override
    public void registerMib(BaseMib mibBase) throws FException { 
        super.registerMib(mibBase);
        mib = (FriendlySnmpMibFriend)mibBase;
        
        FScalar scalar;
        // RW Deadlock Check Interval
        scalar = mib.getDeadlockCheckInterval();
        scalar.setVolatile(false); // loads persistent value (if exist)
        if (!scalar.isPersistLoaded()) {
            setDefaultDeadlockCheckInterval();                
        }
        scalar.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                restartDeadlockMonitorThread();
            }
        });
        scalar.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultDeadlockCheckInterval();
                restartDeadlockMonitorThread();
            }
        });
        
        // RW
        // No GET/SET listeners, keep value in scalar
        scalar = mib.getDeadlockViewPolicy();
        scalar.setVolatile(false); // loads persistent value (if exist)
        if (!scalar.isPersistLoaded()) {
            setDefaultDeadlockViewPolicy();
        }
        scalar.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultDeadlockViewPolicy();
            }
        });
        
        // RW
        scalar = mib.getDeadlockViewFixedIndex();
        scalar.setVolatile(true);
        scalar.addGetListener(new FScalarGetListener() {
            @Override
            public void get(FScalar scalar) {
                scalar.setValueEx(tiCurView == null ? 0 : tiCurView.getThreadId());
            }
        });
        scalar.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                tiCurView = getThreadInfo((Integer)scalar.getValue());
            }
        });
        scalar.addValidationListener(new FScalarValidationListener() {
            @Override
            public ValueValidation validate(FScalar scalar, Object objNewValue) {
                return validateViewFixedIndex((Integer)objNewValue);
            }
        });
        
        // RO Table with list of deadlocked threads
        mib.getDeadlocksListEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadDeadlocksListTable(table);
            }
        });
        
        // RO Table Content
        mib.getDeadlockViewEntry().addGetListener(new FTableGetListener() {
            @Override
            public void get(FTable table) {
                loadDeadlockThreadViewTable(table);
            }
        });
    } // registerMib()
    
    /** 
     * {@inheritDoc}
     * Starts deadlock detection thread.  
     *
     * @see org.friendlysnmp.FHandler#start(org.friendlysnmp.FHandler.AgentStartType)
     */
    @Override
    public void start(AgentStartType startType) {
        restartDeadlockMonitorThread();
    } // start()

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.FHandler#stop()
     */
    @Override
    public void stop() {
        if (threadDeadlockMonitor != null) {
            threadDeadlockMonitor.shutdown();
            threadDeadlockMonitor.interrupt();
            threadDeadlockMonitor = null;
        }
    } // stop()
    
    /** 
     * {@inheritDoc}
     * Shutdowns deadlock detection thread.  
     *
     * @see org.friendlysnmp.FHandler#shutdown()
     */
    @Override
    public void shutdown() {
        stop();
    } // shutdown()
    
    private void setDefaultDeadlockViewPolicy() throws FException {
        mib.getDeadlockViewPolicy().setValue(FriendlyViewPolicyTC.next);
    }
    
    /**
     * Sets default deadlock check interval.
     * 
     * @throws FException
     */
    private void setDefaultDeadlockCheckInterval() throws FException {
        mib.getDeadlockCheckInterval().setValue(FConstant.DEFAULT_DEADLOCK_CHECK_INTERVAL_SEC);
    }
    
    /**
     * Starts deadlock detection thread
     */
    private void restartDeadlockMonitorThread() {
        Object obj = mib.getDeadlockCheckInterval().getValue();
        deadlockCheckIntervalMls = ((Number)obj).longValue() * 1000;
        logger.debug(String.format(
                "Deadlock check interval: %d mls", deadlockCheckIntervalMls));
        stop();
        if (deadlockCheckIntervalMls > 0) {
            threadDeadlockMonitor = new DeadlockMonitorThread();
            threadDeadlockMonitor.start();
        }
    } // restartDeadlockMonitorThread()

    /**
     * Deadlock detection
     */
    private synchronized void checkDeadlock() {
        // Two threads deadlock:
        // Thread-1: 
        //   id=12,                              ThreadInfo.getThreadId(); 
        //   name=DeadlockDemoThread-B           ThreadInfo.getThreadName();
        //   lockName=java.lang.String@2935df7   ThreadInfo.getLockName();
        //   lockOwnerID=13                      ThreadInfo.getLockOwnerId();
        //   lockOwnerName=DeadlockDemoThread-A  ThreadInfo.getLockOwnerName();
        // Thread-2: 
        //   id=13 
        //   name=DeadlockDemoThread-A
        //   lockName=java.lang.String@345da57 
        //   lockOwnerID=12 
        //   lockOwnerName=DeadlockDemoThread-B 
        
        // Get all deadlocked threads
        logger.debug("Checking deadlocks.");
        long[] deadlockAll = mxbThread.findMonitorDeadlockedThreads();
        if (deadlockAll == null  ||  deadlockAll.length == 0) {
            // No deadlocks, nothing to report
            return;
        }
        
        // Collect thread IDs which are chained in deadlocks, 
        // and are not already collected in lstDeadlockInfo
        List<Long> lstThreadId = new ArrayList<Long>();
        for (long id : deadlockAll) {
            boolean collected = false;
            for (DeadlockInfo di : lstDeadlockInfo) {
                if (di.isParticipant(id)) {
                    collected = true;
                    break;
                }
            }
            if (!collected) {
                lstThreadId.add(id);
            }
        }
        
        // Collect all deadlock chains (these are new deadlock chains)
        while (lstThreadId.size() > 0) {
            extractFirstDeadlockChain(lstThreadId);
        }
        
        // Send deadlock notifications:
        for (DeadlockInfo di : lstDeadlockInfo) {
            di.sendDeadlockNotification();
        }
    } // checkDeadlock()
    
    /**
     * Extracts first deadlocked chain of threads from provided collection
     * of deadlocked thread IDs.
     * 
     * @param lstId collection of deadlocked thread IDs
     */
    private void extractFirstDeadlockChain(List<Long> lstId) {
        List<ThreadInfo> lst = new ArrayList<ThreadInfo>();
        long minValue = Long.MAX_VALUE;
        for (long l : lstId) {
            if (minValue > l) {
                minValue = l;
            }
        }
        for (Long id = minValue;  id.longValue() > 0; ) {
            lstId.remove(id);
            ThreadInfo inf = mxbThread.getThreadInfo(id);
            if (inf == null) {
                break;
            }
            lst.add(inf);
            id = inf.getLockOwnerId();
            if (!lstId.contains(id)) {
                break;
            }
        }
        if (lst.size() > 0) {
            lstDeadlockInfo.add(new DeadlockInfo(lst));
        }
    } // extractFirstDeadlockChain()
    
    private synchronized void loadDeadlocksListTable(FTable table) {
        try {
            table.deleteAll();
            for (DeadlockInfo di : lstDeadlockInfo) {
                for (ThreadInfo ti : di.lst) {
                    FID idRow = table.addRow(di.id, (int)ti.getThreadId());
                    table.setValueAt(ti.getThreadName(),
                            idRow, FriendlySnmpMibFriend.COLUMN_DeadlocksListThreadName);
                    ThreadInfo tiBlck = di.getBlockingThreadInfo(ti); 
                    table.setValueAt(tiBlck.getThreadId(),
                            idRow, FriendlySnmpMibFriend.COLUMN_DeadlocksListBlockedByThreadID);
                    table.setValueAt(tiBlck.getThreadName(),
                            idRow, FriendlySnmpMibFriend.COLUMN_DeadlocksListBlockedByThreadName);
                    table.setValueAt(ti.getLockName(),
                            idRow, FriendlySnmpMibFriend.COLUMN_DeadlocksListLock);
                    table.setValueAt(di.ts.toString(),
                            idRow, FriendlySnmpMibFriend.COLUMN_DeadlocksListBlockedTime);
                }
            }
        } catch (FException e) {
            mib.exceptionThrown(table, e);
        }
    }
    
    private synchronized void loadDeadlockThreadViewTable(FTable table) {
        int size = lstThreadInfo.size();
        // Load table with current ThreadInfo 
        try {
            table.deleteAll();
            if (tiCurView == null  &&  size > 0) {
                tiCurView = lstThreadInfo.get(0);
            }            
            if (tiCurView == null) {
                return;
            }
            logger.debug("Current load: " + tiCurView);
            addContentLine("Thread ID: " + tiCurView.getThreadId());
            addContentLine("Thread name: " + tiCurView.getThreadName());
            for (DeadlockInfo di : lstDeadlockInfo) {
                ThreadInfo tiBlck = di.getBlockingThreadInfo(tiCurView); 
                if (tiBlck != null) {
                    addContentLine("Blocked by thread ID: " + tiBlck.getThreadId());
                    addContentLine("Blocked by thread name: " + tiBlck.getThreadName());
                    addContentLine("Blocked since: " + di.ts.toString());
                    break;
                }
            }
            addContentLine("Waiting for lock: " + tiCurView.getLockName());
            addContentLine("Trace stack: ");
            
            // Method ThreadInfo.getStackTrace() returns empty array.
            // This is a documented 'feature'. Use the workaround:
            Map<Thread, StackTraceElement[]> hm = Thread.getAllStackTraces();
            for (Thread t : hm.keySet()) {
                if (t.getId() == tiCurView.getThreadId()) {
                    StackTraceElement[] steAll = hm.get(t);
                    for (StackTraceElement ste : steAll) {
                        addContentLine(ste.toString());
                    }
                    break;
                }
            }
        } catch (FException e) {
            mib.exceptionThrown(table, e);
        }
        // Prepare next ThreadInfo to view on next GET request
        int viewPolicy = (Integer)mib.getDeadlockViewPolicy().getValue();
        if (viewPolicy == FriendlyViewPolicyTC.next  &&  size > 0) {
            int curIndex = 0;
            for (int i = 0;  i < size;  i++) {
                ThreadInfo inf = lstThreadInfo.get(i);
                if (inf.equals(tiCurView)) {
                    curIndex = i; // current index
                    break;
                }                
            }
            curIndex++;
            if (curIndex >= size) {
                curIndex = 0;
            }
            tiCurView = lstThreadInfo.get(curIndex);
        }
        logger.debug("Future load: " + tiCurView);
    }
    
    /**
     * Adds deadlocked thread content line
     * 
     * @param line content line
     * 
     * @throws FException
     */
    private void addContentLine(String line) throws FException {
        FTable table = mib.getDeadlockViewEntry();
        FID idRow = table.addRowNext();
        table.setValueAt(line, 
                idRow, FriendlySnmpMibFriend.COLUMN_DeadlockThreadLine);
    } // addContentLine()
    
    /**
     * Validates view fixed index thread ID SET request
     * 
     * @param id selected thread ID
     * 
     * @return validation result
     */
    private synchronized ValueValidation validateViewFixedIndex(int id) {
        for (ThreadInfo ti : lstThreadInfo) {
            if (ti.getThreadId() == id) {
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
    private synchronized ThreadInfo getThreadInfo(int id) {
        for (ThreadInfo ti : lstThreadInfo) {
            if (ti.getThreadId() == id) {
                return ti;
            }
        }
        return null;
    } // getThreadInfo()
    
    /**
     * Deadlock detection thread
     */
    private class DeadlockMonitorThread extends Thread {
        /** Shutdown flag */
        private volatile boolean shutdown;
        
        /**
         * Shutdowns deadlock detection thread 
         */
        void shutdown() {
            shutdown = true;
        }
        
        /** 
         * Starts heartbeat thread
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            logger.debug("Deadlock check thread started");
            setName("FriendlyDeadlockMonitorThread");
            do {
                if (deadlockCheckIntervalMls != 0) {
                    checkDeadlock();
                }
                try {
                    if (deadlockCheckIntervalMls == 0) {
                        Thread.sleep(Long.MAX_VALUE);
                    } else {
                        Thread.sleep(deadlockCheckIntervalMls);
                    }
                } catch (InterruptedException e) {
                }
            } while (!shutdown);
            logger.debug("Deadlock check thread exited");
        }
    } // inner class DeadlockMonitorThread
    
    /**
     * Deadlock information
     */
    private class DeadlockInfo {
        /** Unique deadlock Id */
        int id;
        /** Collection of mutually deadlocked threads */
        private List<ThreadInfo> lst;
        /** Timestamp when deadlock notification was sent */
        private Timestamp ts;
        
        /**
         * Constructor
         * 
         * @param lst collection of deadlocked threads
         */
        DeadlockInfo(List<ThreadInfo> lst) {
            this.id = ++maxID;
            this.lst = lst;
            lstThreadInfo.addAll(lst);
        }
        
        /**
         * Checks the provided thread ID participation in deadlocked
         * chain.
         * 
         * @param threadId thread Id
         * @return true if <code>threadId</code> participates in deadlocked chain
         * stored in <i>this</i> <code>DeadlockInfo</code> object.
         */
        boolean isParticipant(long threadId) {
            for (ThreadInfo ti : lst) {
                if (threadId == ti.getThreadId()) {
                    return true;
                }
            }
            return false;
        }
        
        ThreadInfo getBlockingThreadInfo(ThreadInfo ti) {
            int size = lst.size();
            for (int i = 0;  i < size;  i++) {
                if (ti == lst.get(i)) {
                    return ((i + 1) == size ? lst.get(0) : lst.get(i + 1));  
                }
            }
            return null;
        }
        
        /**
         * Send deadlock notification.
         */
        void sendDeadlockNotification() {
            if (ts != null) {
                return;
            }
            ts = new Timestamp(System.currentTimeMillis());
            int size = lst.size();
            Object[] objParamAll = new Object[size];
            for (int i = 0;  i < size;  i++) {
                ThreadInfo tiThis = lst.get(i);
                ThreadInfo tiBlck = getBlockingThreadInfo(tiThis);
                objParamAll[i] = String.format(
                    "Thread ID=%d %s is blocked by thread ID=%d %s waiting for lock %s", 
                    tiThis.getThreadId(), tiThis.getThreadName(),
                    tiBlck.getThreadId(), tiBlck.getThreadName(),
                    tiThis.getLockName());
            }
            mib.getAppDeadlock().sendNotification(objParamAll);
        } // sendNotification()
    } // inner class DeadlockInfo
    
} // class CoreDeadlockHandler
