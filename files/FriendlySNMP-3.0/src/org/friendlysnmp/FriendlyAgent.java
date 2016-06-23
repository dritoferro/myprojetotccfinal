/*
 * File: FriendlyAgent.java
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
 * $Id: FriendlyAgent.java,v 1.35 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.friendlysnmp.event.FExceptionListener;
import org.friendlysnmp.event.ShutdownListener;
import org.friendlysnmp.event.UncaughtExceptionListener;
import org.friendlysnmp.mib.BaseMib;
import org.friendlysnmp.persist.PersistStorage;
import org.friendlysnmp.target.TargetBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.smi.OID;

/**
 * This agent class is used in Java application to enable SNMP.
 * The code in the application to create the SNMP agent is the following: 
 <blockquote><pre>
 FriendlyAgent agentSNMP = new FriendlyAgent(TITLE, VERSION, prop);
 // Optional UncaughtExceptionListener
 agentSNMP.addUncaughtExceptionListener(new UncaughtExceptionListener() {
     public void uncaughtException(Thread t, Throwable e) {
         [process uncaught exception]
     }
 });
 agentSNMP.initAgent();
 agentSNMP.start();</pre></blockquote>
 * <p>
 * There is no need to shutdown or close the SNMP agent. It catches 
 * JVM shutdown event and closes SNMP connections and agent threads. 
 * 
 * @version $Revision: 1.35 $
 */
public class FriendlyAgent {
    
    /**
     * Logger object.
     */
    private static final Logger logger = LoggerFactory.getLogger(FriendlyAgent.class);
    
    /**
     * Agent private implementation.
     */
    private AgentWorker agentWorker;

    //--------------------------------separator--------------------------------
    static int ______SYSTEM;
    
    /**
     * Constructor.
     * 
     * @param title application title.
     * @param version application version.
     * @param prop properties to initialize agent and with other.
     * application information.
     * 
     * @throws FException
     */
    public FriendlyAgent(String title, String version, Properties prop) throws FException {
        // For log and popup only. Application may reset TITLE and VERSION later:
        logger.info("=== FriendlyAgent init... ===");
        SNMP4JSettings.setForwardRuntimeExceptions(true);
        agentWorker = new AgentWorker(title, version, prop);
        logger.info("=== FriendlyAgent STARTED ===");
    } // FriedlyAgent()  
    
    /**
     * Adds <code>UncaughtExceptionListener</code>
     * 
     * @param l <code>UncaughtExceptionListener</code> object
     */
    public void addUncaughtExceptionListener(UncaughtExceptionListener l) {
        agentWorker.addUncaughtExceptionListener(l);
    } // addUncaughtExceptionListener()
    
    /**
     * Removes <code>UncaughtExceptionListener</code>
     * 
     * @param l <code>UncaughtExceptionListener</code> object
     */
    public void removeUncaughtExceptionListener(UncaughtExceptionListener l) {
        agentWorker.removeUncaughtExceptionListener(l);
    } // removeUncaughtExceptionListener()

    /**
     * Adds <code>FExceptionListener</code>
     * 
     * @param l <code>FExceptionListener</code> object
     */
    public void addFExceptionListener(FExceptionListener l) {
        agentWorker.addFExceptionListener(l);
    } // addFExceptionListener()
    
    /**
     * Removes <code>FExceptionListener</code>
     * 
     * @param l <code>FExceptionListener</code> object
     */
    public void removeFExceptionListener(FExceptionListener l) {
        agentWorker.removeFExceptionListener(l);
    } // removeFExceptionListener()
    
    /**
     * Adds <code>ShutdownListener</code>
     * 
     * @param l <code>ShutdownListener</code> object
     */
    public void addShutdownListener(ShutdownListener l) {
        agentWorker.addShutdownListener(l);
    } // addShutdownListener()
    
    /**
     * Removes <code>ShutdownListener</code>
     * 
     * @param l <code>ShutdownListener</code> object
     */
    public void removeShutdownListener(ShutdownListener l) {
        agentWorker.removeShutdownListener(l);
    } // removeShutdownListener()
    
    /**
     * Method to add user defined MIB to the agent. 
     * Call this method multiple times for each additional MIB.
     * This method must be called before agent starts.
     * 
     * @param mib MIB object
     * @throws FException 
     */
    public void addMIB(BaseMib mib) throws FException {
        agentWorker.addMIB(mib);
    } // addMIB()

