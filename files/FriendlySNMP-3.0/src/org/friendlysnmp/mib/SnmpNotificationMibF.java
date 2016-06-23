/*
 * File: SnmpNotificationMibF.java
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
 * $Id: SnmpNotificationMibF.java,v 1.9 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FTable;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.smi.OctetString;

public class SnmpNotificationMibF extends BaseMib {

    private SnmpNotificationMIB mibORIG;

    // Tables
    private FTable snmpNotifyEntry;
    private FTable snmpNotifyFilterEntry;
    private FTable snmpNotifyFilterProfileEntry;
    
    // Columns for table snmpNotifyEntry
    public final static FColumn COLUMN_SnmpNotifyTag = 
        new FColumn("snmpNotifyTag",
                SnmpNotificationMIB.idxSnmpNotifyTag, 
                SnmpNotificationMIB.colSnmpNotifyTag);
    public final static FColumn COLUMN_SnmpNotifyType = 
        new FColumn("snmpNotifyType",
                SnmpNotificationMIB.idxSnmpNotifyType, 
                SnmpNotificationMIB.colSnmpNotifyType);
    public final static FColumn COLUMN_SnmpNotifyStorageType = 
        new FColumn("snmpNotifyStorageType",
                SnmpNotificationMIB.idxSnmpNotifyStorageType, 
                SnmpNotificationMIB.colSnmpNotifyStorageType);
    public final static FColumn COLUMN_SnmpNotifyRowStatus = 
        new FColumn("snmpNotifyRowStatus",
                SnmpNotificationMIB.idxSnmpNotifyRowStatus, 
                SnmpNotificationMIB.colSnmpNotifyRowStatus);

    // Columns for table snmpNotifyFilterEntry
    public final static FColumn COLUMN_SnmpNotifyFilterMask = 
        new FColumn("snmpNotifyFilterMask",
                SnmpNotificationMIB.idxSnmpNotifyFilterMask, 
                SnmpNotificationMIB.colSnmpNotifyFilterMask);
    public final static FColumn COLUMN_SnmpNotifyFilterType = 
        new FColumn("snmpNotifyFilterType",
                SnmpNotificationMIB.idxSnmpNotifyFilterType, 
                SnmpNotificationMIB.colSnmpNotifyFilterType);
    public final static FColumn COLUMN_SnmpNotifyFilterStorageType = 
        new FColumn("snmpNotifyFilterStorageType",
                SnmpNotificationMIB.idxSnmpNotifyFilterStorageType, 
                SnmpNotificationMIB.colSnmpNotifyFilterStorageType);
    public final static FColumn COLUMN_SnmpNotifyFilterRowStatus = 
        new FColumn("snmpNotifyFilterRowStatus",
                SnmpNotificationMIB.idxSnmpNotifyFilterRowStatus, 
                SnmpNotificationMIB.colSnmpNotifyFilterRowStatus);
    
    // Columns for table snmpNotifyFilterProfileEntry
    public final static FColumn COLUMN_SnmpNotifyFilterProfileName = 
        new FColumn("snmpNotifyFilterProfileName",
                SnmpNotificationMIB.idxSnmpNotifyFilterProfileName, 
                SnmpNotificationMIB.colSnmpNotifyFilterProfileName);
    public final static FColumn COLUMN_SnmpNotifyFilterProfileStorType = 
        new FColumn("snmpNotifyFilterProfileStorType",
                SnmpNotificationMIB.idxSnmpNotifyFilterProfileStorType, 
                SnmpNotificationMIB.colSnmpNotifyFilterProfileStorType);
    public final static FColumn COLUMN_SnmpNotifyFilterProfileRowStatus = 
        new FColumn("snmpNotifyFilterProfileRowStatus",
                SnmpNotificationMIB.idxSnmpNotifyFilterProfileRowStatus, 
                SnmpNotificationMIB.colSnmpNotifyFilterProfileRowStatus);
    
    public SnmpNotificationMibF(SnmpNotificationMIB mibORIG) {
        super();
        this.mibORIG = mibORIG;
    } // SnmpNotificationMibF()

    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        // Tables
        snmpNotifyEntry = new FTable("snmpNotifyEntry", mibORIG.getNotifyTable(), agent,
                COLUMN_SnmpNotifyTag, 
                COLUMN_SnmpNotifyType, 
                COLUMN_SnmpNotifyStorageType, 
                COLUMN_SnmpNotifyRowStatus); 
        snmpNotifyEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpNotifyEntry);
        
        snmpNotifyFilterEntry = new FTable("snmpNotifyFilterEntry", mibORIG.getNotifyFilterTable(), agent,
                COLUMN_SnmpNotifyFilterMask, 
                COLUMN_SnmpNotifyFilterType, 
                COLUMN_SnmpNotifyFilterStorageType, 
                COLUMN_SnmpNotifyFilterRowStatus); 
        snmpNotifyFilterEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpNotifyFilterEntry);
        
        snmpNotifyFilterProfileEntry = new FTable("snmpNotifyFilterProfileEntry", mibORIG.getNotifyFilterProfileTable(), agent,
                COLUMN_SnmpNotifyFilterProfileName, 
                COLUMN_SnmpNotifyFilterProfileStorType, 
                COLUMN_SnmpNotifyFilterProfileRowStatus); 
        snmpNotifyFilterProfileEntry.setVolatile(false); // loads persistent value (if exist)
        addNode(snmpNotifyFilterProfileEntry);
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

    public FTable getSnmpNotifyEntry() {
        return snmpNotifyEntry;
    } // getSnmpNotifyEntry()
    
    public FTable getSnmpNotifyFilterEntry() {
        return snmpNotifyFilterEntry;
    } // getSnmpNotifyFilterEntry()
    
    public FTable getSnmpNotifyFilterProfileEntry() {
        return snmpNotifyFilterProfileEntry;
    } // getSnmpNotifyFilterProfileEntry()
    
} // class SnmpNotificationMibF
