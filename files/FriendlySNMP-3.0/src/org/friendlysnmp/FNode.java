/*
 * File: FNode.java
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
 * $Id: FNode.java,v 1.14 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

import org.friendlysnmp.persist.PersistStorage;
import org.snmp4j.smi.OID;

/**
 * Abstract base class which represents MIB node (scalar, table, notification).
 * 
 * @version $Revision: 1.14 $
 */
public abstract class FNode {
    
    private FID fid;
    
    /**
     * SNMP agent 
     */
    protected AgentWorker agent;
    
    /**
     * Constructs MIB node object
     * 
     * @param name name of the object
     * @param oid OID of the object
     */
    public FNode(String name, OID oid) {
        // Node _always_ has params pair "name + oid" and ctor has them 
        // in this order to make code readable.
        // Base class FID has "name" parameter optional and the params order
        // is reversed.
        fid = new FID(oid, name);
    } // FNode()

    /**
     * Sets the agent.
     * 
     * @param agent SNMP agent
     */
    public void setAgent(AgentWorker agent) {
        this.agent = agent;
    } // setAgent()
    
    /**
     * Returns persistence storage object.
     *  
     * @return persistence storage object
     */
    public PersistStorage getPersistStorage() {
        return agent.getPersistStorage();
    } // getPersistStorage()
    
    /**
     * Returns copy of internal OID
     * 
     * @return copy of internal OID
     */
    public OID getOID() {
        return fid.getOID();
    }
    
    /**
     * Returns name
     * 
     * @return name
     */
    public String getName() {
        return fid.getName();
    }
    
    /**
     * Returns FID
     * 
     * @return FID
     */
    public FID getFID() {
        return fid.cloneFID();
    }
    
    /**
     * Returns internal OID + name string.
     * 
     * @return string in format "OID + name" convenient for logging. 
     */
    public String getFIDtoString() {
        return fid.getFIDtoString();
    } // getFIDtoString()
    
    /**
     * Returns size of internal OID
     * 
     * @return size of internal OID
     */
    public int getOIDSize() {
        return fid.size();
    }
    
    /**
     * Method to throw FException with message appended with object name
     * @param msg error message
     * @throws FException
     */
    protected void throwFException(String msg) throws FException {
        throw new FException("%s (%s)", msg, getFIDtoString());
    }
    
    /**
     * Method to throw FException with message appended with object name
     * @param msg error message
     * @param cause exception cause
     * @throws FException
     */
    protected void throwFException(String msg, Throwable cause) throws FException {
        throw new FException(String.format("%s (%s)", msg, getFIDtoString()), cause);
    }
    
} // class FNode