    /**
     * Adds targets {@link TargetBase} to the agent. 
     * 
     * <p>Usually targets are added via properties file, 
     * see {@link FConstant#KEY_V1_COMMUNITY},
     * {@link FConstant#KEY_V2_COMMUNITY} and {@link FConstant#KEY_V3_USER}, 
     * Alternative technique is to derive class from 
     * {@link TargetBase} class, implement required functionality and 
     * add target with this method.
     * 
     * @param target target object. 
     * @throws FException
     */
    public void addTarget(TargetBase target) throws FException {
        agentWorker.addTarget(target);
    } // addTarget()
    
    /**
     * Initializes SNMP agent. This is a complex procedure (in comparison to 
     * simple {@link #start()})
     * 
     * @throws FException wrapper exception with a cause exception which
     * may be IOException while loading persistence or properties problem, 
     * DuplicateRegistrationException while loading managed objects,
     * BindException if address is already in use, FException for
     * loading initial values and initialization.
     */
    public void init() throws FException {
        // Do not call org.snmp4j.agent.BaseAgent.init()!!
        try {
            agentWorker.initAgent(); // <-- modified & simplified BaseAgent.init()
        } catch (Exception e) {
            // DuplicateRegistrationException 
            // IOException
            // BindException: Address already in use: Cannot bind
            throw new FException("Failure to init SNMP agent", e);
        } 
    } // init()

    /**
     * Starts SNMP agent.
     * @throws FException 
     */
    public void start() throws FException {
        agentWorker.startAgent();
    } // start()
    
    /**
     * Stops SNMP agent.
     * @throws FException
     */
    public void stop() throws FException {
        agentWorker.stopAgent();
    }
    
    /**
     * Returns state of the SNMP agent.
     * @return boolean flag: agent is in running state
     */
    public boolean isRunning() {
        return agentWorker.isRunning();
    }
    
    /**
     * Shutdowns SNMP agent. 
     */
    public void shutdown() {
        agentWorker.shutdown("explicit call");
    } // shutdown()
    
    /**
     * Returns node registered in one of agent's mib 
     * 
     * @param oid node OID
     * @return node or null if not found
     */
    public FNode getNode(OID oid) {
        return agentWorker.getNode(oid);
    } // getNode()
    
    /**
     * Returns scalar registered in one of agent's mib 
     * 
     * @param oid node OID
     * @return node or null if not found
     */
    public FScalar getScalar(OID oid) {
        return agentWorker.getScalar(oid);
    } // getNode()
    
    /**
     * Returns table registered in one of agent's mib 
     * 
     * @param oid node OID
     * @return node or null if not found
     */
    public FTable getTable(OID oid) {
        return agentWorker.getTable(oid);
    }
    
    /**
     * Returns notification registered in one of agent's mib 
     * 
     * @param oid node OID
     * @return node or null if not found
     */
    public FNotification getNotification(OID oid) {
        return agentWorker.getNotification(oid);
    }
    
    /**
     * Sets persistence storage
     * 
     * @param persistStorage persistence storage
     * @throws FException 
     */
    public void setPersistStorage(PersistStorage persistStorage) throws FException {
        agentWorker.setPersistStorage(persistStorage);
    } // setPersistStorage()
    
    /**
     * Returns persistence storage
     * 
     * @return persistence storage
     */
    public PersistStorage getPersistStorage() {
        return agentWorker.getPersistStorage();
    } // getPersistStorage()
    
    /**
     * Use this method to report caught exception to MIB browser via SNMP.
     * 
     * @param comment error message (empty or null)
     * @param e exception
     */
    public void reportException(String comment, Throwable e) {
        agentWorker.reportException(comment, e);
    } // reportException()
    
    /**
     * Helper method to load properties file
     * 
     * @param clazz use this class classloader to load properties 
     * @param filename properties file name
     * @return properties object
     * @throws IOException
     */
    public static Properties loadProps(Class<?> clazz, String filename) throws IOException {
        InputStream is = null;
        try {
            // Try to find prop-file in the root of JAR using classloader:
            is = clazz.getResourceAsStream("/" + filename);
            if (is == null) {
                // Classloader failed to find prop-file in JAR.
                // Try to find prop-file in a file system:
                is = new FileInputStream(filename); // throws FileNotFoundException
            }
            Properties prop = new Properties();
            prop.load(is);
            is.close();
            logger.debug("Loaded prop: " + prop);
            return prop;
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) { }
            }
        }
    } // loadProps()
    
} // class FriendlyAgent
