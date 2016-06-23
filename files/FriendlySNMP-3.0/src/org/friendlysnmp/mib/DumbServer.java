/*
 * File: DumbServer.java
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
 * $Id: DumbServer.java,v 1.15 2014/10/29 19:41:34 mg Exp $
 */
package org.friendlysnmp.mib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.snmp4j.agent.ContextListener;
import org.snmp4j.agent.MOQuery;
import org.snmp4j.agent.MOScope;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.MOServerLookupListener;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

/**
 * Many managed objects in the SNMP4J MIBs are declared private without
 * get-methods. 
 * This class extracts them from SNMP4J MIBs via their registration. 
 * 
 * @version $Revision: 1.15 $
 */
public class DumbServer implements MOServer {

    private HashMap<OID, ManagedObject> hmMO = new HashMap<OID, ManagedObject>(); 
    
    @Override
    public void addContext(OctetString context) {
    }

    @Override
    public void addContextListener(ContextListener listener) {
    }

    @Override
    public void addLookupListener(MOServerLookupListener listener,
            ManagedObject mo) {
    }

    @Override
    public OctetString[] getContexts() {
        return null;
    }

    @Override
    public boolean isContextSupported(OctetString context) {
        return false;
    }

    @Override
    public Iterator<Map.Entry<MOScope, ManagedObject>> iterator() {
        return null;
    }

    @Override
    public ManagedObject lookup(MOQuery query) {
        return null;
    }

    @Override
    public void register(ManagedObject mo, OctetString context) {
        if (mo instanceof MOScalar) {
            hmMO.put(((MOScalar<?>)mo).getID(), mo);
        }
        if (mo instanceof MOTable) {
            hmMO.put(((MOTable<?,?,?>)mo).getOID(), mo);
        }
    }

    @Override
    public void removeContext(OctetString context) {
    }

    @Override
    public void removeContextListener(ContextListener listener) {
    }

    @Override
    public boolean removeLookupListener(MOServerLookupListener listener,
            ManagedObject mo) 
    {
        return false;
    }

    @Override
    public void unlock(Object owner, ManagedObject managedObject) {
    }

    @Override
    public ManagedObject unregister(ManagedObject mo, OctetString context) {
        return null;
    }

    public MOScalar<?> getMOScalar(OID oid) {
        ManagedObject mo = hmMO.get(oid); 
        return (mo instanceof MOScalar ? (MOScalar<?>)mo : null);
    }
    
    public MOTable<?,?,?> getMOTable(OID oid) {
        ManagedObject mo = hmMO.get(oid); 
        return (mo instanceof MOTable ? (MOTable<?,?,?>)mo : null);
    }

    @Override
    public boolean lock(Object owner, ManagedObject managedObject,
           long timeoutMillis) {
        return false;
    }

    @Override
    public boolean lock(Object owner, ManagedObject managedObject) {
        return false;
    }

    @Override
    public OctetString[] getRegisteredContexts(ManagedObject managedObject) {
        return null;
    }
    
} // class DumbServer
