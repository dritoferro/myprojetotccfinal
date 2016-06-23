/*
 * File: TargetBase.java
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
 * $Id: TargetBase.java,v 1.20 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.target;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FConfig;
import org.friendlysnmp.FConstant;
import org.friendlysnmp.FException;
import org.friendlysnmp.TransportSnmp;
import org.friendlysnmp.TransportDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB.SnmpNotifyTypeEnum;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;

/**
 * This class is a base class for SNMP v2 and v3 targets.
 * 
 * @version $Revision: 1.20 $
 */
public abstract class TargetBase {
    
    /**
     * Logger object 
     */
    private static final Logger logger = LoggerFactory.getLogger(TargetBase.class);

    /** 
     * Type names. Suffixes associated with these names are used to generate
     * names for SNMP tables.  
     */
    enum TypeName {
        CONTEXT(""), 
        SECURITY(""), 
        GROUP("Group"),
        READ_VIEW("ReadView"),
        WRITE_VIEW("WriteView"),
        NOTIFY_VIEW("NotifyView"),
        NOTIFY_TAG("Trap");
        private String suffix;
        private TypeName(String suffix) {
            this.suffix = suffix;
        }
        OctetString createName(TargetBase target) {
            String value;
            if (this == CONTEXT) {
                if (target.secModel == SecurityModel.SECURITY_MODEL_USM) {
                    value = target.agent.getConfig().getConfigString(FConstant.KEY_V3_CONTEXT);
                } else {
                    value = target.targetName;
                }
            } else {
                value = target.targetName + suffix;
            }
            return new OctetString(value);
        }
    } // enum TypeName
    
    /** Notification types wrapper to declaration in SNMP4J */
    enum NotificationType { 
        TRAP  (SnmpNotifyTypeEnum.trap), 
        INFORM(SnmpNotifyTypeEnum.inform);
        private int type;
        private NotificationType(int type) {
            this.type = type;
        }
        int getType() {
            return type;
        }
    } // enum NotificationType
    
    /** A target name is used to generate names in VACM and USM tables */
    private String targetName;
    
    /** SNMP security model */
    private int secModel;
    
    /** SNMP message processing model */
    private int procModel;
    
    /** SNMP security level */
    private int secLevel;
    
    /** SNMP agent */
    protected AgentWorker agent;
    
    /** Configuration object */
    protected FConfig config;    
    
    /** Notification timeout in milliseconds */
    private int notifyTimeoutMls;
    
    /** Notification retry count */
    private int notifyRetryCount;
    
    /**
     * Notification name index.
     * This value is increased by 1 for each new notification name. 
     */
    private int notificationIndex;
    
    /** Storage for Vacm group */
    protected Storage storageVacmGroup;
    /** Storage for Vacm access */
    protected Storage storageVacmAccess;
    /** Storage for Vacm view tree */
    protected Storage storageVacmViewTree;
    /** Storage for notify */
    protected Storage storageNotify;
    
    //--------------------------------separator--------------------------------
    static int ______SYSTEM;
    
