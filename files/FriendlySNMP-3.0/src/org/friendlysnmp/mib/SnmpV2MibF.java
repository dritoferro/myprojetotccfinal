/*
 * File: SnmpV2MibF.java
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
 * $Id: SnmpV2MibF.java,v 1.18 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FColumn;
import org.friendlysnmp.FException;
import org.friendlysnmp.FNotification;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.event.FRestoreDefaultEvent;
import org.friendlysnmp.event.FRestoreDefaultListener;
import org.friendlysnmp.event.FScalarSetListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.snmp.SNMPv2MIB;
import org.snmp4j.agent.mo.snmp.SNMPv2MIB.SnmpEnableAuthenTrapsEnum;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

public class SnmpV2MibF extends BaseMib {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SnmpV2MibF.class);

    private SNMPv2MIB mibORIG;

    /** Duplicate to private org.snmp4j.agent.mo.snmp.SNMPv2MIB */
    public static final OID oidSysORLastChange =
        new OID(new int[] { 1,3,6,1,2,1,1,8,0 });

    /** Duplicate to private org.snmp4j.agent.mo.snmp.SNMPv2MIB */
    public static final OID oidSnmpEnableAuthenTraps =
        new OID(new int[] { 1,3,6,1,2,1,11,30,0 });

    // Column sub-identifier definitions for sysOREntry:
    private static final int colSysORID = 2;
    private static final int colSysORDescr = 3;
    private static final int colSysORUpTime = 4;

    // Column index definitions for sysOREntry:
    private static final int idxSysORID = 0;
    private static final int idxSysORDescr = 1;
    private static final int idxSysORUpTime = 2;
    
    // Scalars
    private FScalar sysDescr;
    private FScalar sysUpTime;
    private FScalar sysObjectID;
    private FScalar sysContact;
    private FScalar sysName;
    private FScalar sysLocation;
    private FScalar sysServices;
    private FScalar sysORLastChange;
    private FScalar snmpInPkts;
    private FScalar snmpInBadVersions;
    private FScalar snmpInBadCommunityNames;
    private FScalar snmpInBadCommunityUses;
    private FScalar snmpInASNParseErrs;
    private FScalar snmpEnableAuthenTraps;
    private FScalar snmpSilentDrops;
    private FScalar snmpProxyDrops;
    private FScalar snmpSetSerialNo;

    // Tables
    private FTable sysOREntry;
    
    // Columns for table sysOREntry
    public final static FColumn COLUMN_SysORID = 
        new FColumn("sysORID",
                idxSysORID, 
                colSysORID);
    public final static FColumn COLUMN_SysORDescr = 
        new FColumn("sysORDescr",
                idxSysORDescr, 
                colSysORDescr);
    public final static FColumn COLUMN_SysORUpTime = 
        new FColumn("sysORUpTime",
                idxSysORUpTime, 
                colSysORUpTime);
    
    // Notifications
    private FNotification coldStart;
    private FNotification warmStart;
    private FNotification authenticationFailure;
    
    public SnmpV2MibF(SNMPv2MIB mibORIG) {
        super();
        this.mibORIG = mibORIG;
    } // SnmpV2MibF()

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
        sysDescr = new FScalar("sysDescr", srv.getMOScalar(SnmpConstants.sysDescr), agent);
        addNode(sysDescr);
        sysUpTime = new FScalar("sysUpTime", srv.getMOScalar(SnmpConstants.sysUpTime), agent);
        addNode(sysUpTime);
        sysObjectID = new FScalar("sysObjectID", srv.getMOScalar(SnmpConstants.sysObjectID), agent);
        addNode(sysObjectID);
        sysContact = new FScalar("sysContact", srv.getMOScalar(SnmpConstants.sysContact), agent);
        sysContact.setVolatile(false); // loads persistent value (if exist)
        if (!sysContact.isPersistLoaded()) {
            setDefaultSysContact();
        }
        sysContact.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        sysContact.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSysContact();
            }
        });
        addNode(sysContact);
        sysName = new FScalar("sysName", srv.getMOScalar(SnmpConstants.sysName), agent);
        sysName.setVolatile(false); // loads persistent value (if exist)
        if (!sysName.isPersistLoaded()) {
            setDefaultSysName();
        }
        sysName.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        sysName.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSysName();
            }
        });
        addNode(sysName);
        sysLocation = new FScalar("sysLocation", srv.getMOScalar(SnmpConstants.sysLocation), agent);
        sysLocation.setVolatile(false); // loads persistent value (if exist)
        if (!sysLocation.isPersistLoaded()) {
            setDefaultSysLocation();
        }
        sysLocation.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        sysLocation.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSysLocation();
            }
        });
        addNode(sysLocation);
        sysServices = new FScalar("sysServices", srv.getMOScalar(SnmpConstants.sysServices), agent);
        addNode(sysServices);
        sysORLastChange = new FScalar("sysORLastChange", srv.getMOScalar(oidSysORLastChange), agent);
        addNode(sysORLastChange);
        snmpInPkts = new FScalar("snmpInPkts", srv.getMOScalar(SnmpConstants.snmpInPkts), agent);
        addNode(snmpInPkts);
        snmpInBadVersions = new FScalar("snmpInBadVersions", srv.getMOScalar(SnmpConstants.snmpInBadVersions), agent);
        addNode(snmpInBadVersions);
        snmpInBadCommunityNames = new FScalar("snmpInBadCommunityNames", srv.getMOScalar(SnmpConstants.snmpInBadCommunityNames), agent);
        addNode(snmpInBadCommunityNames);
        snmpInBadCommunityUses = new FScalar("snmpInBadCommunityUses", srv.getMOScalar(SnmpConstants.snmpInBadCommunityUses), agent);
        addNode(snmpInBadCommunityUses);
        snmpInASNParseErrs = new FScalar("snmpInASNParseErrs", srv.getMOScalar(SnmpConstants.snmpInASNParseErrs), agent);
        addNode(snmpInASNParseErrs);
        snmpEnableAuthenTraps = new FScalar("snmpEnableAuthenTraps", srv.getMOScalar(oidSnmpEnableAuthenTraps), agent);
        snmpEnableAuthenTraps.setVolatile(false); // loads persistent value (if exist)
        if (!snmpEnableAuthenTraps.isPersistLoaded()) {
            setDefaultEnableAuthenTraps();
        }
        snmpEnableAuthenTraps.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        snmpEnableAuthenTraps.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultEnableAuthenTraps();
            }
        });
        
        addNode(snmpEnableAuthenTraps);
        snmpSilentDrops = new FScalar("snmpSilentDrops", 
                srv.getMOScalar(SnmpConstants.snmpSilentDrops), agent);
        addNode(snmpSilentDrops);
        snmpProxyDrops = new FScalar("snmpProxyDrops", 
                srv.getMOScalar(SnmpConstants.snmpProxyDrops), agent);
        addNode(snmpProxyDrops);
        snmpSetSerialNo = new FScalar("snmpSetSerialNo", 
                srv.getMOScalar(SnmpConstants.snmpSetSerialNo), agent);
        snmpSetSerialNo.setVolatile(false); // loads persistent value (if exist)
        if (!snmpSetSerialNo.isPersistLoaded()) {
            setDefaultSetSerialNo();
        }
        snmpSetSerialNo.addSetListener(new FScalarSetListener() {
            @Override
            public void set(FScalar scalar) {
                try {
                    agent().getPersistStorage().put(scalar);
                } catch (FException e) {
                    logger.error("Failure to persist " + scalar.getFIDtoString());
                }
            }
        });
        snmpSetSerialNo.addRestoreDefaultListener(new FRestoreDefaultListener() {
            @Override
            public void restoreDefault(FRestoreDefaultEvent ev) throws FException {
                setDefaultSetSerialNo();
            }
        });
        addNode(snmpSetSerialNo);
        
        // Tables
        sysOREntry = new FTable("sysOREntry", 
                srv.getMOTable(SnmpConstants.sysOREntry), agent,
                COLUMN_SysORID,
                COLUMN_SysORDescr,
                COLUMN_SysORUpTime);
        addNode(sysOREntry);
        
        // Notifications
        coldStart = new FNotification("coldStart", 
                SnmpConstants.coldStart, agent);
        addNode(coldStart);
        warmStart = new FNotification("warmStart", 
                SnmpConstants.warmStart, agent);
        addNode(warmStart);
        authenticationFailure = new FNotification("authenticationFailure", 
                SnmpConstants.authenticationFailure, agent);
        addNode(authenticationFailure);
    } // init()

    private void setDefaultSysContact() throws FException {
        sysContact.setValue("");
        logger.debug("Default SysContact: <empty>");
    }
    
    private void setDefaultSysName() throws FException {
        sysName.setValue("");
        logger.debug("Default SysName: <empty>");
    }
    
    private void setDefaultSysLocation() throws FException {
        sysLocation.setValue("");
        logger.debug("Default SysLocation: <empty>");
    }

    private void setDefaultEnableAuthenTraps() throws FException {
        snmpEnableAuthenTraps.setValue(SnmpEnableAuthenTrapsEnum.enabled);
        logger.debug("Default EnableAuthenTraps: enabled");
    }
    
    private void setDefaultSetSerialNo() throws FException {
        snmpSetSerialNo.setValue(0);
        logger.debug("Default SetSerialNo: 0");
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

    public FScalar getSysDescr() {
        return sysDescr;
    } // getSysDescr()

    public FScalar getSysUpTime() {
        return sysUpTime;
    } // getSysUpTime()
    
    public FScalar getSysObjectID() {
        return sysObjectID;
    } // getSysObjectID()
    
    public FScalar getSysContact() {
        return sysContact;
    } // getSysContact()
    
    public FScalar getSysName() {
        return sysName;
    } // getSysName()
    
    public FScalar getSysLocation() {
        return sysLocation;
    } // getSysLocation()
    
    public FScalar getSysServices() {
        return sysServices;
    } // getSysServices()

    public FScalar getSysORLastChange() {
        return sysORLastChange;
    } // getSysORLastChange()
    
    public FScalar getSnmpInPkts() {
        return snmpInPkts;
    } // getSnmpInPkts()
    
    public FScalar getSnmpInBadVersions() {
        return snmpInBadVersions;
    } // getSnmpInBadVersions()
    
    public FScalar getSnmpInBadCommunityNames() {
        return snmpInBadCommunityNames;
    } // getSnmpInBadCommunityNames()
    
    public FScalar getSnmpInBadCommunityUses() {
        return snmpInBadCommunityUses;
    } // getSnmpInBadCommunityUses()
    
    public FScalar getSnmpInASNParseErrs() {
        return snmpInASNParseErrs;
    } // getSnmpInASNParseErrs()
    
    public FScalar getSnmpEnableAuthenTraps() {
        return snmpEnableAuthenTraps;
    } // getSnmpEnableAuthenTraps()
    
    public FScalar getSnmpSilentDrops() {
        return snmpSilentDrops;
    } // getSnmpSilentDrops()
    
    public FScalar getSnmpProxyDrops() {
        return snmpProxyDrops;
    } // getSnmpProxyDrops()
    
    public FScalar getSnmpSetSerialNo() {
        return snmpSetSerialNo;
    } // getSnmpSetSerialNo()
    
    public FTable getSysOREntry() {
        return sysOREntry;
    } // getSysOREntry()
    
    public FNotification getColdStart() {
        return coldStart;
    } // getColdStart()

    public FNotification getWarmStart() {
        return warmStart;
    } // getWarmStart()

    public FNotification getAuthenticationFailure() {
        return authenticationFailure;
    } // getAuthenticationFailure()

} // class SnmpV2MibF
