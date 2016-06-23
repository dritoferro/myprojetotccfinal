/*
 * File: AgentWorker.java
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
 * $Id: AgentWorker.java,v 1.78 2014/01/22 23:02:20 mg Exp $
 */
package org.friendlysnmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.friendlysnmp.FHandler.AgentStartType;
import org.friendlysnmp.event.FExceptionListener;
import org.friendlysnmp.event.ShutdownListener;
import org.friendlysnmp.event.UncaughtExceptionListener;
import org.friendlysnmp.mib.BaseMib;
import org.friendlysnmp.mib.SnmpCommunityMibF;
import org.friendlysnmp.mib.SnmpFrameworkMibF;
import org.friendlysnmp.mib.SnmpNotificationMibF;
import org.friendlysnmp.mib.SnmpProxyMibF;
import org.friendlysnmp.mib.SnmpTargetMibF;
import org.friendlysnmp.mib.SnmpUsmMibF;
import org.friendlysnmp.mib.SnmpV2MibF;
import org.friendlysnmp.mib.SnmpVacmMibF;
import org.friendlysnmp.persist.PersistPolicy;
import org.friendlysnmp.persist.PersistStorage;
import org.friendlysnmp.persist.PersistStorageImpl;
import org.friendlysnmp.plugin.FPlugin;
import org.friendlysnmp.plugin.core.FriendlySnmpMib.FriendlyExceptionTypeTC;
import org.friendlysnmp.plugin.core.PluginCore;
import org.friendlysnmp.target.TargetBase;
import org.friendlysnmp.target.TargetV1;
import org.friendlysnmp.target.TargetV2;
import org.friendlysnmp.target.TargetV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DefaultMOServer;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.ProxyForwarder;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.NotificationOriginatorImpl;
import org.snmp4j.agent.mo.snmp.ProxyForwarderImpl;
import org.snmp4j.agent.mo.snmp.SNMPv2MIB;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpFrameworkMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpProxyMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.UsmMIB;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.util.WorkerPool;

/**
 * This class is an SNMP4J agent proxy. 
 * It initializes underlying SNMP4J agent and registers MIBs.
 * A user could use this class directly or via (recommended) a friendly 
 * agent class {@link FriendlyAgent} which exposes minimal set of publicly 
 * available methods.
 * 
 * @version $Revision: 1.78 $
 * @see FriendlyAgent
 */
public class AgentWorker 
{
    /** Logger object */
    private static final Logger logger = LoggerFactory.getLogger(AgentWorker.class);
    
    /** 
     * Agent states. 
     * (similar to agent state variables in org.snmp4j.agent.BaseAgent)
     */
    private enum AgentState {
        CREATED, 
        INIT_START, 
        INIT_FINISH, 
        RUNNING, 
        STOPPED, 
        SHUTDOWN
    } // enum AgentState
    
