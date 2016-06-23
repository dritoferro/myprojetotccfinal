package org.friendlysnmp.plugin.core;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FNotification;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.mib.BaseMib;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.DefaultMOFactory;
import org.snmp4j.smi.OctetString;

public class FriendlySnmpMibFriend extends BaseMib {

    private FriendlySnmpMib mibORIG;

    // Scalars
    private FScalar deadlockCheckInterval;
    private FScalar deadlockViewFixedIndex;
    private FScalar deadlockViewPolicy;
    private FScalar exceptionViewFixedIndex;
    private FScalar exceptionViewPolicy;
    private FScalar shutdownApp;

    // Tables
    private FTable appConfigEntry;
    private FTable appDependenciesEntry;
    private FTable appInfoEntry;
    private FTable appPropEntry;
    private FTable deadlockViewEntry;
    private FTable deadlocksListEntry;
    private FTable exceptionViewEntry;
    private FTable exceptionsListEntry;
    private FTable persistCellEntry;
    private FTable persistScalarEntry;
    private FTable persistTableEntry;
    private FTable sysPropEntry;

    // Columns for table appConfigEntry
    public final static FColumn COLUMN_AppConfigKey = 
        new FColumn("AppConfigKey",
                FriendlySnmpMib.idxAppConfigKey, 
                FriendlySnmpMib.colAppConfigKey);
    public final static FColumn COLUMN_AppConfigValue = 
        new FColumn("AppConfigValue",
                FriendlySnmpMib.idxAppConfigValue, 
                FriendlySnmpMib.colAppConfigValue);

    // Columns for table appDependenciesEntry
    public final static FColumn COLUMN_AppDependenciesName = 
        new FColumn("AppDependenciesName",
                FriendlySnmpMib.idxAppDependenciesName, 
                FriendlySnmpMib.colAppDependenciesName);
    public final static FColumn COLUMN_AppDependenciesDesc = 
        new FColumn("AppDependenciesDesc",
                FriendlySnmpMib.idxAppDependenciesDesc, 
                FriendlySnmpMib.colAppDependenciesDesc);

    // Columns for table appInfoEntry
    public final static FColumn COLUMN_AppInfoName = 
        new FColumn("AppInfoName",
                FriendlySnmpMib.idxAppInfoName, 
                FriendlySnmpMib.colAppInfoName);
    public final static FColumn COLUMN_AppInfoDesc = 
        new FColumn("AppInfoDesc",
                FriendlySnmpMib.idxAppInfoDesc, 
                FriendlySnmpMib.colAppInfoDesc);

    // Columns for table appPropEntry
    public final static FColumn COLUMN_AppPropKey = 
        new FColumn("AppPropKey",
                FriendlySnmpMib.idxAppPropKey, 
                FriendlySnmpMib.colAppPropKey);
    public final static FColumn COLUMN_AppPropValue = 
        new FColumn("AppPropValue",
                FriendlySnmpMib.idxAppPropValue, 
                FriendlySnmpMib.colAppPropValue);

    // Columns for table deadlockViewEntry
    public final static FColumn COLUMN_DeadlockThreadLine = 
        new FColumn("DeadlockThreadLine",
                FriendlySnmpMib.idxDeadlockThreadLine, 
                FriendlySnmpMib.colDeadlockThreadLine);

    // Columns for table deadlocksListEntry
    public final static FColumn COLUMN_DeadlocksListThreadName = 
        new FColumn("DeadlocksListThreadName",
                FriendlySnmpMib.idxDeadlocksListThreadName, 
                FriendlySnmpMib.colDeadlocksListThreadName);
    public final static FColumn COLUMN_DeadlocksListBlockedByThreadID = 
        new FColumn("DeadlocksListBlockedByThreadID",
                FriendlySnmpMib.idxDeadlocksListBlockedByThreadID, 
                FriendlySnmpMib.colDeadlocksListBlockedByThreadID);
    public final static FColumn COLUMN_DeadlocksListBlockedByThreadName = 
        new FColumn("DeadlocksListBlockedByThreadName",
                FriendlySnmpMib.idxDeadlocksListBlockedByThreadName, 
                FriendlySnmpMib.colDeadlocksListBlockedByThreadName);
    public final static FColumn COLUMN_DeadlocksListLock = 
        new FColumn("DeadlocksListLock",
                FriendlySnmpMib.idxDeadlocksListLock, 
                FriendlySnmpMib.colDeadlocksListLock);
    public final static FColumn COLUMN_DeadlocksListBlockedTime = 
        new FColumn("DeadlocksListBlockedTime",
                FriendlySnmpMib.idxDeadlocksListBlockedTime, 
                FriendlySnmpMib.colDeadlocksListBlockedTime);

