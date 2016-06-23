/*
 * File: SnmpTargetMibF.java
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
 * $Id: SnmpTargetMibF.java,v 1.14 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.event.FRestoreDefaultEvent;
import org.friendlysnmp.event.FRestoreDefaultListener;
import org.friendlysnmp.event.FScalarSetListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

public class SnmpTargetMibF extends BaseMib {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SnmpTargetMibF.class);

    private SnmpTargetMIB mibORIG;

    /** Duplicate to private org.snmp4j.agent.mo.snmp.SnmpTargetMIB */
    public static final OID oidSnmpTargetSpinLock =
        new OID(new int[] {1, 3, 6, 1, 6, 3, 12, 1, 1, 0});

    /** Duplicate to private org.snmp4j.agent.mo.snmp.SnmpTargetMIB */
    public static final OID oidSnmpTargetAddrEntry =
        new OID(new int[] {1, 3, 6, 1, 6, 3, 12, 1, 2, 1});

    // Column sub-identifier definitions for snmpTargetAddrEntry:
    private static final int colSnmpTargetAddrTDomain = 2;
    private static final int colSnmpTargetAddrTAddress = 3;
    private static final int colSnmpTargetAddrTimeout = 4;
    private static final int colSnmpTargetAddrRetryCount = 5;
    private static final int colSnmpTargetAddrTagList = 6;
    private static final int colSnmpTargetAddrParams = 7;
    private static final int colSnmpTargetAddrStorageType = 8;
    private static final int colSnmpTargetAddrRowStatus = 9;

    // Column index definitions for snmpTargetAddrEntry:
    static final int idxSnmpTargetAddrTDomain = 0;
    static final int idxSnmpTargetAddrTAddress = 1;
    static final int idxSnmpTargetAddrTimeout = 2;
    static final int idxSnmpTargetAddrRetryCount = 3;
    static final int idxSnmpTargetAddrTagList = 4;
    static final int idxSnmpTargetAddrParams = 5;
    static final int idxSnmpTargetAddrStorageType = 6;
    static final int idxSnmpTargetAddrRowStatus = 7;
    
    /** Duplicate to private org.snmp4j.agent.mo.snmp.SnmpTargetMIB */
    public static final OID oidSnmpTargetParamsEntry =
        new OID(new int[] {1, 3, 6, 1, 6, 3, 12, 1, 3, 1});
    
    // Column sub-identifier definitions for snmpTargetParamsEntry:
    private static final int colSnmpTargetParamsMPModel = 2;
    private static final int colSnmpTargetParamsSecurityModel = 3;
    private static final int colSnmpTargetParamsSecurityName = 4;
    private static final int colSnmpTargetParamsSecurityLevel = 5;
    private static final int colSnmpTargetParamsStorageType = 6;
    private static final int colSnmpTargetParamsRowStatus = 7;

    // Column index definitions for snmpTargetParamsEntry:
    static final int idxSnmpTargetParamsMPModel = 0;
    static final int idxSnmpTargetParamsSecurityModel = 1;
    static final int idxSnmpTargetParamsSecurityName = 2;
    static final int idxSnmpTargetParamsSecurityLevel = 3;
    static final int idxSnmpTargetParamsStorageType = 4;
    static final int idxSnmpTargetParamsRowStatus = 5;
    
    // Scalars
    private FScalar snmpTargetSpinLock;
    private FScalar snmpUnavailableContexts;
    private FScalar snmpUnknownContexts;
    
    // Tables
    private FTable snmpTargetAddrEntry;
    private FTable snmpTargetParamsEntry;
    
    // Columns for table snmpTargetAddrEntry
    public final static FColumn COLUMN_SnmpTargetAddrTDomain = 
        new FColumn("snmpTargetAddrTDomain",
                idxSnmpTargetAddrTDomain, 
                colSnmpTargetAddrTDomain);
    public final static FColumn COLUMN_SnmpTargetAddrTAddress = 
        new FColumn("snmpTargetAddrTAddress",
                idxSnmpTargetAddrTAddress, 
                colSnmpTargetAddrTAddress);
    public final static FColumn COLUMN_SnmpTargetAddrTimeout = 
        new FColumn("snmpTargetAddrTimeout",
                idxSnmpTargetAddrTimeout, 
                colSnmpTargetAddrTimeout);
    public final static FColumn COLUMN_SnmpTargetAddrRetryCount = 
        new FColumn("snmpTargetAddrRetryCount",
                idxSnmpTargetAddrRetryCount, 
                colSnmpTargetAddrRetryCount);
    public final static FColumn COLUMN_SnmpTargetAddrTagList = 
        new FColumn("snmpTargetAddrTagList",
                idxSnmpTargetAddrTagList, 
                colSnmpTargetAddrTagList);
    public final static FColumn COLUMN_SnmpTargetAddrParams = 
        new FColumn("snmpTargetAddrParams",
                idxSnmpTargetAddrParams, 
                colSnmpTargetAddrParams);
    public final static FColumn COLUMN_SnmpTargetAddrStorageType = 
        new FColumn("snmpTargetAddrStorageType",
                idxSnmpTargetAddrStorageType, 
                colSnmpTargetAddrStorageType);
    public final static FColumn COLUMN_SnmpTargetAddrRowStatus = 
        new FColumn("snmpTargetAddrRowStatus",
                idxSnmpTargetAddrRowStatus, 
                colSnmpTargetAddrRowStatus);

    // Columns for table snmpTargetParamsEntry
    public final static FColumn COLUMN_SnmpTargetParamsMPModel = 
        new FColumn("snmpTargetParamsMPModel",
                idxSnmpTargetParamsMPModel, 
                colSnmpTargetParamsMPModel);
    public final static FColumn COLUMN_SnmpTargetParamsSecurityModel = 
        new FColumn("snmpTargetParamsSecurityModel",
                idxSnmpTargetParamsSecurityModel, 
                colSnmpTargetParamsSecurityModel);
    public final static FColumn COLUMN_SnmpTargetParamsSecurityName = 
        new FColumn("snmpTargetParamsSecurityName",
                idxSnmpTargetParamsSecurityName, 
                colSnmpTargetParamsSecurityName);
    public final static FColumn COLUMN_SnmpTargetParamsSecurityLevel = 
        new FColumn("snmpTargetParamsSecurityLevel",
                idxSnmpTargetParamsSecurityLevel, 
                colSnmpTargetParamsSecurityLevel);
    public final static FColumn COLUMN_SnmpTargetParamsStorageType = 
        new FColumn("snmpTargetParamsStorageType",
                idxSnmpTargetParamsStorageType, 
                colSnmpTargetParamsStorageType);
    public final static FColumn COLUMN_SnmpTargetParamsRowStatus = 
        new FColumn("snmpTargetParamsRowStatus",
                idxSnmpTargetParamsRowStatus, 
                colSnmpTargetParamsRowStatus);
    
    public SnmpTargetMibF(SnmpTargetMIB mibOrig) {
        super();
        this.mibORIG = mibOrig;
    } // SnmpTargetMibF()

    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        DumbServer srv = new DumbServer();
        try {
            mibORIG.registerMOs(srv, null);
        } catch (DuplicateRegistrationException e) {
            throw new RuntimeException(e);
        }
        // Scalars
        snmpTargetSpinLock = new FScalar("snmpTargetSpinLock", srv.getMOScalar(oidSnmpTargetSpinLock), agent);
        snmpTargetSpinLock.setVolatile(false); // loads persistent value (if exist)
        if (!snmpTargetSpinLock.isPersistLoaded()) {
            setDefaultSpinLock();
        }
        snmpTargetSpinLock.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        snmpTargetSpinLock.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSpinLock();
            }
        });
        addNode(snmpTargetSpinLock);
        snmpUnavailableContexts = new FScalar("snmpUnavailableContexts", 
                srv.getMOScalar(SnmpConstants.snmpUnavailableContexts), agent);
        addNode(snmpUnavailableContexts);
        snmpUnknownContexts = new FScalar("snmpUnknownContexts", 
                srv.getMOScalar(SnmpConstants.snmpUnknownContexts), agent);
        addNode(snmpUnknownContexts);
        
        // Tables
        snmpTargetAddrEntry = new FTable("snmpTargetAddrEntry", 
                srv.getMOTable(oidSnmpTargetAddrEntry), agent,
                COLUMN_SnmpTargetAddrTDomain, 
                COLUMN_SnmpTargetAddrTAddress, 
                COLUMN_SnmpTargetAddrTimeout, 
                COLUMN_SnmpTargetAddrRetryCount,
                COLUMN_SnmpTargetAddrTagList,
                COLUMN_SnmpTargetAddrParams,
                COLUMN_SnmpTargetAddrStorageType, 
                COLUMN_SnmpTargetAddrRowStatus); 
        snmpTargetAddrEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpTargetAddrEntry);
        snmpTargetParamsEntry = new FTable("snmpTargetParamsEntry", srv.getMOTable(oidSnmpTargetParamsEntry), agent,
                COLUMN_SnmpTargetParamsMPModel,
                COLUMN_SnmpTargetParamsSecurityModel, 
                COLUMN_SnmpTargetParamsSecurityName,
                COLUMN_SnmpTargetParamsSecurityLevel, 
                COLUMN_SnmpTargetParamsStorageType,
                COLUMN_SnmpTargetParamsRowStatus);
        snmpTargetParamsEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpTargetParamsEntry);
    } // init()

    private void setDefaultSpinLock() throws FException {
        snmpTargetSpinLock.setValue(0);
        logger.debug("Default TargetSpinLock: 0");
    }
    
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

    public FScalar getSnmpTargetSpinLock() {
        return snmpTargetSpinLock;
    } // getSnmpTargetSpinLock()

    public FScalar getSnmpUnavailableContexts() {
        return snmpUnavailableContexts;
    } // getSnmpUnavailableContexts()

    public FScalar getSnmpUnknownContexts() {
        return snmpUnknownContexts;
    } // getSnmpUnknownContexts()

    public FTable getSnmpTargetAddrEntry() {
        return snmpTargetAddrEntry;
    } // getSnmpTargetAddrEntry()
    
    public FTable getSnmpTargetParamsEntry() {
        return snmpTargetParamsEntry;
    } // getSnmpTargetParamsEntry()
    
} // class SnmpTargetMibF