    /** Enumeration of exception types */
    public enum ExceptionType { 
        CAUGHT(FriendlyExceptionTypeTC.caught), 
        UNCAUGHT(FriendlyExceptionTypeTC.uncaught);
        private int type;
        ExceptionType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }
    } // enum ExceptionType
    
    /** Agent state */
    private AgentState stateAgent;
    
    /** Transport */
    private TransportSnmp transport;
    
    /** Transport domain */
    private TransportDomain transportDomain;
    
    /** Collection of registered MIBs */
    private List<BaseMib> lstMIB;
    
    /** Collection of handlers in this agent */
    private List<FHandler> lstHandler;
    
    /** Collection of all nodes in this agent */
    private Map<FID, FNode> hmNode;
    
    /** Core plugin */
    private PluginCore pluginCore;

    /** Collection with optional plugins */
    private List<FPlugin> lstPlugin;
    
    /** Default persistence storage */
    private PersistStorage persistStorage;
    
    /** Ignore duplicate GET requests interval (in milliseconds) */
    private int ignoreUpdateMs;
    
    private Set<TargetBase> hsTarget;
    private Set<UncaughtExceptionListener> hsUncaughtListener;
    private Set<FExceptionListener> hsFExceptionListener;
    
    private FConfig config;
    private WorkerPool workerPool;

    /** MIBs */
    private SnmpV2MibF mibSnmpV2;
    private SnmpFrameworkMibF mibSnmpFramework;
    private SnmpTargetMibF mibSnmpTarget;
    private SnmpNotificationMibF mibSnmpNotification;
    private SnmpProxyMibF mibSnmpProxy;
    private SnmpUsmMibF mibSnmpUsm;
    private SnmpVacmMibF mibSnmpVacm;
    private SnmpCommunityMibF mibSnmpCommunity;
    
    // Variables from org.snmp4j.agent.BaseAgent
    private DefaultMOServer server;
    private Snmp session;
    private TransportMapping<?>[] transportMappings;
    private MessageDispatcherImpl dispatcher;
    private CommandProcessor agent;
    private NotificationOriginatorImpl notificationOriginator;
    private ProxyForwarderImpl defaultProxyForwarder;
    private MPv3 mpv3;
    private USM usm;
    
    //--------------------------------separator--------------------------------
    static int ______SYSTEM;

    /**
     * Constructor
     * 
     * @param appTitle application title
     * @param appVersion application version
     * @param propApp properties
     * 
     * @throws FException
     */
    public AgentWorker(String appTitle, String appVersion, Properties propApp) 
    throws FException 
    {
        this.server = new DefaultMOServer();
        stateAgent = AgentState.CREATED;
        config = new FConfig(appTitle, appVersion, propApp);
        boolean showConsoleUncaught = config.getConfigBoolean(FConstant.KEY_CONSOLE_UNCAUGHT);
        hsUncaughtListener = UncaughtExceptionHandler.init(showConsoleUncaught);
        hsTarget = new HashSet<TargetBase>();
        hsFExceptionListener = new CopyOnWriteArraySet<FExceptionListener>();
        
        OctetString octEngineID = config.getLocalEngineID();
        agent = new CommandProcessor(octEngineID); // creates "agent"
        logger.info(String.format(
                "Created agent at %s; Notify to %s; EngineID=%s", 
                config.getAddress(), config.getNotifyAddress(), octEngineID.toHexString()));
        
        // FException might be thrown from this point.
        // UncaughtExceptionHandler init is completed and it will catch  
        // RuntimeExceptions even without THIS agent ctor completion.
        workerPool = ThreadPool.create("RequestPool",
                     config.getConfigInteger(FConstant.KEY_THREAD_POOL_SIZE));
        agent.setWorkerPool(workerPool);
        lstMIB = new ArrayList<BaseMib>();
        lstHandler = new ArrayList<FHandler>();
        hmNode = new HashMap<FID, FNode>();
        this.ignoreUpdateMs = config.getIgnoreUpdateMs();
        String persistFilename = config.getConfigString(FConstant.KEY_PERSIST_FILENAME);
        setPersistStorage(new PersistStorageImpl(persistFilename));
        this.transport = config.getTransport();
        this.transportDomain = config.getTransportDomain();
        
        // Targets (V1/V2 communities and V3 users)
        loadTargets();

        // Plugins
        addPlugins();
    } // AgentWorker()
   
    /**
     * This method performs all essential actions as  
     * <code>org.snmp4j.agent.BaseAgent.init()</code> does.
     * 
     * @throws IOException loading persistence or properties problem
     * @throws DuplicateRegistrationException loading managed objects problem
     * @throws FException loading initial values and initialization
     */
    public void initAgent() throws IOException, FException 
    {
        checkAgentState(AgentState.CREATED);
        stateAgent = AgentState.INIT_START;
        if (hsTarget.size() == 0) {
            throw new FException("Target is not registered (at least one).");
        }
        initTransportMappings(); // creates: transportMappings 
                                 // throws if wrong address or address in use
        initSecurityModels(); // inits SecurityProtocols and creates usm
        initMessageDispatcher(); // creates: dispatcher, mpv3, session 
        OctetString sysDescr = new OctetString(String.format(
                "FriendlySNMP - %s - %s - %s", 
                System.getProperty("os.name", ""), 
                System.getProperty("os.arch"), 
                System.getProperty("os.version"))); // defines: sysDescr 
        OID sysOID = new OID("1.3.6.1.4.1");
        sysOID.append(FConstant.FRIENDLY_ENTERPRISE_ID);
        
        // === SNMPv2-MIB / File: SNMPv2-MIB-rfc3418.txt
        Integer32 sysServices = new Integer32(10);
        SNMPv2MIB snmpv2MIB = new SNMPv2MIB(sysDescr, sysOID, sysServices);
        dispatcher.addCounterListener(snmpv2MIB); // register counters for updates
        agent.addCounterListener(snmpv2MIB);
        mibSnmpV2 = new SnmpV2MibF(snmpv2MIB);
        addMIBPrivate(mibSnmpV2);
        
        // === SNMP-FRAMEWORK-MIB / File: SNMP-FRAMEWORK-MIB-rfc3411.txt
        SnmpFrameworkMIB snmpFrameworkMIB = new SnmpFrameworkMIB(
                (USM)mpv3.getSecurityModel(SecurityModel.SECURITY_MODEL_USM),
                dispatcher.getTransportMappings());
        mibSnmpFramework = new SnmpFrameworkMibF(snmpFrameworkMIB);
        addMIBPrivate(mibSnmpFramework);
        
        // === SNMP-TARGET-MIB / File: SNMP-APPLICATIONS-rfc3413.txt 
        SnmpTargetMIB snmpTargetMIB = new SnmpTargetMIB(dispatcher);
        mibSnmpTarget = new SnmpTargetMibF(snmpTargetMIB);
        addMIBPrivate(mibSnmpTarget);
        
        // === SNMP-NOTIFICATION-MIB / File: SNMP-APPLICATIONS-rfc3413.txt
        SnmpNotificationMIB snmpNotificationMIB = new SnmpNotificationMIB();
        mibSnmpNotification = new SnmpNotificationMibF(snmpNotificationMIB);
        addMIBPrivate(mibSnmpNotification);
        
        // === SNMP-PROXY-MIB / File: SNMP-APPLICATIONS-rfc3413.txt
        SnmpProxyMIB snmpProxyMIB = new SnmpProxyMIB();
        mibSnmpProxy = new SnmpProxyMibF(snmpProxyMIB);
        addMIBPrivate(mibSnmpProxy);
        
        // === SNMP-USM-AES-MIB / File: SNMP-USM-AES-MIB-rfc3826.txt
        UsmMIB usmMIB = new UsmMIB(usm, SecurityProtocols.getInstance());
        usm.addUsmUserListener(usmMIB);
        mibSnmpUsm = new SnmpUsmMibF(usmMIB);
        addMIBPrivate(mibSnmpUsm);

        // === SNMP-VIEW-BASED-ACM-MIB / File: SNMP-VIEW-BASED-ACM-MIB-rfc3415.txt
        VacmMIB vacmMIB = new VacmMIB(new MOServer[] { server });
        mibSnmpVacm = new SnmpVacmMibF(vacmMIB);
        addMIBPrivate(mibSnmpVacm);
        
        // === SNMP-COMMUNITY-MIB / File: SNMP-COMMUNITY-MIB-rfc2576.txt
        SnmpCommunityMIB snmpCommunityMIB = new SnmpCommunityMIB(snmpTargetMIB);
        mibSnmpCommunity = new SnmpCommunityMibF(snmpCommunityMIB);
        addMIBPrivate(mibSnmpCommunity);
        
        // === Excluded: snmp4jLogMIB = new Snmp4jLogMib();
        // Defined in AGENTPP-CONFIG-MIB / File: AGENTPP-CONFIG-MIB.txt
        // This logger is based on SNMP4J proprietary logging system. 
        // Replaced by explicit Log4j support in Log4j plugin.
        
        // === Excluded snmp4jConfigMIB = new Snmp4jConfigMib();
        // Defined in AGENTPP-CONFIG-MIB / File: AGENTPP-CONFIG-MIB.txt
        // Also removed loadConfig() and saveConfig().
        // All SNMP4J persistency is replaced.
        
        defaultProxyForwarder = new ProxyForwarderImpl(
                session, snmpProxyMIB, snmpTargetMIB);
        agent.addProxyForwarder(
                defaultProxyForwarder, null, ProxyForwarder.PROXY_TYPE_ALL);
        defaultProxyForwarder.addCounterListener(snmpv2MIB);
        
        addCommunities(snmpCommunityMIB);
        addViews(vacmMIB);
        addUsmUser(usm);
        addNotificationTargets(snmpTargetMIB, snmpNotificationMIB);
        registerManagedObjects(); // standard & user defined MIBs
        addShutdownHook();
        
        // Finish init (see BaseAgent.finishInit() without coldStart)
        notificationOriginator = new NotificationOriginatorImpl(
                session, vacmMIB, snmpv2MIB.getSysUpTime(),
                snmpTargetMIB, snmpNotificationMIB);
        agent.setNotificationOriginator(notificationOriginator);
        agent.addMOServer(server);
        agent.setCoexistenceProvider(snmpCommunityMIB);
        agent.setVacm(vacmMIB);
        
        // Collect nodes and handlers from all MIBs
        for (BaseMib mib : lstMIB) {
            mib.collectNodes(hmNode);
            mib.collectHandlers(lstHandler);
        }        
        
        // Init handlers (after loading SNMP4J persistent managed objects!!)
        for (FHandler handler : lstHandler) {
            handler.setAgent(this);
            handler.init();
        }
        
        stateAgent = AgentState.INIT_FINISH;
    } // initAgent()
    
    /**
     * Starts the agent.
     */
    public void startAgent() throws FException {
        checkAgentState(AgentState.INIT_FINISH, AgentState.STOPPED);
        AgentStartType startType;
        if (stateAgent == AgentState.INIT_FINISH) {
            incBootsCount(); 
            startType = AgentStartType.STARTED;
            session = new Snmp(dispatcher); // added as CommandResponder to the dispatcher
            for (int i = 0;  i < transportMappings.length;  i++) {
                session.addTransportMapping(transportMappings[i]);
            }
            notificationOriginator.setSession(session);
            defaultProxyForwarder.setSession(session);
            try {
                session.listen(); // Started and never stopped
            } catch (IOException e) {
                throw new FException("Cannot start agent", e);
            }
        } else {
            startType = AgentStartType.RESTARTED;
        }
        // The order of CommandResponder(s) in dispatcher is essential:
        //    CommandResponder-0: org.snmp4j.Snmp
        //    CommandResponder-1: org.snmp4j.agent.CommandProcessor
        // especially for SNMPv3 when "org.snmp4j.Snmp" must acknowledge
        // response PDU from MIB browser before handling it 
        // to "org.snmp4j.agent.CommandProcessor" as a new request PDU.
        dispatcher.addCommandResponder(agent);
        
        // Start handlers: start threads and send startup notification
        // (including core and plugins)
        stateAgent = AgentState.RUNNING; // before handler.start()
        for (FHandler handler : lstHandler) {
            handler.start(startType);
        }
    } // startAgent()
    
    /**
     * Stops the agent by closing the SNMP session and associated transport
     * mappings.
     * @throws FException 
     */
    public void stopAgent() throws FException {
        checkAgentState(AgentState.RUNNING);
        // 1. Stop handlers (including core and plugins)
        for (FHandler handler : lstHandler) {
            handler.stop();
        }
        // 2. Suspend processing of SNMP requests. 
        // Decouples message dispatcher and agent. All transport mappings 
        // remain unchanged and thus all ports remain opened.
        dispatcher.removeCommandResponder(agent);
        
        stateAgent = AgentState.STOPPED;
    } // stopAgent

    public boolean isRunning() {
        return (stateAgent == AgentState.RUNNING);
    }
    
    /**
     * Performs shutdown procedures.
     * The method could be called from 2 places:
     * - explicitly from {@link FriendlyAgent#shutdown()})
     * - from shutdown hook {@link #addShutdownHook()})
     */
    void shutdown(String hint) {
        if (stateAgent == AgentState.RUNNING) {
            for (FHandler handler : lstHandler) {
                handler.shutdown(); // including: shutdown notification
            }
            try {
                persistStorage.shutdown();
                session.close();
            } catch (Exception e) {
                logger.debug("Failure to shutdown: " + e);
            }
        }
        if (workerPool != null) {
            workerPool.stop();
        }
        stateAgent = AgentState.SHUTDOWN;
        logger.info(String.format("=== FriendlyAgent CLOSED (%s) ===", hint));
    } // shutdown()
    
    /** 
     * Adds shutdown hook to intercept JVM shutdown event.
     * 
     * @see org.snmp4j.agent.BaseAgent#addShutdownHook()
     */
    protected void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown("via shutdown hook");
            }
        });
    } // addShutdownHook()
    
    /**
     * Increments boots counter in persistent storage and updates USM.
     * 
     * @return incremented boot counter
     * @throws FException
     */
    private void incBootsCount() throws FException {
        checkAgentState(AgentState.INIT_FINISH);
        logger.debug("Incrementing boots count");
        int bootsCount = 0;
        try {
            // Scalar SnmpFrameworkMIB.snmpEngineBoots is not consistent.
            // It saves value into the scalar, but returns value from
            // USM.getEngineBoots(). As a result it always returns 0 
            // on scalar.getValue() if USM.setEngineBoots() was not called.
            // Use temporary well-behaving FScalar as a workaround  
            // to reuse scalar persistency loading:   
            FID id = getSnmpEngineBootsFID();
            MOScalar<Integer32> mo = new MOScalar<Integer32>(id.getOID(), 
                    MOAccessImpl.ACCESS_READ_WRITE, new Integer32());
            FScalar scalar = new FScalar("snmpEngineBoots", mo, this);  
            scalar.setVolatile(false); // will load persistent value
            Object obj = scalar.getValue();
            if (obj instanceof Integer) {
                bootsCount = (Integer)obj;
            }
            bootsCount++;
            scalar.setValue(bootsCount);
            getPersistStorage().put(scalar);
            usm.setEngineBoots(bootsCount); // after mibSnmpFramework is init() 
        } catch (FException e) {
            exceptionThrown("Failure to increment boots count", e);
        }
    } // incBootsCount()
    
    /**
     * Returns FID of the snmpEngineBoots scalar.
     * 
     * @return FID of the snmpEngineBoots scalar.
     */
    public FID getSnmpEngineBootsFID() {
        return mibSnmpFramework.getSnmpEngineBoots().getFID();
    } // getSnmpEngineBootsFID()
    
    //--------------------------------separator--------------------------------
    static int ______EVENTS;

    /**
     * Adds <code>UncaughtExceptionListener</code>
     * 
     * @param l <code>UncaughtExceptionListener</code> object
     */
    public void addUncaughtExceptionListener(UncaughtExceptionListener l) {
        hsUncaughtListener.add(l);
    } // addUncaughtExceptionListener()
    
    /**
     * Removes <code>UncaughtExceptionListener</code>
     * 
     * @param l <code>UncaughtExceptionListener</code> object
     */
    public void removeUncaughtExceptionListener(UncaughtExceptionListener l) {
        hsUncaughtListener.remove(l);
    } // removeUncaughtExceptionListener()

    /**
     * Adds <code>FExceptionListener</code>
     * 
     * @param l <code>FExceptionListener</code> object
     */
    public void addFExceptionListener(FExceptionListener l) {
        hsFExceptionListener.add(l);
    } // addFExceptionListener()
    
    /**
     * Removes <code>FExceptionListener</code>
     * 
     * @param l <code>FExceptionListener</code> object
     */
    public void removeFExceptionListener(FExceptionListener l) {
        hsFExceptionListener.remove(l);
    } // removeFExceptionListener()
    
    /**
     * Adds <code>ShutdownListener</code>
     * 
     * @param l <code>ShutdownListener</code> object
     */
    public void addShutdownListener(ShutdownListener l) {
        pluginCore.addShutdownListener(l);
    } // addShutdownListener()
    
    /**
     * Removes <code>ShutdownListener</code>
     * 
     * @param l <code>ShutdownListener</code> object
     */
    public void removeShutdownListener(ShutdownListener l) {
        pluginCore.removeShutdownListener(l);
    } // removeShutdownListener()
    
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
        reportException(msg, e);
        for (FExceptionListener l : hsFExceptionListener) {
            l.exceptionThrown(msg, e);
        }
    } // exceptionThrown()
    
    /**
     * Sends notification. Normally this method is called from 
     * {@link FNotification} objects. Do not call this method directly.
     * 
     * @param oid
     * @param vbs
     * @throws FException 
     */
    public void sendNotification(OID oid, VariableBinding[] vbs) {
        // Throw IllegalStateException if agent is not running:
        if (stateAgent == AgentState.RUNNING) {
            OctetString[] contextAll = server.getContexts();
            for (OctetString context : contextAll) {
                notificationOriginator.notify(new OctetString(context), oid, vbs);
            }
        }
    } // sendNotification()
    
    /**
     * Call this method to report a caught exception in the application 
     * to the MIB browser.
     * 
     * @param comment error message
     * @param e exception
     */
    public void reportException(String comment, Throwable e) {
        if (stateAgent == AgentState.RUNNING) {
            pluginCore.reportException(comment, e);
        }
    } // reportException()
    
    //--------------------------------separator--------------------------------
    static int ______MIB;

    /**
     * Adds MIB. The method is exposed via {@link FriendlyAgent} 
     * to a FriendlySNMP user.
     * 
     * @param mib mib
     * @throws FException 
     */
    void addMIB(BaseMib mib) throws FException {
        checkAgentState(AgentState.CREATED);
        addMIBPrivate(mib);
    } // addMIB()

    /**
     * Adds MIB without state check.
     *  
     * @param mib
     * @throws FException 
     */
    private void addMIBPrivate(BaseMib mib) throws FException {
        lstMIB.add(mib);
        mib.init(this);
    } // addMIBPrivate()
    
    private void addPlugins() throws FException {
        lstPlugin = new ArrayList<FPlugin>();
        // Core plugin is added always:
        pluginCore = new PluginCore();
        lstPlugin.add(pluginCore);

        // Dynamic plugins are declared in props:
        String[] plugins = config.getPlugins();
        StringBuilder sbLog = new StringBuilder(); 
        for (String s : plugins) {
            try {
                sbLog.append("\n  " + s);
                Class<?> c = Class.forName(s); // ClassNotFoundException 
                Object obj = c.newInstance(); // InstantiationException, IllegalAccessException
                lstPlugin.add((FPlugin)obj); // ClassCastException
            } catch (Exception e) {
                throw new FException("Cannot load plugin " + s, e);
            }
        }
        if (sbLog.length() > 0) {
            logger.info("Loaded plugins:" + sbLog);
        }
        
        for (FPlugin p : lstPlugin) {
            p.setAgent(this);
            p.loadDefaultProperties();
            addMIB(p.getMib());
            p.initPlugin();
        }
    } // addPlugins()
    
    //--------------------------------separator--------------------------------
    static int ______GET_SET;

    /**
     * Returns ignore updates interval (in milliseconds).
     *  
     * @return ignore updates interval (in milliseconds).
     */
    public int getIgnoreUpdateMs() {
        return ignoreUpdateMs;
    } // getIgnoreUpdateMs()
    
    /**
     * Returns transport
     * 
     * @return transport
     */
    public TransportSnmp getTransport() {
        return transport;
    } // getTransport()
    
    /**
     * Returns transport domain
     * 
     * @return transport domain
     */
    public TransportDomain getTransportDomain() {
        return transportDomain;
    } // getTransportDomain()

    /**
     * Adds {@link TargetBase} objects to the collection of targets 
     * without actual initialization of the added target.
     * 
     * @param target target
     * @throws FException
     */
    public void addTarget(TargetBase target) throws FException {
        checkAgentState(AgentState.CREATED);
        for (TargetBase t : hsTarget) {
            if (t.getTargetName().equalsIgnoreCase(target.getTargetName())) {
                throw new FException("Duplicate target '%s'", t.getTargetName());
            }
        }
        target.init(this);
        hsTarget.add(target);
        logger.info("Added Target: " + target);
    } // addTarget()
    
    /**
     * Private worker which loads {@link TargetBase} objects declared 
     * in properties.
     * 
     * @throws FException
     */
    private void loadTargets() throws FException {
        String[] community1All = config.getTargets(FConstant.KEY_V1_COMMUNITY);
        for (String community : community1All) {
            community = community.trim();
            if (community.length() > 0) {
                addTarget(new TargetV1(community));
            }
        }
        String[] community2All = config.getTargets(FConstant.KEY_V2_COMMUNITY);
        for (String community : community2All) {
            community = community.trim();
            if (community.length() > 0) {
                addTarget(new TargetV2(community));
            }
        }
        String[] userAll = config.getTargets(FConstant.KEY_V3_USER); 
        for (String user : userAll) {
            user = user.trim();
            if (user.length() > 0) {
                addTarget(new TargetV3(user));
            }
        }
    } // loadTargets()
    
    /**
     * Returns configuration object
     * 
     * @return configuration object
     */
    public FConfig getConfig() {
        return config;
    } // getConfig()
    
    /**
     * Returns default persistence storage object.
     * 
     * @return default persistence storage object
     */
    public PersistStorage getPersistStorage() {
        return persistStorage;
    } // getPersistStorage()

    /**
     * Sets persistence storage. This method allows setting custom made
     * persistency storage.
     * 
     * @param persistStorage persistence storage
     * @throws FException 
     */
    void setPersistStorage(PersistStorage persistStorage) throws FException {
        checkAgentState(AgentState.CREATED);
        persistStorage.setPersistPolicy(
                PersistPolicy.find(
                config.getConfigString(FConstant.KEY_PERSIST_POLICY)));
        this.persistStorage = persistStorage;
    } // setPersistStorage()
    
    /**
     * Checks agent state.
     * 
     * @param state
     * @throws FException 
     */
    private void checkAgentState(AgentState ... state) throws FException {
        for (AgentState st : state) {
            if (st == stateAgent) {
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (AgentState st : state) {
            if (sb.length() > 0) {
                sb.append(" or ");
            }
            sb.append(st);
        }
        throw new FException("AgentState is %s, expected: %s", stateAgent, sb.toString());
    } // checkAgentState()
    
    public FNode getNode(OID oid) {
        return hmNode.get(new FID(oid));
    }
    
    public FScalar getScalar(OID oid) {
        FNode node = getNode(oid);
        return (node instanceof FScalar ? (FScalar)node : null);
    }
    
    public FTable getTable(OID oid) {
        FNode node = getNode(oid);
        return (node instanceof FTable ? (FTable)node : null);
    }
    
    public FNotification getNotification(OID oid) {
        FNode node = getNode(oid);
        return (node instanceof FNotification ? (FNotification)node : null);
    }
    
    //--------------------------------separator--------------------------------
    static int ______BASE_AGENT;
  
    /**
     * Adds community to security name mappings needed for SNMPv1 and SNMPv2c.
     * @param communityMIB
     *    the SnmpCommunityMIB holding coexistence configuration for community
     *    based security models.
     * @see org.snmp4j.agent.BaseAgent#addCommunities(org.snmp4j.agent.mo.snmp.SnmpCommunityMIB)
     */
    protected void addCommunities(SnmpCommunityMIB communityMIB) {
        for (TargetBase target : hsTarget) {
            target.addCommunities(communityMIB);
        }
    } // addCommunities()

    /**
     * Adds initial VACM configuration.
     * @param vacmMIB 
     *    the VacmMIB holding the agent's view configuration.
     * @see org.snmp4j.agent.BaseAgent#addViews(org.snmp4j.agent.mo.snmp.VacmMIB)
     */
    protected void addViews(VacmMIB vacmMIB) {
        for (TargetBase target : hsTarget) {
            target.addViews(vacmMIB);
        }
    } // addViews()

    /**
     * Adds all the necessary initial users to the USM.
     * @param usm 
     *    the USM instance used by this agent.
     * @see org.snmp4j.agent.BaseAgent#addUsmUser(org.snmp4j.security.USM)
     */
    protected void addUsmUser(USM usm) {
        for (TargetBase target : hsTarget) {
            target.addUsmUser(usm);
        }
    } // addUsmUser()

    /**
     * Adds initial notification targets and filters.
     * @param targetMIB
     *    the SnmpTargetMIB holding the target configuration.
     * @param notificationMIB
     *    the SnmpNotificationMIB holding the notification (filter)
     *    configuration.
     * @see org.snmp4j.agent.BaseAgent#addNotificationTargets(org.snmp4j.agent.mo.snmp.SnmpTargetMIB, org.snmp4j.agent.mo.snmp.SnmpNotificationMIB)
     */
    protected void addNotificationTargets(
            SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) 
    {
        targetMIB.addDefaultTDomains();
        for (TargetBase target : hsTarget) {
            target.addNotificationTargets(targetMIB, notificationMIB);
        }
    } // addNotificationTargets()

    /**
     * Register managed objects in registered MIBs at the agent's server.
     * @see org.snmp4j.agent.BaseAgent#registerManagedObjects()
     */
    protected void registerManagedObjects() {
        OctetString[] contextAll = server.getContexts();
        logger.debug("MIB's list: " + lstMIB);
        for (BaseMib mib : lstMIB) {
            for (OctetString context : contextAll) {
                String log = String.format("%s for context '%s'",
                    mib.getClass().getName(), context);
                logger.debug("Registering " + log);
                // Exception to register a single MIB does not prevent registering other MIBs:
                try {
                    mib.registerMOs(server, context);
                } catch (DuplicateRegistrationException e) {
                    exceptionThrown(new FException(
                        "Failure to registerManagedObjects() for MIB " + log, e));
                }
            }
        }
    } // registerManagedObjects()

    /**
     * Initializes the transport mappings (ports) to be used by the agent.
     * @see org.snmp4j.agent.BaseAgent#initTransportMappings()
     */
    protected void initTransportMappings() throws IOException {
        logger.debug("Transport=" + transport);
        String address = config.getAddress(); 
        transportMappings = new TransportMapping[1];
        switch (transport) {
            case UDP:
                transportMappings[0] = new DefaultUdpTransportMapping(
                                       new UdpAddress(address)); 
                break;
            case TCP:
                transportMappings[0] = new DefaultTcpTransportMapping(
                                       new TcpAddress(address)); 
                break;
            default:
                throw new IllegalArgumentException("Not valid transport: " + transport);
        }
    } // initTransportMappings()

    /**
     * Initializes security models.
     * 
     * See also: org.snmp4j.agent.AgentConfigManager#initSecurityModels()
     */
    protected void initSecurityModels() {
        SecurityProtocols.getInstance().addDefaultProtocols();
        usm = new USM(SecurityProtocols.getInstance(),
                agent.getContextEngineID(),
                0); 
        SecurityModels.getInstance().addSecurityModel(usm);
    } // initSecurityModels()
    
    /**
     * Initializes the message dispatcher ({@link MessageDispatcherImpl}) with
     * the transport mappings.
     * 
     * See also: org.snmp4j.agent.AgentConfigManager#initMessageDispatcherWithMPs()
     */
    protected void initMessageDispatcher() {
        dispatcher = new MessageDispatcherImpl();
        mpv3 = new MPv3(agent.getContextEngineID().getValue());
        dispatcher.addMessageProcessingModel(new MPv1());
        dispatcher.addMessageProcessingModel(new MPv2c());
        dispatcher.addMessageProcessingModel(mpv3);
    }
    
    /**
     * Returns agent: CommandProcessor object.
     * 
     * @return agent.
     */
    public CommandProcessor getAgent() {
        return agent;
    } // getAgent()

    /**
     * Returns server object.
     * 
     * @return server.
     */
    public DefaultMOServer getServer() {
        return server;
    } // getServer()

} // class AgentWorker
