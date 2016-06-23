/*
 * File: SnmpCommunityMibF.java
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
 * $Id: SnmpCommunityMibF.java,v 1.8 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FTable;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.smi.OctetString;

public class SnmpCommunityMibF extends BaseMib {

    private SnmpCommunityMIB mibORIG;
    
    // Tables
    private FTable snmpCommunityEntry;
    private FTable snmpTargetAddrExtEntry;
    
    // Columns for table snmpCommunityEntry
    public final static FColumn COLUMN_SnmpCommunityName = 
        new FColumn("snmpCommunityName",
                SnmpCommunityMIB.idxSnmpCommunityName, 
                SnmpCommunityMIB.colSnmpCommunityName);
    public final static FColumn COLUMN_SnmpCommunitySecurityName = 
        new FColumn("snmpCommunitySecurityName",
                SnmpCommunityMIB.idxSnmpCommunitySecurityName, 
                SnmpCommunityMIB.colSnmpCommunitySecurityName);
    public final static FColumn COLUMN_SnmpCommunityContextEngineID = 
        new FColumn("snmpCommunityContextEngineID",
                SnmpCommunityMIB.idxSnmpCommunityContextEngineID, 
                SnmpCommunityMIB.colSnmpCommunityContextEngineID);
    public final static FColumn COLUMN_SnmpCommunityContextName = 
        new FColumn("snmpCommunityContextName",
                SnmpCommunityMIB.idxSnmpCommunityContextName, 
                SnmpCommunityMIB.colSnmpCommunityContextName);
    public final static FColumn COLUMN_SnmpCommunityTransportTag = 
        new FColumn("snmpCommunityTransportTag",
                SnmpCommunityMIB.idxSnmpCommunityTransportTag, 
                SnmpCommunityMIB.colSnmpCommunityTransportTag);
    public final static FColumn COLUMN_SnmpCommunityStorageType = 
        new FColumn("snmpCommunityStorageType",
                SnmpCommunityMIB.idxSnmpCommunityStorageType, 
                SnmpCommunityMIB.colSnmpCommunityStorageType);
    public final static FColumn COLUMN_SnmpCommunityStatus = 
        new FColumn("snmpCommunityStatus",
                SnmpCommunityMIB.idxSnmpCommunityStatus, 
                SnmpCommunityMIB.colSnmpCommunityStatus);

    // Columns for table snmpTargetAddrExtEntry
    public final static FColumn COLUMN_SnmpTargetAddrTMask = 
        new FColumn("snmpTargetAddrTMask",
                SnmpCommunityMIB.idxSnmpTargetAddrTMask, 
                SnmpCommunityMIB.colSnmpTargetAddrTMask);
    public final static FColumn COLUMN_SnmpTargetAddrMMS = 
        new FColumn("snmpTargetAddrMMS",
                SnmpCommunityMIB.idxSnmpTargetAddrMMS, 
                SnmpCommunityMIB.colSnmpTargetAddrMMS);
    
    public SnmpCommunityMibF(SnmpCommunityMIB mibORIG) {
        super();
        this.mibORIG = mibORIG;
    } // SnmpCommunityMibF()

    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        // Tables
        snmpCommunityEntry = new FTable("snmpCommunityEntry", mibORIG.getSnmpCommunityEntry(), agent,
                COLUMN_SnmpCommunityName, 
                COLUMN_SnmpCommunitySecurityName, 
                COLUMN_SnmpCommunityContextEngineID, 
                COLUMN_SnmpCommunityContextName,
                COLUMN_SnmpCommunityTransportTag, 
                COLUMN_SnmpCommunityStorageType, 
                COLUMN_SnmpCommunityStatus);
        snmpCommunityEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpCommunityEntry);
        snmpTargetAddrExtEntry = new FTable("snmpTargetAddrExtEntry", mibORIG.getSnmpTargetAddrExtEntry(), agent,
                COLUMN_SnmpTargetAddrTMask, 
                COLUMN_SnmpTargetAddrMMS);
        snmpTargetAddrExtEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpTargetAddrExtEntry);
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

    public FTable getSnmpCommunityEntry() {
        return snmpCommunityEntry;
    } // getSnmpCommunityEntry()

    public FTable getSnmpTargetAddrExtEntry() {
        return snmpTargetAddrExtEntry;
    } // getSnmpTargetAddrExtEntry()
    
} // class SnmpCommunityMibF
