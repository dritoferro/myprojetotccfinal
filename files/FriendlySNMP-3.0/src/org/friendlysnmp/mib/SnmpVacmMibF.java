/*
 * File: SnmpVacmMibF.java
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
 * $Id: SnmpVacmMibF.java,v 1.13 2014/01/11 02:19:24 mg Exp $
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
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.smi.OctetString;

public class SnmpVacmMibF extends BaseMib {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SnmpVacmMibF.class);

    private VacmMIB mibORIG;

    // Not declared in org.snmp4j.agent.mo.snmp.VacmMIB,
    // hardcoded and defined by insert order in: 
    //
    //    MOColumn[] vacmContextColumns = new MOColumn[] {..}
    private static final int colVacmContextName = 1;
    private static final int idxVacmContextName = 0;
    
    //    MOColumn[] vacmSecurityToGroupColumns = new MOColumn[] {..}
    private static final int idxVacmGroupName = 0;
    private static final int idxVacmSecurityToGroupStorageType = 1;
    private static final int idxVacmSecurityToGroupRowStatus = 2;
    
    // Scalars
    private FScalar vacmViewSpinLock;
    
    // Tables
    private FTable vacmContextEntry;
    private FTable vacmSecurityToGroupEntry;
    private FTable vacmAccessEntry;
    private FTable vacmViewTreeFamilyEntry;
    
    // Columns for table vacmContextEntry
    public final static FColumn COLUMN_VacmContextName = 
        new FColumn("vacm",
                idxVacmContextName, 
                colVacmContextName);
    
    // Columns for table vacmSecurityToGroupEntry
    public final static FColumn COLUMN_VacmGroupName = 
        new FColumn("vacmVacmGroupName",
                idxVacmGroupName, 
                VacmMIB.colVacmGroupName);
    public final static FColumn COLUMN_VacmSecurityToGroupStorageType = 
        new FColumn("vacmSecurityToGroupStorageType",
                idxVacmSecurityToGroupStorageType, 
                VacmMIB.colVacmSecurityToGroupStorageType);
    public final static FColumn COLUMN_VacmSecurityToGroupRowStatus = 
        new FColumn("vacmSecurityToGroupRowStatus",
                idxVacmSecurityToGroupRowStatus, 
                VacmMIB.colVacmSecurityToGroupRowStatus);
    
    // Columns for table vacmAccessEntry
    public final static FColumn COLUMN_VacmAccessContextMatch = 
        new FColumn("vacmAccessContextMatch",
                VacmMIB.idxVacmAccessContextMatch, 
                VacmMIB.colVacmAccessContextMatch);
    public final static FColumn COLUMN_VacmAccessReadViewName = 
        new FColumn("vacmAccessReadViewName",
                VacmMIB.idxVacmAccessReadViewName, 
                VacmMIB.colVacmAccessReadViewName);
    public final static FColumn COLUMN_VacmAccessWriteViewName = 
        new FColumn("vacmAccessWriteViewName",
                VacmMIB.idxVacmAccessWriteViewName, 
                VacmMIB.colVacmAccessWriteViewName);
    public final static FColumn COLUMN_VacmAccessNotifyViewName = 
        new FColumn("vacmAccessNotifyViewName",
                VacmMIB.idxVacmAccessNotifyViewName, 
                VacmMIB.colVacmAccessNotifyViewName);
    public final static FColumn COLUMN_VacmAccessStorageType = 
        new FColumn("vacmAccessStorageType",
                VacmMIB.idxVacmAccessStorageType, 
                VacmMIB.colVacmAccessStorageType);
    public final static FColumn COLUMN_VacmAccessRowStatus = 
        new FColumn("vacmAccessRowStatus",
                VacmMIB.idxVacmAccessRowStatus, 
                VacmMIB.colVacmAccessRowStatus);
    
    // Columns for table vacmViewTreeFamilyEntry
    public final static FColumn COLUMN_VacmViewTreeFamilyMask = 
        new FColumn("vacmViewTreeFamilyMask",
                VacmMIB.idxVacmViewTreeFamilyMask, 
                VacmMIB.colVacmViewTreeFamilyMask);
    public final static FColumn COLUMN_VacmViewTreeFamilyType = 
        new FColumn("vacmViewTreeFamilyType",
                VacmMIB.idxVacmViewTreeFamilyType, 
                VacmMIB.colVacmViewTreeFamilyType);
    public final static FColumn COLUMN_VacmViewTreeFamilyStorageType = 
        new FColumn("vacmTreeFamilyStorageType",
                VacmMIB.idxVacmViewTreeFamilyStorageType, 
                VacmMIB.colVacmViewTreeFamilyStorageType);
    public final static FColumn COLUMN_VacmViewTreeFamilyRowStatus = 
        new FColumn("vacmViewTreeFamilyRowStatus",
                VacmMIB.idxVacmViewTreeFamilyRowStatus, 
                VacmMIB.colVacmViewTreeFamilyRowStatus);
    
    public SnmpVacmMibF(VacmMIB mibOrig) {
        super();
        this.mibORIG = mibOrig;
    } // SnmpVacmMibF()

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
        vacmViewSpinLock = new FScalar("vacmViewSpinLock", 
                srv.getMOScalar(VacmMIB.vacmViewSpinLockOID), agent);
        vacmViewSpinLock.setVolatile(false); // loads persistent value (if exist)
        if (!vacmViewSpinLock.isPersistLoaded()) {
            setDefaultSpinLock();
        }
        vacmViewSpinLock.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        vacmViewSpinLock.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSpinLock();
            }
        });
        addNode(vacmViewSpinLock);

        // Tables
        vacmContextEntry = new FTable("vacmContextEntry", 
                srv.getMOTable(VacmMIB.vacmContextEntryOID), agent,
                COLUMN_VacmContextName);
        addNode(vacmContextEntry);
        vacmSecurityToGroupEntry = new FTable("vacmSecurityToGroupEntry", 
                srv.getMOTable(VacmMIB.vacmSecurityToGroupEntryOID), agent,
                COLUMN_VacmGroupName, 
                COLUMN_VacmSecurityToGroupStorageType, 
                COLUMN_VacmSecurityToGroupRowStatus);
        vacmSecurityToGroupEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(vacmSecurityToGroupEntry);
        vacmAccessEntry = new FTable("vacmAccessEntry", 
                srv.getMOTable(VacmMIB.vacmAccessEntryOID), agent,
                COLUMN_VacmAccessContextMatch, 
                COLUMN_VacmAccessReadViewName, 
                COLUMN_VacmAccessWriteViewName, 
                COLUMN_VacmAccessNotifyViewName, 
                COLUMN_VacmAccessStorageType,
                COLUMN_VacmAccessRowStatus);
        vacmAccessEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(vacmAccessEntry);
        vacmViewTreeFamilyEntry = new FTable("vacmViewTreeFamilyEntry", 
                srv.getMOTable(VacmMIB.vacmViewTreeFamilyEntryOID), agent,
                COLUMN_VacmViewTreeFamilyMask, 
                COLUMN_VacmViewTreeFamilyType, 
                COLUMN_VacmViewTreeFamilyStorageType, 
                COLUMN_VacmViewTreeFamilyRowStatus);
        vacmViewTreeFamilyEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(vacmViewTreeFamilyEntry);
    } // init()

    private void setDefaultSpinLock() throws FException {
        vacmViewSpinLock.setValue(0);
        logger.debug("Default VacmViewSpinLock: 0");
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

    public FScalar getVacmViewSpinLock() {
        return vacmViewSpinLock;
    } // getVacmViewSpinLock()

    public FTable getVacmContextEntry() {
        return vacmContextEntry;
    } // getVacmContextEntry()
    
    public FTable getVacmSecurityToGroupEntry() {
        return vacmSecurityToGroupEntry;
    } // getVacmSecurityToGroupEntry()
    
    public FTable getVacmAccessEntry() {
        return vacmAccessEntry;
    } // getVacmAccessEntry()
    
    public FTable getVacmViewTreeFamilyEntry() {
        return vacmViewTreeFamilyEntry;
    } // getVacmViewTreeFamilyEntry()
    
} // class SnmpVacmMibF
