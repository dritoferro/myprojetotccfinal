/*
 * File: TargetV2.java
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
 * $Id: TargetV1.java,v 1.10 2014/01/11 02:27:21 mg Exp $
 */
package org.friendlysnmp.target;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FConfig;
import org.friendlysnmp.FConstant;
import org.friendlysnmp.FException;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

/**
 * SNMP v1 target implementation.
 * 
 * @version $Revision: 1.10 $
 */
public class TargetV1 extends TargetBase {

    private Storage storageCommunity;
    
    /**
     * Constructor
     * 
     * @param targetName target (V1 community name)
     */
    public TargetV1(String targetName) {
        super(targetName);
        setSNMP(SecurityModel.SECURITY_MODEL_SNMPv1, 
                MessageProcessingModel.MPv1, 
                SecurityLevel.NOAUTH_NOPRIV);
    } // TargetV1()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#init(org.friendlysnmp.AgentWorker)
     */
    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        FConfig config = agent.getConfig();
        storageCommunity = Storage.find(
                config.getConfigString(FConstant.KEY_STORAGE_COMMUNITY));
    } // init()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#addViews(org.snmp4j.agent.mo.snmp.VacmMIB)
     */
    @Override
    public void addViews(VacmMIB vacm) {
        // Table vacmSecurityToGroupEntry in SNMP-VIEW-BASED-ACM-MIB:
        vacm.addGroup(
                getSecurityModel(),
                TypeName.SECURITY.createName(this),
                TypeName.GROUP.createName(this),
                storageVacmGroup.storageType);
        // Table vacmAccessEntry in SNMP-VIEW-BASED-ACM-MIB:
        vacm.addAccess(
                TypeName.GROUP.createName(this),
                TypeName.CONTEXT.createName(this), // context prefix
                getSecurityModel(),
                getSecurityLevel(),
                MutableVACM.VACM_MATCH_EXACT,
                TypeName.READ_VIEW.createName(this),
                TypeName.WRITE_VIEW.createName(this),
                TypeName.NOTIFY_VIEW.createName(this),
                storageVacmAccess.storageType);
        // Table vacmViewTreeFamilyEntry in SNMP-VIEW-BASED-ACM-MIB:
        vacm.addViewTreeFamily(
                TypeName.READ_VIEW.createName(this),
                new OID(config.getConfigString(FConstant.KEY_OID_ROOT_READ_VIEW)),
                new OctetString(), 
                VacmMIB.vacmViewIncluded,
                storageVacmViewTree.storageType);
        vacm.addViewTreeFamily(
                TypeName.WRITE_VIEW.createName(this),
                new OID(config.getConfigString(FConstant.KEY_OID_ROOT_WRITE_VIEW)),
                new OctetString(), 
                VacmMIB.vacmViewIncluded,
                storageVacmViewTree.storageType);
        vacm.addViewTreeFamily(
                TypeName.NOTIFY_VIEW.createName(this),
                new OID(config.getConfigString(FConstant.KEY_OID_ROOT_NOTIFY_VIEW)),
                new OctetString(), 
                VacmMIB.vacmViewIncluded,
                storageVacmViewTree.storageType);
    } // addViews()

    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#addCommunities(org.snmp4j.agent.mo.snmp.SnmpCommunityMIB)
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addCommunities(SnmpCommunityMIB communityMIB) {
        // Table snmpCommunityEntry in SNMP-COMMUNITY-MIB:
        Variable[] com2sec = new Variable[] {
                TypeName.CONTEXT.createName(this),     // community name
                TypeName.SECURITY.createName(this),    // security name
                agent.getAgent().getContextEngineID(), // local engine ID
                TypeName.CONTEXT.createName(this),     // default context name
                new OctetString(),                     // transport tag
                new Integer32(storageCommunity.storageType), // storage type
                new Integer32(RowStatus.active)        // row status
            };
        OctetString octRowName = TypeName.CONTEXT.createName(this);
        octRowName.append("2");
        octRowName.append(TypeName.CONTEXT.createName(this));
        MOTable moTable = communityMIB.getSnmpCommunityEntry();
        MOTableRow moRow = moTable.createRow(octRowName.toSubIndex(true), com2sec);
        moTable.addRow(moRow);
    } // addCommunities()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#addNotificationTargets(org.snmp4j.agent.mo.snmp.SnmpTargetMIB, org.snmp4j.agent.mo.snmp.SnmpNotificationMIB)
     */
    @Override
    public void addNotificationTargets(
            SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) 
    {
        addTargetAddress(targetMIB);
        addTargetParams(targetMIB);
        addNotificationEntry(notificationMIB, NotificationType.TRAP);
    } // addNotificationTargets()

} // class TargetV1
