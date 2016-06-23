/*
 * File: SnmpUsmMibF.java
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
 * $Id: SnmpUsmMibF.java,v 1.12 2014/01/11 02:19:24 mg Exp $
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
import org.snmp4j.agent.mo.snmp.UsmMIB;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;

public class SnmpUsmMibF extends BaseMib {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SnmpUsmMibF.class);

    private UsmMIB mibORIG;

    // Not declared in org.snmp4j.agent.mo.snmp.UsmMIB,
    // defined by insert order in: 
    //    MOColumn[] usmUserColumns = new MOColumn[] {...}
    private static final int idxUsmUserSecurityName = 0;
    private static final int idxUsmUserCloneFrom = 1;
    private static final int idxUsmUserAuthProtocol = 2;
    private static final int idxUsmUserAuthKeyChange = 3;
    private static final int idxUsmUserOwnAuthKeyChange = 4;
    private static final int idxUsmUserPrivProtocol = 5;
    private static final int idxUsmUserPrivKeyChange = 6;
    private static final int idxUsmUserOwnPrivKeyChange = 7;
    private static final int idxUsmUserPublic = 8;
    private static final int idxUsmUserStorageType = 9;
    private static final int idxUsmUserStatus = 10;
    
    // Scalars
    private FScalar usmStatsUnsupportedSecLevels;
    private FScalar usmStatsNotInTimeWindows;
    private FScalar usmStatsUnknownUserNames;
    private FScalar usmStatsUnknownEngineIDs;
    private FScalar usmStatsWrongDigests;
    private FScalar usmStatsDecryptionErrors;
    private FScalar usmUserSpinLock;
    
    // Tables
    private FTable usmUserEntry;
    
    // Columns for table usmUserEntry
    public final static FColumn COLUMN_UsmUserSecurityName = 
        new FColumn("usmUserSecurityName",
                idxUsmUserSecurityName, 
                UsmMIB.colUsmUserSecurityName);
    public final static FColumn COLUMN_UsmUserCloneFrom = 
        new FColumn("usmUserCloneFrom",
                idxUsmUserCloneFrom, 
                UsmMIB.colUsmUserCloneFrom);
    public final static FColumn COLUMN_UsmUserAuthProtocol = 
        new FColumn("usmUserAuthProtocol",
                idxUsmUserAuthProtocol, 
                UsmMIB.colUsmUserAuthProtocol);
    public final static FColumn COLUMN_UsmUserAuthKeyChange = 
        new FColumn("usmUserAuthKeyChange",
                idxUsmUserAuthKeyChange, 
                UsmMIB.colUsmUserAuthKeyChange);
    public final static FColumn COLUMN_UsmUserOwnAuthKeyChange = 
        new FColumn("usmUserOwnAuthKeyChange",
                idxUsmUserOwnAuthKeyChange, 
                UsmMIB.colUsmUserOwnAuthKeyChange);
    public final static FColumn COLUMN_UsmUserPrivProtocol = 
        new FColumn("usmUserPrivProtocol",
                idxUsmUserPrivProtocol, 
                UsmMIB.colUsmUserPrivProtocol);
    public final static FColumn COLUMN_UsmUserPrivKeyChange = 
        new FColumn("usmUserPrivKeyChange",
                idxUsmUserPrivKeyChange, 
                UsmMIB.colUsmUserPrivKeyChange);
    public final static FColumn COLUMN_UsmUserOwnPrivKeyChange = 
        new FColumn("usmUserOwnPrivKeyChange",
                idxUsmUserOwnPrivKeyChange, 
                UsmMIB.colUsmUserOwnPrivKeyChange);
    public final static FColumn COLUMN_UsmUserPublic = 
        new FColumn("usmUserPublic",
                idxUsmUserPublic, 
                UsmMIB.colUsmUserPublic);
    public final static FColumn COLUMN_UsmUserStorageType = 
        new FColumn("usmUserStorageType",
                idxUsmUserStorageType, 
                UsmMIB.colUsmUserStorageType);
    public final static FColumn COLUMN_UsmUserStatus = 
        new FColumn("usmUserStatus",
                idxUsmUserStatus, 
                UsmMIB.colUsmUserStatus);
    
    public SnmpUsmMibF(UsmMIB mibORIG) {
        super();
        this.mibORIG = mibORIG;
    } // UsmMIBFriend()

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
        usmStatsUnsupportedSecLevels = new FScalar("usmStatsUnsupportedSecLevels", srv.getMOScalar(SnmpConstants.usmStatsUnsupportedSecLevels), agent);
        addNode(usmStatsUnsupportedSecLevels);
        usmStatsNotInTimeWindows = new FScalar("usmStatsNotInTimeWindows", srv.getMOScalar(SnmpConstants.usmStatsNotInTimeWindows), agent);
        addNode(usmStatsNotInTimeWindows);
        usmStatsUnknownUserNames = new FScalar("usmStatsUnknownUserNames", srv.getMOScalar(SnmpConstants.usmStatsUnknownUserNames), agent);
        addNode(usmStatsUnknownUserNames);
        usmStatsUnknownEngineIDs = new FScalar("usmStatsUnknownEngineIDs", srv.getMOScalar(SnmpConstants.usmStatsUnknownEngineIDs), agent);
        addNode(usmStatsUnknownEngineIDs);
        usmStatsWrongDigests = new FScalar("usmStatsWrongDigests", srv.getMOScalar(SnmpConstants.usmStatsWrongDigests), agent);
        addNode(usmStatsWrongDigests);
        usmStatsDecryptionErrors = new FScalar("usmStatsDecryptionErrors", srv.getMOScalar(SnmpConstants.usmStatsDecryptionErrors), agent);
        addNode(usmStatsDecryptionErrors);
        usmUserSpinLock = new FScalar("usmUserSpinLock", srv.getMOScalar(UsmMIB.usmUserSpinLockOID), agent);
        usmUserSpinLock.setVolatile(false); // loads persistent value (if exist)
        if (!usmUserSpinLock.isPersistLoaded()) {
            setDefaultSpinLock();
        }
        usmUserSpinLock.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        usmUserSpinLock.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSpinLock();
            }
        });
        addNode(usmUserSpinLock);
        
        // Tables
        usmUserEntry = new FTable("usmUserEntry", srv.getMOTable(UsmMIB.usmUserEntryOID), agent,
                COLUMN_UsmUserSecurityName, 
                COLUMN_UsmUserCloneFrom,
                COLUMN_UsmUserAuthProtocol, 
                COLUMN_UsmUserAuthKeyChange, 
                COLUMN_UsmUserOwnAuthKeyChange, 
                COLUMN_UsmUserPrivProtocol,
                COLUMN_UsmUserPrivKeyChange, 
                COLUMN_UsmUserOwnPrivKeyChange, 
                COLUMN_UsmUserPublic,
                COLUMN_UsmUserStorageType, 
                COLUMN_UsmUserStatus);
        usmUserEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(usmUserEntry);
    } // init()

    private void setDefaultSpinLock() throws FException {
        usmUserSpinLock.setValue(0);
        logger.debug("Default USMSpinLock: 0");
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

    public FScalar getUsmStatsUnsupportedSecLevels() {
        return usmStatsUnsupportedSecLevels;
    } // getUsmStatsUnsupportedSecLevels()
    
    public FScalar getUsmStatsNotInTimeWindows() {
        return usmStatsNotInTimeWindows;
    } // getUsmStatsNotInTimeWindows()
    
    public FScalar getUsmStatsUnknownUserNames() {
        return usmStatsUnknownUserNames;
    } // getUsmStatsUnknownUserNames()
    
    public FScalar getUsmStatsUnknownEngineIDs() {
        return usmStatsUnknownEngineIDs;
    } // getUsmStatsUnknownEngineIDs()
    
    public FScalar getUsmStatsWrongDigests() {
        return usmStatsWrongDigests;
    } // getUsmStatsWrongDigests()
    
    public FScalar getUsmStatsDecryptionErrors() {
        return usmStatsDecryptionErrors;
    } // getUsmStatsDecryptionErrors()
    
    public FScalar getUsmUserSpinLock() {
        return usmUserSpinLock;
    } // getUsmUserSpinLock()
    
    public FTable getUsmUserEntry() {
        return usmUserEntry;
    } // getUsmUserEntry()
    
} // class SnmpUsmMibF
