/*
 * File: FConfig.java
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
 * $Id: FConfig.java,v 1.31 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.friendlysnmp.plugin.core.PluginCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OctetString;

public class FConfig extends FConstant {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FConfig.class);

    /**
     * Properties object which was used to initialize the agent 
     */
    private Properties propApp;
    private String appTitle;
    private String appVersion;
    private Map<String, String> hmConfig;
    
    public FConfig(String appTitle, String appVersion, Properties propApp) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
        this.propApp = propApp;
        hmConfig = loadDefaults();
        // Modify defaults with matching property from propApp:
        Set<Entry<String, String>> hsEntry = hmConfig.entrySet();
        for (Entry<String, String> entry : hsEntry) {
            String key = entry.getKey();
            String val = propApp.getProperty(key);
            if (val != null) {
                entry.setValue(val);
            }
        }
        
        // Speed up loading: do not generate name if it is specified in prop file:
        String persistFilename = propApp.getProperty(KEY_PERSIST_FILENAME);
        if (persistFilename == null) {
            persistFilename = getDefaultPersistFilename();
        }
        hmConfig.put(KEY_PERSIST_FILENAME, persistFilename);
    } // FConfig()
    
    private static Map<String, String> loadDefaults() {
        // Ordered alphabetically 
        Map<String, String> hm = new TreeMap<String, String>();
        hm.put(KEY_ADDRESS_SET_GET,        null);
        hm.put(KEY_ADDRESS_SEND_NOTIFY,    null);
        hm.put(KEY_ALLOW_BOOTS_COUNT_RESET,Boolean.toString(DEFAULT_ALLOW_BOOTS_COUNT_RESET));
        hm.put(KEY_CONSOLE_UNCAUGHT,       Boolean.toString(DEFAULT_CONSOLE_UNCAUGHT));
        hm.put(KEY_ENGINE_ID,              createLocalEngineID().toString());
        hm.put(KEY_IGNORE_UPDATE_MS,       Integer.toString(DEFAULT_IGNORE_UPDATE_MS));
        hm.put(KEY_NOTIFY_RETRY_COUNT,     Integer.toString(DEFAULT_NOTIFY_RETRY_COUNT));
        hm.put(KEY_NOTIFY_TIMEOUT_MS,      Integer.toString(DEFAULT_NOTIFY_TIMEOUT_MS));
        hm.put(KEY_OID_ROOT_NOTIFY_VIEW,   DEFAULT_OID_ROOT);
        hm.put(KEY_OID_ROOT_READ_VIEW,     DEFAULT_OID_ROOT);
        hm.put(KEY_OID_ROOT_WRITE_VIEW,    DEFAULT_OID_ROOT);
        hm.put(KEY_PERSIST_POLICY,         DEFAULT_PERSIST_POLICY.toString());
        hm.put(KEY_STORAGE_COMMUNITY,      DEFAULT_STORAGE_COMMUNITY.toString());
        hm.put(KEY_STORAGE_NOTIFY,         DEFAULT_STORAGE_NOTIFY.toString());
        hm.put(KEY_STORAGE_VACM_ACCESS,    DEFAULT_STORAGE_VACM_ACCESS.toString());
        hm.put(KEY_STORAGE_VACM_GROUP,     DEFAULT_STORAGE_VACM_GROUP.toString());
        hm.put(KEY_STORAGE_VACM_VIEWTREE,  DEFAULT_STORAGE_VACM_VIEWTREE.toString());
        hm.put(KEY_THREAD_POOL_SIZE,       Integer.toString(DEFAULT_THREAD_POOL_SIZE));
        hm.put(KEY_TRANSPORT,              DEFAULT_TRANSPORT.name());
        hm.put(KEY_TRANSPORT_DOMAIN,       DEFAULT_TRANSPORT_DOMAIN.name());
        hm.put(KEY_V1_COMMUNITY,           "");
        hm.put(KEY_V2_COMMUNITY,           "");
        hm.put(KEY_V3_CONTEXT,             ""); // default context
        hm.put(KEY_V3_PASSWORD_AUTH,       ""); // AuthProtocol.NONE
        hm.put(KEY_V3_PASSWORD_KEY,        DEFAULT_V3_PASSWORD_KEY);
        hm.put(KEY_V3_PASSWORD_PRIV,       ""); // PrivProtocol.NONE
        hm.put(KEY_V3_PROTOCOL_AUTH,       ""); // AuthProtocol.NONE
        hm.put(KEY_V3_PROTOCOL_PRIV,       ""); // PrivProtocol.NONE
        hm.put(KEY_V3_USER,                "");
        return hm;
    } // loadDefaults()
    
    public void addDefaultProperty(String key, String value) {
        hmConfig.put(key, value);
    }
    
    public String getAppProperty(String key) {
        return propApp.getProperty(key);
    } // getAppProperty()
    
    public String[] getTargets(String key) {
        String val = hmConfig.get(key);
        if (val == null  ||  val.length() == 0) {
            return new String[0];
        }
        return val.split(" ");
    } // getTargets()
    
    public String getConfigString(String key) {
        String val = hmConfig.get(key);
        if (val == null) {
            throwRuntimeException(key); // missing defaults           
        }
        return val;
    } // getConfigString()
    
    public int getConfigInteger(String key) throws FException {
        String val = hmConfig.get(key);
        if (val == null) {
            throwRuntimeException(key); // missing defaults           
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throwFException(key, e);            
        }
        return -1; // to keep compiler happy
    } // getConfigInteger()
    
    public boolean getConfigBoolean(String key) {
        String val = hmConfig.get(key);
        if (val == null) {
            throwRuntimeException(key); // missing defaults           
        }
        return Boolean.parseBoolean(val);
    } // getConfigBoolean()
    
    int getIgnoreUpdateMs() throws FException {
        int ignoreUpdateMs = getConfigInteger(KEY_IGNORE_UPDATE_MS);
        if (ignoreUpdateMs < 0  ||  ignoreUpdateMs > MAX_IGNORE_UPDATE_MS) {
            throwFException(KEY_IGNORE_UPDATE_MS);
        }
        logger.debug("IgnoreUpdate: " + ignoreUpdateMs + " ms");
        return ignoreUpdateMs;
    } // getIgnoreUpdateMs()
    
    TransportSnmp getTransport() throws FException {
        String transport = hmConfig.get(KEY_TRANSPORT);
        TransportSnmp tr = TransportSnmp.valueOf(transport);
        if (tr == null) {
            throwFException(KEY_TRANSPORT);
        }
        logger.debug("Transport: " + tr);
        return tr; 
    } // getTransport()
    
    TransportDomain getTransportDomain() throws FException {
        String transportDomain = hmConfig.get(KEY_TRANSPORT_DOMAIN);
        TransportDomain td = TransportDomain.valueOf(transportDomain);
        if (td == null) {
            throwFException(KEY_TRANSPORT_DOMAIN);
        }
        logger.debug("TransportDomain: " + td);
        return td; 
    } // getTransportDomain()
    
    
    /**
     * Creates a local engine ID based on the local IP address
     * and FriendlySNMP enterprise ID. The code to generate it 
     * in FConfig.createLocalEngineID() is similar to 
     * org.snmp4j.mp.MPv3.createLocalEngineID(). 
     * <p>
     * Frank Fock posted "Not in time window Error Using TestAgent" in 
     * 2006-March forum: 
     * <p>
     * If you are using SNMP4J SnmpRequest with the TestAgent, it is likely 
     * that both use the same engine ID which causes these problems. Please 
     * make sure that both use different engine IDs.
     * <p>
     * The MIB Explorer uses engine ID generated based on SNMP4J enterprise ID
     * while FriendlyAgent uses different engine IDs based on FriendlySNMP
     * enterprise ID.
     * 
     * @see #createLocalEngineID()
     *  
     * @return local engine ID
     */
    OctetString getLocalEngineID() {
        String engineID = hmConfig.get(KEY_ENGINE_ID);
        return OctetString.fromHexString(engineID);
    } // getLocalEngineID()
    
    /**
     * Returns collection of application information: title, version and so on. 
     * This information is for information only and is presented in MIB browser.
     * 
     * @return collection of application information
     * @throws FException
     */
    public Map<String, String> getAppInfo() throws FException { 
        Map<String, String> hm = new TreeMap<String, String>();
        Set<String> hsKey = propApp.stringPropertyNames();
        for (String key : hsKey) {
            if (key.equals(PREFIX_APP)) {
                throw new FException("Not valid property: %s", PREFIX_APP);
            }
            if (key.indexOf(PREFIX_APP) == 0) {
                String val = propApp.getProperty(key);
                key = key.substring(PREFIX_APP.length());
                hm.put(key, val);
            }
        }
        hm.put("Title", appTitle);
        hm.put("Version", appVersion);
        return hm;
    } // getAppInfo()
    
    /**
     * Returns application dependencies collection: external jars, used tools, 
     * and so on. This information is for information only and is presented
     * in MIB browser.
     * 
     * @return application dependencies collection
     * @throws FException
     */
    public Map<String, String> getAppDependencies() throws FException { 
        Map<String, String> hm = new TreeMap<String, String>();
        Set<String> hsKey = propApp.stringPropertyNames();
        for (String key : hsKey) {
            if (key.equals(PREFIX_DEPENDENCY)) {
                throw new FException("Not valid property: %s", PREFIX_DEPENDENCY);
            }
            if (key.indexOf(PREFIX_DEPENDENCY) == 0) {
                String val = propApp.getProperty(key);
                key = key.substring(PREFIX_DEPENDENCY.length());
                hm.put(key, val);
            }
        }
        
        hm.put("FriendlySNMP", FRIENDLYSNMP_INFO);
        hm.put("AgenPro", AGENPRO_INFO);
        hm.put("snmp4j", SNMP4J_INFO);
        hm.put("snmp4j-agent", SNMP4JAGENT_INFO);
        hm.put("JVM-MANAGEMENT-MIB", JVM_MIB_INFO);
        return hm;
    } // getAppDependencies()
    
    /**
     * Returns application properties. This method loads the properties object  
     * at agent startup to expose them to MIB browser. Any changes to the 
     * properties object after the fact are ignored.
     * 
     * @return collection object
     */
    public Map<String, String> getAppProp() {
        Map<String, String> hm = new TreeMap<String, String>();
        Set<String> hsKey = propApp.stringPropertyNames();
        for (String key : hsKey) {
            String val = propApp.getProperty(key);
            hm.put(key, val);
        }
        return hm;
    } // getAppProp()
    
    public Map<String, String> getConfigProp() {
        return hmConfig;
    } // getConfigProp()
    
    /**
     * Returns application title
     * 
     * @return application title
     */
    public String getAppTitle() {
        return appTitle;
    } // getAppTitle()
    
    public String getAddress() {
        return hmConfig.get(KEY_ADDRESS_SET_GET);
    } // getAddress()
    
    public String getNotifyAddress() {
        return hmConfig.get(KEY_ADDRESS_SEND_NOTIFY);
    } // getNotifyAddress()
    
    public String[] getPlugins() throws FException {
        String coreKey = KEY_PLUGIN_PREFIX + "core";
        if (propApp.getProperty(coreKey) != null) {
            throw new FException("Properties should not contain '%s' entry.", coreKey);
        }
        hmConfig.put(coreKey, PluginCore.class.getName());
        Set<String> setPlugin = new HashSet<String>();
        Set<String> setKey = propApp.stringPropertyNames();
        for (String key : setKey) {
            if (key.indexOf(KEY_PLUGIN_PREFIX) == 0) {
                // The key is plugin class entry or plugin parameter
                String keySuffix = key.substring(KEY_PLUGIN_PREFIX.length());
                if (!keySuffix.matches("[A-Za-z0-9]+")) {
                    // This is plugin "xyz" parameter like "snmp.plugin.xyz.key"
                    continue;
                }
                String plugin = propApp.getProperty(key);
                setPlugin.add(plugin);
                hmConfig.put(key, plugin);
            }
        }
        return setPlugin.toArray(new String[setPlugin.size()]);
    } // getPlugins()
    
    private static OctetString createLocalEngineID() {
        final int id = FRIENDLY_ENTERPRISE_ID;
        byte[] engineID = new byte[5];
        engineID[0] = (byte)(0x80 | ((id >> 24) & 0xFF));
        engineID[1] = (byte)((id >> 16) & 0xFF);
        engineID[2] = (byte)((id >> 8) & 0xFF);
        engineID[3] = (byte)(id & 0xFF);
        engineID[4] = 1;
        OctetString os = new OctetString();
        try {
            os.setValue(getInetAddress().getAddress());
        } catch (IOException ex) {
            logger.debug(
                "Local host cannot be determined to generate local engine ID");
            engineID[4] = 4;
            os.setValue("FriendlySNMP".getBytes());
        }
        OctetString octEngineID = new OctetString(engineID);
        octEngineID.append(os);
        return octEngineID;
    } // createLocalEngineID()
    
    private static String getDefaultPersistFilename() {
        final class ThisSecurityManager extends SecurityManager {
            public Class<?> getRootClass() {
                // getClassContext() returns execution stack
                Class<?>[] a = getClassContext();
                return a[a.length - 1];
            }
        } // inner class ThisSecurityManager
        Class<?> classRoot = (new ThisSecurityManager()).getRootClass();
        return classRoot.getSimpleName() + ".bin";
    } // getDefaultPersistFilename()
    
    /**
     * Returns network host in the following preferences order: (1)IPv4 host,
     * (2)IPv6 host if IPv4 host is not found, (3)localhost if none of 
     * the above is found. 
     * <p>
     * The method InetAddress.getLocalHost() returns localhost on a Linux.
     * Also, Linux returns IPv6 host _before_ IPv4 host while IPv4 return value 
     * is preferable in many cases. Thus more complex method to find a host 
     * is required. 
     *  
     * @return network address
     * @throws IOException
     */
    public static InetAddress getInetAddress() 
    throws IOException 
    {
        InetAddress addrIPv4 = null;
        InetAddress addrIPv6 = null;
        Enumeration<NetworkInterface> netAll = NetworkInterface.getNetworkInterfaces();
        while (netAll.hasMoreElements()) {
            NetworkInterface net = netAll.nextElement();
            Enumeration<InetAddress> addrAll = net.getInetAddresses();
            while (addrAll.hasMoreElements()) {
                InetAddress addr = addrAll.nextElement();
                if (addr.isLoopbackAddress()) {
                    continue;
                }
                if (addr instanceof Inet4Address) {
                    addrIPv4 = addr;
                }
                if (addr instanceof Inet6Address) {
                    addrIPv6 = addr;
                }
            }
        }
        if (addrIPv4 != null) {
            return addrIPv4;
        }
        if (addrIPv6 != null) {
            return addrIPv6;
        }
        return InetAddress.getLocalHost(); // in form "MYHOST/10.0.12.34"
    } // getInetAddress()
    
    public static void throwRuntimeException(String key) {
        throw new RuntimeException(String.format(
                "Missing key or invalid value '%s' in config.", key));
    } // throwFException()
    
    public static void throwFException(String key) throws FException {
        throwFException(key, null);
    } // throwFException()
    
    private static void throwFException(String key, Throwable cause) throws FException {
        throw new FException("Not valid value for the key '%s'", key);
    } // throwFException()
    
} // class FConfig
