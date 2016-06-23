/*
 * File: FConstant.java
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
 * $Id: FConstant.java,v 1.27 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import org.friendlysnmp.persist.PersistPolicy;
import org.friendlysnmp.target.Storage;

public class FConstant {
    
    /**
     * Private Enterprise Number assigned to FriendlySNMP
     */
    public final static int FRIENDLY_ENTERPRISE_ID = 29091;
    
    /**
     * Agent properties entry key prefix for filtering out application
     * information list.
     */
    public final static String PREFIX_APP = "app.";
    
    /**
     * Agent properties entry key prefix for filtering out application
     * dependencies list
     */
    public final static String PREFIX_DEPENDENCY = "dependency.";
    
    /** SNMP4J info displayed in MIB browser */
    public static final String SNMP4J_INFO = 
        org.snmp4j.version.VersionInfo.getVersion(); 
    
    /** SNMP4J-agent info displayed in MIB browser */
    public static final String SNMP4JAGENT_INFO = 
        org.snmp4j.agent.version.VersionInfo.getVersion(); 
    
    //--------------------------------separator--------------------------------
    static int ______DYNAMIC;
    
    /** FriendlySNMP info displayed in MIB browser */
    public static final String FRIENDLYSNMP_INFO =  
        "@FRIENDLYSNMP_VERSION@ / Build @BUILD_NUMBER@ / @BUILD_TIME@";
    
    /** AgenPro info displayed in MIB browser */
    public static final String AGENPRO_INFO =   
        "@AGENPRO_VERSION@ / MIB-to-Java generator";
    
    /** JVM-MANAGEMENT-MIB info displayed in MIB browser */
    public static final String JVM_MIB_INFO = 
        "200403041800Z / JSR 163 v1.0 with FF fix";
    
    //--------------------------------separator--------------------------------
    static int ______KEYS;
    
    /**
     * Key in a properties file defines address to send notifications
     * from THIS application to this address (MIB browser).
     * There is no default value for this entry.
     */
    public final static String KEY_ADDRESS_SEND_NOTIFY = "snmp.address.send-notification";
    
    /**
     * Key in a properties file defines address of THIS application 
     * to respond on SET and GET requests from MIB browser.
     * There is no default value for this entry.
     */
    public final static String KEY_ADDRESS_SET_GET = "snmp.address.get-set";
    
    /**
     * Key in a properties file to disallow application boots count reset.
     * This value is optional and overrides default value which is "true".
     * 
     * @see #DEFAULT_ALLOW_BOOTS_COUNT_RESET
     */
    public final static String KEY_ALLOW_BOOTS_COUNT_RESET = "snmp.allow-boots-count-reset"; 
    
    /**
     * Key in a properties file defines how stack trace is shown in a console.
     * Uncaught exceptions are handled by agent and their stack traces are not
     * shown by JVM in a console. Set this value to "true" in development
     * environment to see stack traces and to "false" in production to keep
     * console clean.
     * 
     * @see #DEFAULT_CONSOLE_UNCAUGHT
     */
    public final static String KEY_CONSOLE_UNCAUGHT = "snmp.console-uncaught";
    
    /**
     * Key in a properties file defines local engine ID. 
     * This value is optional and overrides default generated value. 
     */
    public final static String KEY_ENGINE_ID = "snmp.engine-id";

    /**
     * Key in a properties file defines ignore updates time. 
     * This value is optional and overrides default value.
     * 
     * @see #DEFAULT_IGNORE_UPDATE_MS
     */
    public final static String KEY_IGNORE_UPDATE_MS = "snmp.ignore-update-ms"; 
    
    /**
     * Key in a properties file defines notify retry count.
     * This value is optional and overrides default value.
     * 
     * @see #DEFAULT_NOTIFY_RETRY_COUNT
     */
    public final static String KEY_NOTIFY_RETRY_COUNT = "snmp.notify.retry-count";
    
    /**
     * Key in a properties file defines notify timeout.
     * This value is optional and overrides default value.
     * 
     * @see #DEFAULT_NOTIFY_TIMEOUT_MS
     */
    public final static String KEY_NOTIFY_TIMEOUT_MS = "snmp.notify.timeout-ms";
    
    /**
     * Key in a properties file defines root OID for the notify view in the
     * vacmViewTreeFamilyTable.
     * <p>This value is optional and overrides default value which is "1.3"
     * 
     * @see #DEFAULT_OID_ROOT
     */
    public final static String KEY_OID_ROOT_NOTIFY_VIEW = "snmp.oidroot.notify-view";
    
    /**
     * Key in a properties file defines root OID for the read view in the
     * vacmViewTreeFamilyTable.
     * <p>This value is optional and overrides default value which is "1.3"
     * 
     * @see #DEFAULT_OID_ROOT
     */
    public final static String KEY_OID_ROOT_READ_VIEW = "snmp.oidroot.read-view";
    
    /**
     * Key in a properties file defines root OID for the write view in the
     * vacmViewTreeFamilyTable.
     * <p>This value is optional and overrides default value which is "1.3"
     * 
     * @see #DEFAULT_OID_ROOT
     */
    public final static String KEY_OID_ROOT_WRITE_VIEW = "snmp.oidroot.write-view";
    
    /**
     * Key in a properties file defines persistence storage filename.
     * This value is optional and overrides default value which is 
     * root "&lt;class-name&gt;.bin" for current execution stack.
     */
    public final static String KEY_PERSIST_FILENAME = "snmp.persist.filename"; 
    
    /**
     * Key in a properties file defines persistence storage policy.
     * The allowed values are textual presentations of <code>PersistPolicy</code>
     * enumeration. Not valid value in a property file throws 
     * <code>FException</code> at the time of SNMP agent is created.  
     * This value is optional and overrides default value which is 
     * <code>PersistPolicy.ON_CHANGE</code>
     * 
     * @see #DEFAULT_PERSIST_POLICY
     * @see PersistPolicy
     */
    public final static String KEY_PERSIST_POLICY = "snmp.persist.policy"; 
    
    /**
     * Key in a properties file defines plugin class.
     * The key in a property file should be appended with any unique suffix.
     */
    public final static String KEY_PLUGIN_PREFIX = "snmp.plugin.";
    
    /**
     * Key in a properties file defines community storage type.
     * <p>See valid values at {@link #KEY_STORAGE_VACM_GROUP}.
     * <p>Default value is <code>DEFAULT_STORAGE_COMMUNITY</code>
     * 
     * @see #DEFAULT_STORAGE_COMMUNITY
     */
    public final static String KEY_STORAGE_COMMUNITY = "snmp.storage.community";
    
    /**
     * Key in a properties file defines notify storage type.
     * <p>See valid values at {@link #KEY_STORAGE_VACM_GROUP}
     * <p>This value is optional and overrides default value 
     * <code>DEFAULT_STORAGE_NOTIFY</code>
     * 
     * @see #DEFAULT_STORAGE_NOTIFY
     */
    public final static String KEY_STORAGE_NOTIFY = "snmp.storage.notify";
    
    /**
     * Key in a properties file defines VACM access storage type.
     * <p>Valid values see {@link #KEY_STORAGE_VACM_GROUP}
     * <p>This value is optional and overrides default value 
     * <code>DEFAULT_STORAGE_VACM_ACCESS</code>
     * 
     * @see #DEFAULT_STORAGE_VACM_ACCESS
     */
    public final static String KEY_STORAGE_VACM_ACCESS = "snmp.storage.vacm.access"; 
    
    /**
     * Key in a properties file defines VACM group storage type. 
     * <p>Valid values are the following strings: readOnly, permanent, volatile, 
     * nonVolatile, other. Not valid value in a property file throws 
     * <code>FException</code> at the time when SNMP agent is created. 
     * <p>This value is optional and overrides default value 
     * <code>DEFAULT_STORAGE_VACM_GROUP</code>
     * 
     * @see #DEFAULT_STORAGE_VACM_GROUP
     */
    public final static String KEY_STORAGE_VACM_GROUP = "snmp.storage.vacm.group";
    
    /**
     * Key in a properties file defines VACM viewtree storage type.
     * <p>See valid values at {@link #KEY_STORAGE_VACM_GROUP}
     * <p>This value is optional and overrides default value 
     * <code>DEFAULT_STORAGE_VACM_VIEWTREE</code>
     * 
     * @see #DEFAULT_STORAGE_VACM_VIEWTREE
     */
    public final static String KEY_STORAGE_VACM_VIEWTREE = "snmp.storage.vacm.viewtree"; 
    
    /**
     * SNMP4J agent thread pool size. 
     * This value is optional and overrides default value 
     * <code>DEFAULT_THREAD_POOL_SIZE</code>
     * 
     * @see #DEFAULT_THREAD_POOL_SIZE
     */
    public final static String KEY_THREAD_POOL_SIZE = "snmp.thread-pool-size";
    
    /**
     * Key in a properties file defines transport. 
     * Valid values are: UDP (default) or TCP.
     * This value is optional and overrides default value 
     * <code>DEFAULT_TRANSPORT</code>.
     * <p>Not valid value in a property file throws 
     * <code>FException</code> at the time of SNMP agent is created.
     * 
     * @see #DEFAULT_TRANSPORT
     */
    public final static String KEY_TRANSPORT = "snmp.transport"; 
    
    /**
     * Key in a properties file defines transport domain.
     * Valid values are: IPV4 (default), IPV4Z, IPV6, IPV6Z.
     * This value is optional and overrides default value
     * <code>DEFAULT_TRANSPORT_DOMAIN</code>
     * <p>Not valid value in a property file throws 
     * <code>FException</code> at the time of SNMP agent is created.
     *  
     * @see #DEFAULT_TRANSPORT_DOMAIN
     */
    public final static String KEY_TRANSPORT_DOMAIN = "snmp.transport.domain"; 
    
    /**
     * Key in a properties file defines V1 communities separated by spaces.
     * Examples: "public", "public hello friendly"
     */
    public final static String KEY_V1_COMMUNITY = "snmp.v1.community";
    
    /**
     * Key in a properties file defines V2 communities separated by spaces.
     * Examples: "public", "public hello friendly"
     */
    public final static String KEY_V2_COMMUNITY = "snmp.v2.community";
    
    /**
     * A key in a properties file defines SNMPv3 context. 
     * Default value is "" (empty string). 
     */
    public final static String KEY_V3_CONTEXT = "snmp.v3.context";
    
    /**
     * A key in a properties file defines SNMPv3 authentication password. 
     * Passwords should be encrypted using FriendlyPro application. 
     */
    public final static String KEY_V3_PASSWORD_AUTH = "snmp.v3.password.auth";
    
    /**
     * A key in a properties file defines SNMPv3 password encryption key. 
     * Passwords should be encrypted with this key using password encryption tool. 
     */
    public final static String KEY_V3_PASSWORD_KEY = "snmp.v3.password.key";
    
    /**
     * A key in a properties file defines privileges password. 
     * Passwords should be encrypted using FriendlyPro application. 
     */
    public final static String KEY_V3_PASSWORD_PRIV = "snmp.v3.password.priv";
    
    /**
     * A key in a properties file defines SNMPv3 authentication protocol. 
     * Valid authentication protocols are: none, MD5, SHA.
     * Default protocol is "none". Empty value is considered as "none".
     */
    public final static String KEY_V3_PROTOCOL_AUTH = "snmp.v3.protocol.auth";
    
    /**
     * A key in a properties file defines SNMPv3 privileges protocol. 
     * Valid privileges protocols are: none, DES, AES128, AES192, AES256.
     * Default protocol is "none". Empty value is considered as "none".
     */
    public final static String KEY_V3_PROTOCOL_PRIV = "snmp.v3.protocol.priv";
    
    /**
     * Key in a properties file defines SNMPv3 users separated by spaces.
     * Examples: "friend", "friend world"
     */
    public final static String KEY_V3_USER = "snmp.v3.user"; 
    
    //--------------------------------separator--------------------------------
    static int ______DEFAULTS;
    
    /**
     * Default behavior is to allow boots count reset.
     * 
     * @see #KEY_ALLOW_BOOTS_COUNT_RESET
     */
    public final static boolean DEFAULT_ALLOW_BOOTS_COUNT_RESET = true;
    
    /**
     * Default behavior is to send uncaught exception stack trace to console.
     * 
     * @see #KEY_CONSOLE_UNCAUGHT
     */
    public final static boolean DEFAULT_CONSOLE_UNCAUGHT = true;
    
    /**
     * Deadlock check interval. This check is performed in a dedicated thread.
     * The value defines the sleep time of this thread between deadlock checks.
     * <p>Default value is 1 sec. MIB browser has an option
     * to override this value, which is saved in persistence storage. 
     * Application will use new updated deadlock check interval after restart.
     */
    public static final int DEFAULT_DEADLOCK_CHECK_INTERVAL_SEC = 1;
    
    /**
     * MIB browser request for some value may come many times in a short 
     * period of time. On one hand, the actual requested value in most cases is 
     * not changing that rapidly. On other hand, the displayed in MIB browser 
     * value is sufficient to be a little bit old. 
     * <br>
     * Multiple identical MIB browser requests for the same value trigger 
     * application listener to update requested value. This could be a
     * performance bottleneck. To enhance application performance 
     * identical MIB browser requests are ignored after the first
     * received request. The default ignore time is 1000 milliseconds (1 sec).
     * <br> 
     * This value could be updated in property file.
     * 
     * @see #KEY_IGNORE_UPDATE_MS
     */
    public final static int DEFAULT_IGNORE_UPDATE_MS = 1000;  // 1 sec
    
    /**
     * Default notify retry count 1 
     * 
     * @see #KEY_NOTIFY_RETRY_COUNT
     */
    public final static int DEFAULT_NOTIFY_RETRY_COUNT = 1;
    
    /**
     * Default notification timeout 1000 millisecond
     * 
     * @see #KEY_NOTIFY_TIMEOUT_MS
     */
    public final static int DEFAULT_NOTIFY_TIMEOUT_MS = 1000;
    
    /**
     * Default OID root for read / write / notify views 
     * in vacmViewTreeFamilyTable. 
     * 
     * @see #KEY_OID_ROOT_READ_VIEW
     * @see #KEY_OID_ROOT_WRITE_VIEW
     * @see #KEY_OID_ROOT_NOTIFY_VIEW
     */
    public final static String DEFAULT_OID_ROOT = "1.3";

    /**
     * Default persistency storage policy is ON_CHANGE. 
     * 
     * @see #KEY_PERSIST_POLICY
     * @see PersistPolicy
     */
    public final static PersistPolicy DEFAULT_PERSIST_POLICY = PersistPolicy.ON_CHANGE;
    
    /**
     * Default storage community type is READONLY. 
     * 
     * @see #KEY_STORAGE_COMMUNITY
     */
    public final static Storage DEFAULT_STORAGE_COMMUNITY = Storage.READONLY;
    
    /**
     * Default storage notify type is READONLY. 
     * 
     * @see #KEY_STORAGE_NOTIFY
     */
    public final static Storage DEFAULT_STORAGE_NOTIFY = Storage.READONLY;
    
    /**
     * Default storage VACM access type is READONLY. 
     * 
     * @see #KEY_STORAGE_VACM_ACCESS
     */
    public final static Storage DEFAULT_STORAGE_VACM_ACCESS = Storage.READONLY;
    
    /**
     * Default storage VACM group type is READONLY. 
     * 
     * @see #KEY_STORAGE_VACM_GROUP
     */
    public final static Storage DEFAULT_STORAGE_VACM_GROUP = Storage.READONLY;
    
    /**
     * Default storage VACM viewtree type is READONLY. 
     * 
     * @see #KEY_STORAGE_VACM_VIEWTREE
     */
    public final static Storage DEFAULT_STORAGE_VACM_VIEWTREE = Storage.READONLY;
    
    /**
     * SNMP4J agent thread pool size. 
     * 
     * @see #KEY_THREAD_POOL_SIZE
     */
    public final static int DEFAULT_THREAD_POOL_SIZE = 4;
    
    /**
     * Default transport is UDP. 
     * Application may modify it by defining different transport 
     * in properties file.
     *  
     * @see #KEY_TRANSPORT
     */
    public final static TransportSnmp DEFAULT_TRANSPORT = TransportSnmp.UDP;
    
    /**
     * Default transport domain is IPv4.
     * Application may modify it by defining different transport domain 
     * in properties file.
     *  
     * @see #KEY_TRANSPORT_DOMAIN
     */
    public final static TransportDomain DEFAULT_TRANSPORT_DOMAIN 
                                                        = TransportDomain.IPV4;
    /**
     * Default encryption key
     * 
     * @see #KEY_V3_PASSWORD_KEY
     */
    public final static String DEFAULT_V3_PASSWORD_KEY = "friendly";
    
    //--------------------------------separator--------------------------------
    static int ______MISC;
    
    /**
     * See notes {@link #DEFAULT_IGNORE_UPDATE_MS}. Properties file 
     * error while setting {@link #DEFAULT_IGNORE_UPDATE_MS} to very long 
     * time might prevent application from updating values. The maximum
     * default value is 10 sec. 
     */
    public final static int MAX_IGNORE_UPDATE_MS = 10000; // 10 sec
    
} // class FConstant
