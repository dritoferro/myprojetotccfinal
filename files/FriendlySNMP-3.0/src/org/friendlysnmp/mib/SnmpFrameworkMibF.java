/*
 * File: SnmpFrameworkMibF.java
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
 * $Id: SnmpFrameworkMibF.java,v 1.11 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FException;
import org.friendlysnmp.FScalar;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.snmp.SnmpFrameworkMIB;
import org.snmp4j.smi.OctetString;

public class SnmpFrameworkMibF extends BaseMib {

    private SnmpFrameworkMIB mibORIG;

    // Scalars
    private FScalar snmpEngineID;
    private FScalar snmpEngineBoots;
    private FScalar snmpEngineTime;
    private FScalar snmpEngineMaxMessageSize;
    
    public SnmpFrameworkMibF(SnmpFrameworkMIB mibORIG) {
        super();
        this.mibORIG = mibORIG;
    } // SnmpFrameworkMibF()

    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        // Scalars
        snmpEngineID = new FScalar("snmpEngineID", mibORIG.getSnmpEngineID(), agent);
        addNode(snmpEngineID);
        mibORIG.getSnmpEngineBoots().setVolatile(false); // to allow persistency
        snmpEngineBoots = new FScalar("snmpEngineBoots", mibORIG.getSnmpEngineBoots(), agent);
        addNode(snmpEngineBoots);
        snmpEngineTime = new FScalar("snmpEngineTime", mibORIG.getSnmpEngineTime(), agent);
        addNode(snmpEngineTime);
        snmpEngineMaxMessageSize = new FScalar("snmpEngineMaxMessageSize", mibORIG.getSnmpEngineMaxMessageSize(), agent);
        addNode(snmpEngineMaxMessageSize);
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

    public FScalar getSnmpEngineID() {
        return snmpEngineID;
    } // getSnmpEngineID()

    public FScalar getSnmpEngineBoots() {
        return snmpEngineBoots;
    } // getSnmpEngineBoots()
    
    public FScalar getSnmpEngineTime() {
        return snmpEngineTime;
    } // getSnmpEngineTime()
    
    public FScalar getSnmpEngineMaxMessageSize() {
        return snmpEngineMaxMessageSize;
    } // getSnmpEngineMaxMessageSize()

} // class SnmpFrameworkMibF