    // Columns for table exceptionViewEntry
    public final static FColumn COLUMN_ExceptionLine = 
        new FColumn("ExceptionLine",
                FriendlySnmpMib.idxExceptionLine, 
                FriendlySnmpMib.colExceptionLine);

    // Columns for table exceptionsListEntry
    public final static FColumn COLUMN_ExceptionsListCount = 
        new FColumn("ExceptionsListCount",
                FriendlySnmpMib.idxExceptionsListCount, 
                FriendlySnmpMib.colExceptionsListCount);
    public final static FColumn COLUMN_ExceptionsListType = 
        new FColumn("ExceptionsListType",
                FriendlySnmpMib.idxExceptionsListType, 
                FriendlySnmpMib.colExceptionsListType);
    public final static FColumn COLUMN_ExceptionsListLastOccur = 
        new FColumn("ExceptionsListLastOccur",
                FriendlySnmpMib.idxExceptionsListLastOccur, 
                FriendlySnmpMib.colExceptionsListLastOccur);
    public final static FColumn COLUMN_ExceptionsListMessage = 
        new FColumn("ExceptionsListMessage",
                FriendlySnmpMib.idxExceptionsListMessage, 
                FriendlySnmpMib.colExceptionsListMessage);
    public final static FColumn COLUMN_ExceptionsListClass = 
        new FColumn("ExceptionsListClass",
                FriendlySnmpMib.idxExceptionsListClass, 
                FriendlySnmpMib.colExceptionsListClass);
    public final static FColumn COLUMN_ExceptionsListThread = 
        new FColumn("ExceptionsListThread",
                FriendlySnmpMib.idxExceptionsListThread, 
                FriendlySnmpMib.colExceptionsListThread);
    public final static FColumn COLUMN_ExceptionsListAction = 
        new FColumn("ExceptionsListAction",
                FriendlySnmpMib.idxExceptionsListAction, 
                FriendlySnmpMib.colExceptionsListAction);

    // Columns for table persistCellEntry
    public final static FColumn COLUMN_PersistCellTableName = 
        new FColumn("PersistCellTableName",
                FriendlySnmpMib.idxPersistCellTableName, 
                FriendlySnmpMib.colPersistCellTableName);
    public final static FColumn COLUMN_PersistCellTableOID = 
        new FColumn("PersistCellTableOID",
                FriendlySnmpMib.idxPersistCellTableOID, 
                FriendlySnmpMib.colPersistCellTableOID);
    public final static FColumn COLUMN_PersistCellRowOID = 
        new FColumn("PersistCellRowOID",
                FriendlySnmpMib.idxPersistCellRowOID, 
                FriendlySnmpMib.colPersistCellRowOID);
    public final static FColumn COLUMN_PersistCellColumnName = 
        new FColumn("PersistCellColumnName",
                FriendlySnmpMib.idxPersistCellColumnName, 
                FriendlySnmpMib.colPersistCellColumnName);
    public final static FColumn COLUMN_PersistCellColumnOID = 
        new FColumn("PersistCellColumnOID",
                FriendlySnmpMib.idxPersistCellColumnOID, 
                FriendlySnmpMib.colPersistCellColumnOID);
    public final static FColumn COLUMN_PersistCellColumnIndex = 
        new FColumn("PersistCellColumnIndex",
                FriendlySnmpMib.idxPersistCellColumnIndex, 
                FriendlySnmpMib.colPersistCellColumnIndex);
    public final static FColumn COLUMN_PersistCellColumnSyntax = 
        new FColumn("PersistCellColumnSyntax",
                FriendlySnmpMib.idxPersistCellColumnSyntax, 
                FriendlySnmpMib.colPersistCellColumnSyntax);
    public final static FColumn COLUMN_PersistCellValue = 
        new FColumn("PersistCellValue",
                FriendlySnmpMib.idxPersistCellValue, 
                FriendlySnmpMib.colPersistCellValue);

