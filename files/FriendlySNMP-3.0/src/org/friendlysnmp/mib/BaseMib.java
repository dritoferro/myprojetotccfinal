/*
 * File: BaseMib.java
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
 * $Id: BaseMib.java,v 1.29 2014/01/17 03:22:25 mg Exp $
 */
package org.friendlysnmp.mib;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FException;
import org.friendlysnmp.FHandler;
import org.friendlysnmp.FID;
import org.friendlysnmp.FNode;
import org.friendlysnmp.FTable;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.smi.OctetString;

/**
 * Base class for MIBs compiled by FriendlyPro. 
 * 
 * @version $Revision: 1.29 $
 */
public abstract class BaseMib {
    //private static final Logger logger = LoggerFactory.getLogger(BaseMib.class);

    /**
     * Agent worker
     */
    private AgentWorker agent;
    
    /**
     * Collection of handlers in this MIB  
     */
    private Set<FHandler> hsHandler;
    
    /**
     * Collection of nodes (scalars, tables and notifications) in this MIB  
     */
    private Set<FNode> hsNode;
    
    /**
     * Default constructor
     */
    public BaseMib() {
        hsHandler = new HashSet<FHandler>();
        hsNode = new HashSet<FNode>();
    } // BaseMib()
    
    /**
     * Adds handler to the MIB
     * 
     * @param handler
     * @throws FException 
     */
    public void addHandler(FHandler handler) throws FException {
        hsHandler.add(handler);
        handler.registerMib(this);
    } // addHandler()
    
    /**
     * Adds node to the MIB
     * 
     * @param node
     */
    protected void addNode(FNode node) {
        hsNode.add(node);
    } // addNode()
    
    /**
     * Collect handlers from this object 
     * 
     * @param c collection handlers from MIB are added to this collection
     */
    public void collectHandlers(Collection<FHandler> c) {
        c.addAll(hsHandler);
    } // collectHandlers()
    
    /**
     * Collect nodes from this object 
     * 
     * @param c collection nodes from MIB are added to this collection
     */
    public void collectNodes(Map<FID, FNode> c) {
        for (FNode node : hsNode) {
            c.put(node.getFID(), node);
        }
    } // collectNodes()
    
    /**
     * Actual implementation is provided in generated MIB-to-Java class.
     * 
     * @param agent SNMP agent
     * @throws FException
     */
    public void init(AgentWorker agent) throws FException {
        this.agent = agent;
    } // init()
    
    /**
     * Checks the state of this MIB object.
     * 
     * @return true if the method {@link #init(AgentWorker)} was called.
     */
    public boolean isInited() {
        return (agent != null);
    } // isInited()
    
    /**
     * Returns SNMP agent. The method name cannot be getAgent() because it
     * may conflict with getters in derived generated classes for
     * declared object "agent" in a MIB.
     * 
     * @return SNMP agent.
     */
    public AgentWorker agent() {
        return agent;
    }
    
    /**
     * Registers objects.
     * 
     * @param server SNMP4J agent
     * @param context context
     * @throws DuplicateRegistrationException
     */
    public abstract void registerMOs(MOServer server, OctetString context)
    throws DuplicateRegistrationException;

    /**
     * Unregisters objects.
     * 
     * @param server SNMP4J server
     * @param context context
     */
    public abstract void unregisterMOs(MOServer server, OctetString context);
    
    /**
     * Sends exception to all registered listeners in the application. 
     * 
     * @param e exception
     * @see <code>FExceptionListener</code>
     */
    public void exceptionThrown(FException e) {
        exceptionThrown((String)null, e);
    } // exceptionThrown()
    
    /**
     * Sends exception to the registered listeners in the application with
     * a message that a problem is found while updating the table.
     * 
     * @param table the table with the exception
     * @param e exception
     */
    public void exceptionThrown(FTable table, FException e) {
        exceptionThrown("Failure to update table " + table.getFIDtoString(), e);
    } // exceptionThrown()
    
    /**
     * Sends exception to the registered listener in the application
     * 
     * @param msg message with details about the exception, or null
     * @param e exception
     */
    public void exceptionThrown(String msg, FException e) {
        agent.exceptionThrown(msg, e);
    } // exceptionThrown()
    
    /**
     * Use this method to report caught exception to MIB browser via SNMP.
     * 
     * @param comment error message (empty or null)
     * @param e exception
     */
    public void reportException(String comment, Throwable e) {
        agent.reportException(comment, e);
    } // reportException()
    
} // class BaseMib
