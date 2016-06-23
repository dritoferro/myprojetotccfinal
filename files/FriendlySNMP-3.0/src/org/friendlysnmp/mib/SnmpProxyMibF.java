/*
 * File: SnmpProxyMibF.java
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
 * $Id: SnmpProxyMibF.java,v 1.8 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FTable;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.snmp.SnmpProxyMIB;
import org.snmp4j.smi.OctetString;

public class SnmpProxyMibF extends BaseMib {

    private SnmpProxyMIB mibORIG;

    // Tables
    private FTable snmpProxyEntry;
    
    // Columns for table snmpProxyEntry
    public final static FColumn COLUMN_SnmpProxyType = 
        new FColumn("snmpProxyType",
                SnmpProxyMIB.idxSnmpProxyType, 
                SnmpProxyMIB.colSnmpProxyType);
    public final static FColumn COLUMN_SnmpProxyContextEngineID = 
        new FColumn("snmpProxyContextEngineID",
                SnmpProxyMIB.idxSnmpProxyContextEngineID, 
                SnmpProxyMIB.colSnmpProxyContextEngineID);
    public final static FColumn COLUMN_SnmpProxyContextName = 
        new FColumn("snmpProxyContextName",
                SnmpProxyMIB.idxSnmpProxyContextName, 
                SnmpProxyMIB.colSnmpProxyContextName);
    public final static FColumn COLUMN_SnmpProxyTargetParamsIn = 
        new FColumn("snmpProxyTargetParamsIn",
                SnmpProxyMIB.idxSnmpProxyTargetParamsIn, 
                SnmpProxyMIB.colSnmpProxyTargetParamsIn);
    public final static FColumn COLUMN_SnmpProxySingleTargetOut = 
        new FColumn("snmpProxySingleTargetOut",
                SnmpProxyMIB.idxSnmpProxySingleTargetOut, 
                SnmpProxyMIB.colSnmpProxySingleTargetOut);
    public final static FColumn COLUMN_SnmpProxyMultipleTargetOut = 
        new FColumn("snmpProxyMultipleTargetOut",
                SnmpProxyMIB.idxSnmpProxyMultipleTargetOut, 
                SnmpProxyMIB.colSnmpProxyMultipleTargetOut);
    public final static FColumn COLUMN_SnmpProxyStorageType = 
        new FColumn("snmpProxyStorageType",
                SnmpProxyMIB.idxSnmpProxyStorageType, 
                SnmpProxyMIB.colSnmpProxyStorageType);
    public final static FColumn COLUMN_SnmpProxyRowStatus = 
        new FColumn("snmpProxyRowStatus",
                SnmpProxyMIB.idxSnmpProxyRowStatus, 
                SnmpProxyMIB.colSnmpProxyRowStatus);
    
    public SnmpProxyMibF(SnmpProxyMIB mibORIG) {
        super();
        this.mibORIG = mibORIG;
    } // SnmpProxyMibF()

    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        // Tables
        snmpProxyEntry = new FTable("snmpProxyEntry", mibORIG.getSnmpProxyEntry(), agent,
                COLUMN_SnmpProxyType, 
                COLUMN_SnmpProxyContextEngineID, 
                COLUMN_SnmpProxyContextName,
                COLUMN_SnmpProxyTargetParamsIn, 
                COLUMN_SnmpProxySingleTargetOut, 
                COLUMN_SnmpProxyMultipleTargetOut, 
                COLUMN_SnmpProxyStorageType,
                COLUMN_SnmpProxyRowStatus);
        snmpProxyEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpProxyEntry);
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

    public FTable getSnmpProxyEntry() {
        return snmpProxyEntry;
    } // getSnmpProxyEntry()
    
} // class SnmpProxyMibF