    // Columns for table persistScalarEntry
    public final static FColumn COLUMN_PersistScalarName = 
        new FColumn("PersistScalarName",
                FriendlySnmpMib.idxPersistScalarName, 
                FriendlySnmpMib.colPersistScalarName);
    public final static FColumn COLUMN_PersistScalarOID = 
        new FColumn("PersistScalarOID",
                FriendlySnmpMib.idxPersistScalarOID, 
                FriendlySnmpMib.colPersistScalarOID);
    public final static FColumn COLUMN_PersistScalarSyntax = 
        new FColumn("PersistScalarSyntax",
                FriendlySnmpMib.idxPersistScalarSyntax, 
                FriendlySnmpMib.colPersistScalarSyntax);
    public final static FColumn COLUMN_PersistScalarValue = 
        new FColumn("PersistScalarValue",
                FriendlySnmpMib.idxPersistScalarValue, 
                FriendlySnmpMib.colPersistScalarValue);
    public final static FColumn COLUMN_PersistScalarAction = 
        new FColumn("PersistScalarAction",
                FriendlySnmpMib.idxPersistScalarAction, 
                FriendlySnmpMib.colPersistScalarAction);

    // Columns for table persistTableEntry
    public final static FColumn COLUMN_PersistTableName = 
        new FColumn("PersistTableName",
                FriendlySnmpMib.idxPersistTableName, 
                FriendlySnmpMib.colPersistTableName);
    public final static FColumn COLUMN_PersistTableOID = 
        new FColumn("PersistTableOID",
                FriendlySnmpMib.idxPersistTableOID, 
                FriendlySnmpMib.colPersistTableOID);
    public final static FColumn COLUMN_PersistTableColumnCount = 
        new FColumn("PersistTableColumnCount",
                FriendlySnmpMib.idxPersistTableColumnCount, 
                FriendlySnmpMib.colPersistTableColumnCount);
    public final static FColumn COLUMN_PersistTableRowCount = 
        new FColumn("PersistTableRowCount",
                FriendlySnmpMib.idxPersistTableRowCount, 
                FriendlySnmpMib.colPersistTableRowCount);
    public final static FColumn COLUMN_PersistTableAction = 
        new FColumn("PersistTableAction",
                FriendlySnmpMib.idxPersistTableAction, 
                FriendlySnmpMib.colPersistTableAction);

    // Columns for table sysPropEntry
    public final static FColumn COLUMN_SysPropKey = 
        new FColumn("SysPropKey",
                FriendlySnmpMib.idxSysPropKey, 
                FriendlySnmpMib.colSysPropKey);
    public final static FColumn COLUMN_SysPropValue = 
        new FColumn("SysPropValue",
                FriendlySnmpMib.idxSysPropValue, 
                FriendlySnmpMib.colSysPropValue);

    // Notifications
    private FNotification appDeadlock;
    private FNotification appExceptionCaught;
    private FNotification appExceptionUncaught;
    private FNotification appShutdown;
    private FNotification appStop;