    /**
     * Constructor
     * 
     * @param targetName target
     */
    protected TargetBase(String targetName) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Created target '%s' SecModel=%s MsgProcModel=%s SecLevel=%s", 
                targetName, secModel, procModel, secLevel));
        }
        this.targetName = targetName;
        notificationIndex = 0;
    } // TargetBase()
    
    protected void setSNMP(int secModel, int procModel, int secLevel) {
        this.secModel = secModel;
        this.procModel = procModel;
        this.secLevel = secLevel;
    } // setSNMP()
    
    /**
     * Initializes target
     * 
     * @param agent agent
     * 
     * @throws FException exception may be thrown in derived class
     */
    public void init(AgentWorker agent) throws FException {
        if (this.agent != null) {
            throw new FException("Agent is already set.");
        }
        this.agent = agent;
        this.config = agent.getConfig();
        agent.getServer().addContext(TypeName.CONTEXT.createName(this));
        storageVacmGroup = Storage.find(
                config.getConfigString(FConstant.KEY_STORAGE_VACM_GROUP));
        storageVacmAccess = Storage.find(
                config.getConfigString(FConstant.KEY_STORAGE_VACM_ACCESS));
        storageVacmViewTree = Storage.find(
                config.getConfigString(FConstant.KEY_STORAGE_VACM_VIEWTREE));
        storageNotify = Storage.find(
                config.getConfigString(FConstant.KEY_STORAGE_NOTIFY));
        notifyTimeoutMls = config.getConfigInteger(FConstant.KEY_NOTIFY_TIMEOUT_MS);
        notifyRetryCount = config.getConfigInteger(FConstant.KEY_NOTIFY_RETRY_COUNT);
        
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "Loaded target '%s': storageVacmGroup=%s " +
                    "storageVacmAccess=%s storageVacmViewTree=%s " +
                    "storageNotify=%s notifyTimeout=%d notifyRetryCount=%d",
                    targetName, storageVacmGroup, 
                    storageVacmAccess, storageVacmViewTree, 
                    storageNotify, notifyTimeoutMls, notifyRetryCount));
        }
    } // init()

    //--------------------------------separator--------------------------------
    static int ______ACCESS;
    
    /**
     * Adds communities
     * 
     * @param communityMIB community MIB
     */
    public void addCommunities(SnmpCommunityMIB communityMIB) {
        // Default does nothing. TargetV2 will override
    } // addCommunities()
    
    /**
     * Adds VACM views
     * 
     * @param vacm VACM MIB
     */
    public abstract void addViews(VacmMIB vacm);
    
    /**
     * Adds USM user
     * 
     * @param usm USM user
     */
    public void addUsmUser(USM usm) {
        // Default does nothing. TargetV3 will override
    }
    
    /**
     * Adds notification target
     * 
     * @param targetMIB target MIB
     * @param notificationMIB notification MIB
     */
    public abstract void addNotificationTargets(
            SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB);
    
    /**
     * Adds target address to the target MIB
     * 
     * @param targetMIB target MIB
     * @throws FException 
     */
    protected void addTargetAddress(SnmpTargetMIB targetMIB) {
        addTargetAddress(targetMIB, TypeName.NOTIFY_TAG.createName(this)); 
    } // addTargetAddress()
    
    /**
     * Adds target address to the target MIB
     * 
     * @param targetMIB target MIB
     * @param octTagList space delimited tag list in OctetString format
     * @throws FException 
     */
    protected void addTargetAddress(SnmpTargetMIB targetMIB, OctetString octTagList) { 
        // Row in snmpTargerAddrEntry table in SNMP-TARGET-MIB:
        // (in SNMP-APPLICATIONS file)
        targetMIB.addTargetAddress(         // COLUMN
                getTargetAddrName(),        // Name
                getTransportDomainOID(),    // TDomain
                getTransportAddress(),      // TAddress
                notifyTimeoutMls,           // Timeout 
                notifyRetryCount,           // RetryCount
                octTagList,                 // TagList (space delimited)
                getTargetParamsName(),      // Params
                storageNotify.storageType); // StorageType
    } // addTargetAddress()

    /**
     * Adds target parameters to the target MIB
     * 
     * @param targetMIB target MIB
     */
    protected void addTargetParams(SnmpTargetMIB targetMIB) {
        // Row in snmpTargerParamsEntry table in SNMP-TARGET-MIB:
        // (in SNMP-APPLICATIONS file)
        targetMIB.addTargetParams(              // COLUMN
                getTargetParamsName(),          // Name
                getProcessingModel(),           // MPModel
                getSecurityModel(),             // SecurityModel
                TypeName.SECURITY.createName(this), // SecurityName
                getSecurityLevel(),             // SecurityLevel
                storageNotify.storageType);
    } // addTargetParams()
    
    /**
     * Adds notification entry with default tag to notification MIB
     * 
     * @param notificationMIB notification MIB
     * @param type notification type
     */
    protected void addNotificationEntry(
            SnmpNotificationMIB notificationMIB, NotificationType type) 
    {
        addNotificationEntry(notificationMIB, type, TypeName.NOTIFY_TAG.createName(this));
    } // addNotificationEntry()
    
    /**
     * Adds notification entry to notification MIB
     * 
     * @param notificationMIB notification MIB
     * @param type notification type
     * @param octTag tag
     */
    protected void addNotificationEntry(
            SnmpNotificationMIB notificationMIB, NotificationType type, OctetString octTag) 
    {
        // Row in snmpNotifyEntry table in SNMP-NOTIFICATION-MIB:
        // (in SNMP-APPLICATIONS file)
        notificationMIB.addNotifyEntry(   // COLUMN
                createNotificationName(), // Name
                octTag,                   // Tag
                type.getType(),           // Type (trap / inform)
                storageNotify.storageType);
    } // addNotificationEntry()
    
    //--------------------------------separator--------------------------------
    static int ______GETTERS;
    
    public String getTargetName() {
        return targetName;
    } // getTargetName()
    
    /**
     * Returns security model.
     * 
     * @return security model
     */
    protected int getSecurityModel() {
        return secModel; 
    } // getSecurityModel()
    
    /**
     * Return message processing model. 
     * 
     * @return message processing model.
     */
    protected int getProcessingModel() {
        return procModel;
    } // getProcessingModel()

    /**
     * Returns security level.
     * 
     * @return security level.
     */
    protected int getSecurityLevel() {
        return secLevel;
    } // getSecurityLevel()
    
    /** 
     * The method creates read-only "Name" for the column snmpTargetAddrName 
     * in the table snmpTargetMIB.snmpTargetAddrTable. 
     * This name could be any unique. See description in the MIB: 
     * "The locally arbitrary, but unique identifier associated
     * with this snmpTargetAddrEntry."
     * 
     * @return OctetString
     */
    protected OctetString getTargetAddrName() {
        return new OctetString(targetName + "NotifyTarget");
    } // getTargetAddrName()
    
    /**
     * The method creates read-only "Name" which is used in two places 
     * linking them: 
     * <br>1. Column snmpTargetParamsName in the table 
     * snmpTargetMIB.snmpTargetParamsTable
     * <br>2. Column snmpTargetAddrParams in the table 
     * snmpTargetMIB.snmpTargetAddrTable
     * <br><br>The name could be any unique. 
     * See description in the MIB for these two tables.
     * MIB Params table: "The locally arbitrary, but unique identifier associated
     * with this snmpTargetParamsEntry."
     * <br>
     * MIB Addr table: "The value of this object identifies an entry in the
     * snmpTargetParamsTable. The identified entry contains SNMP parameters 
     * to be used when generating messages to be sent to this transport address."
     * 
     * @return OctetString
     */
    protected OctetString getTargetParamsName() {
        return new OctetString(targetName + "NotifyParam");
    } // getTargetParamsName()

    /**
     * Returns notification name which is used to create an entry index 
     * 
     * @return notification name
     */
    protected OctetString createNotificationName() {
        notificationIndex++;
        return new OctetString(targetName + "Notify" + notificationIndex);
    } // createNotificationName()

    /**
     * Returns transport domain OID
     * 
     * @return transport domain OID
     */
    protected OID getTransportDomainOID() {
        TransportSnmp ts = agent.getTransport();
        TransportDomain td = agent.getTransportDomain();
        return ts.getTransportDomainOID(td);
    } // getTransportDomainsOID()

    /**
     * Return transport address
     * 
     * @return transport address
     * @throws FException 
     */
    protected OctetString getTransportAddress() {
        // UDP or TCP makes no difference. Use as a converter: 
        String notifyTarget = agent.getConfig().getNotifyAddress();
        TransportIpAddress addr = new UdpAddress(notifyTarget);
        return new OctetString(addr.getValue());
    } // getTransportAddress()    
    
    /**
     * Returns a string representation of the object.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s TARGET=%s", getClass().getSimpleName(), targetName);        
    } // toString()
    
} // class TargetBase