    public FriendlySnmpMibFriend() {
        super();
    } // FriendlySnmpMibFriend()

    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        mibORIG = new FriendlySnmpMib(DefaultMOFactory.getInstance());
        // Scalars
        deadlockCheckInterval = new FScalar("deadlockCheckInterval", mibORIG.getDeadlockCheckInterval(), agent);
        addNode(deadlockCheckInterval);
        deadlockViewFixedIndex = new FScalar("deadlockViewFixedIndex", mibORIG.getDeadlockViewFixedIndex(), agent);
        addNode(deadlockViewFixedIndex);
        deadlockViewPolicy = new FScalar("deadlockViewPolicy", mibORIG.getDeadlockViewPolicy(), agent);
        addNode(deadlockViewPolicy);
        exceptionViewFixedIndex = new FScalar("exceptionViewFixedIndex", mibORIG.getExceptionViewFixedIndex(), agent);
        addNode(exceptionViewFixedIndex);
        exceptionViewPolicy = new FScalar("exceptionViewPolicy", mibORIG.getExceptionViewPolicy(), agent);
        addNode(exceptionViewPolicy);
        shutdownApp = new FScalar("shutdownApp", mibORIG.getShutdownApp(), agent);
        addNode(shutdownApp);
        // Tables
        appConfigEntry = new FTable("appConfigEntry", mibORIG.getAppConfigEntry(), agent,
            COLUMN_AppConfigKey,
            COLUMN_AppConfigValue);
        addNode(appConfigEntry);
        appDependenciesEntry = new FTable("appDependenciesEntry", mibORIG.getAppDependenciesEntry(), agent,
            COLUMN_AppDependenciesName,
            COLUMN_AppDependenciesDesc);
        addNode(appDependenciesEntry);
        appInfoEntry = new FTable("appInfoEntry", mibORIG.getAppInfoEntry(), agent,
            COLUMN_AppInfoName,
            COLUMN_AppInfoDesc);
        addNode(appInfoEntry);
        appPropEntry = new FTable("appPropEntry", mibORIG.getAppPropEntry(), agent,
            COLUMN_AppPropKey,
            COLUMN_AppPropValue);
        addNode(appPropEntry);
        deadlockViewEntry = new FTable("deadlockViewEntry", mibORIG.getDeadlockViewEntry(), agent,
            COLUMN_DeadlockThreadLine);
        addNode(deadlockViewEntry);
        deadlocksListEntry = new FTable("deadlocksListEntry", mibORIG.getDeadlocksListEntry(), agent,
            COLUMN_DeadlocksListThreadName,
            COLUMN_DeadlocksListBlockedByThreadID,
            COLUMN_DeadlocksListBlockedByThreadName,
            COLUMN_DeadlocksListLock,
            COLUMN_DeadlocksListBlockedTime);
        addNode(deadlocksListEntry);
        exceptionViewEntry = new FTable("exceptionViewEntry", mibORIG.getExceptionViewEntry(), agent,
            COLUMN_ExceptionLine);
        addNode(exceptionViewEntry);
        exceptionsListEntry = new FTable("exceptionsListEntry", mibORIG.getExceptionsListEntry(), agent,
            COLUMN_ExceptionsListCount,
            COLUMN_ExceptionsListType,
            COLUMN_ExceptionsListLastOccur,
            COLUMN_ExceptionsListMessage,
            COLUMN_ExceptionsListClass,
            COLUMN_ExceptionsListThread,
            COLUMN_ExceptionsListAction);
        addNode(exceptionsListEntry);
        persistCellEntry = new FTable("persistCellEntry", mibORIG.getPersistCellEntry(), agent,
            COLUMN_PersistCellTableName,
            COLUMN_PersistCellTableOID,
            COLUMN_PersistCellRowOID,
            COLUMN_PersistCellColumnName,
            COLUMN_PersistCellColumnOID,
            COLUMN_PersistCellColumnIndex,
            COLUMN_PersistCellColumnSyntax,
            COLUMN_PersistCellValue);
        addNode(persistCellEntry);
        persistScalarEntry = new FTable("persistScalarEntry", mibORIG.getPersistScalarEntry(), agent,
            COLUMN_PersistScalarName,
            COLUMN_PersistScalarOID,
            COLUMN_PersistScalarSyntax,
            COLUMN_PersistScalarValue,
            COLUMN_PersistScalarAction);
        addNode(persistScalarEntry);
        persistTableEntry = new FTable("persistTableEntry", mibORIG.getPersistTableEntry(), agent,
            COLUMN_PersistTableName,
            COLUMN_PersistTableOID,
            COLUMN_PersistTableColumnCount,
            COLUMN_PersistTableRowCount,
            COLUMN_PersistTableAction);
        addNode(persistTableEntry);
        sysPropEntry = new FTable("sysPropEntry", mibORIG.getSysPropEntry(), agent,
            COLUMN_SysPropKey,
            COLUMN_SysPropValue);
        addNode(sysPropEntry);
        // Notifications
        appDeadlock = new FNotification("appDeadlock", FriendlySnmpMib.oidAppDeadlock, agent);
        addNode(appDeadlock);
        appExceptionCaught = new FNotification("appExceptionCaught", FriendlySnmpMib.oidAppExceptionCaught, agent);
        addNode(appExceptionCaught);
        appExceptionUncaught = new FNotification("appExceptionUncaught", FriendlySnmpMib.oidAppExceptionUncaught, agent);
        addNode(appExceptionUncaught);
        appShutdown = new FNotification("appShutdown", FriendlySnmpMib.oidAppShutdown, agent);
        addNode(appShutdown);
        appStop = new FNotification("appStop", FriendlySnmpMib.oidAppStop, agent);
        addNode(appStop);
    } // init()

    @Override
    public void registerMOs(MOServer server, OctetString context)
    throws DuplicateRegistrationException
    {
        mibORIG.registerMOs(server, context);
    } // registerMOs()

    @Override
    public void unregisterMOs(MOServer server, OctetString context) {
        mibORIG.unregisterMOs(server, context);
    } // unregisterMOs()

    public FScalar getDeadlockCheckInterval() {
        return deadlockCheckInterval;
    } // getDeadlockCheckInterval()

    public FScalar getDeadlockViewFixedIndex() {
        return deadlockViewFixedIndex;
    } // getDeadlockViewFixedIndex()

    public FScalar getDeadlockViewPolicy() {
        return deadlockViewPolicy;
    } // getDeadlockViewPolicy()

    public FScalar getExceptionViewFixedIndex() {
        return exceptionViewFixedIndex;
    } // getExceptionViewFixedIndex()

    public FScalar getExceptionViewPolicy() {
        return exceptionViewPolicy;
    } // getExceptionViewPolicy()

    public FScalar getShutdownApp() {
        return shutdownApp;
    } // getShutdownApp()

    public FTable getAppConfigEntry() {
        return appConfigEntry;
    } // getAppConfigEntry()

    public FTable getAppDependenciesEntry() {
        return appDependenciesEntry;
    } // getAppDependenciesEntry()

    public FTable getAppInfoEntry() {
        return appInfoEntry;
    } // getAppInfoEntry()

    public FTable getAppPropEntry() {
        return appPropEntry;
    } // getAppPropEntry()

    public FTable getDeadlockViewEntry() {
        return deadlockViewEntry;
    } // getDeadlockViewEntry()

    public FTable getDeadlocksListEntry() {
        return deadlocksListEntry;
    } // getDeadlocksListEntry()

    public FTable getExceptionViewEntry() {
        return exceptionViewEntry;
    } // getExceptionViewEntry()

    public FTable getExceptionsListEntry() {
        return exceptionsListEntry;
    } // getExceptionsListEntry()

    public FTable getPersistCellEntry() {
        return persistCellEntry;
    } // getPersistCellEntry()

    public FTable getPersistScalarEntry() {
        return persistScalarEntry;
    } // getPersistScalarEntry()

    public FTable getPersistTableEntry() {
        return persistTableEntry;
    } // getPersistTableEntry()

    public FTable getSysPropEntry() {
        return sysPropEntry;
    } // getSysPropEntry()

    public FNotification getAppDeadlock() {
        return appDeadlock;
    } // getAppDeadlock()

    public FNotification getAppExceptionCaught() {
        return appExceptionCaught;
    } // getAppExceptionCaught()

    public FNotification getAppExceptionUncaught() {
        return appExceptionUncaught;
    } // getAppExceptionUncaught()

    public FNotification getAppShutdown() {
        return appShutdown;
    } // getAppShutdown()

    public FNotification getAppStop() {
        return appStop;
    } // getAppStop()

} // class FriendlySnmpMibFriend
