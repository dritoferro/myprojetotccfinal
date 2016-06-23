/*
 * File: CoreAppInfoHandler.java
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
 * $Id: CoreAppInfoHandler.java,v 1.14 2014/01/22 23:28:02 mg Exp $
 */
package org.friendlysnmp.plugin.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.friendlysnmp.FConstant;
import org.friendlysnmp.FException;
import org.friendlysnmp.FHandler;
import org.friendlysnmp.FID;
import org.friendlysnmp.FTable;

/**
 * The class provides application information to SNMP objects  
 * declared in FRIENDLY-SNMP-MIB.
 * 
 * @version $Revision: 1.14 $
 */
public class CoreAppInfoHandler extends FHandler {
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.FHandler#init()
     */
    @Override
    public void init() {
        // Static values. Load once after SNMP4J agent loads its persistent storage.
        loadAppDependenciesTable(); 
        loadAppInfoTable();
        loadAppPropTable();
        loadAppConfigTable();
        loadSysPropTable();
    } // init()
    
    /**
     * Loads application information table data (once at startup)
     */
    private void loadAppInfoTable() {
        FTable table = ((FriendlySnmpMibFriend)mibBase).getAppInfoEntry();
        try {
            Map<String, String> hm = agent.getConfig().getAppInfo();
            table.deleteAll();
            for (Entry<String, String> entry : hm.entrySet()) {
                FID idRow = table.addRowNext();
                table.setValueAt(entry.getKey(),  
                        idRow, FriendlySnmpMibFriend.COLUMN_AppInfoName);
                table.setValueAt(entry.getValue(),
                        idRow, FriendlySnmpMibFriend.COLUMN_AppInfoDesc);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadAppInfoTable()

    /**
     * Loads dependencies information table data (once at startup)
     */
    private void loadAppDependenciesTable() {
        FTable table = ((FriendlySnmpMibFriend)mibBase).getAppDependenciesEntry();
        try {
            Map<String, String> hm = agent.getConfig().getAppDependencies();
            table.deleteAll();
            for (Entry<String, String> entry : hm.entrySet()) {
                FID idRow = table.addRowNext();
                table.setValueAt(entry.getKey(),  
                        idRow, FriendlySnmpMibFriend.COLUMN_AppDependenciesName);
                table.setValueAt(entry.getValue(),
                        idRow, FriendlySnmpMibFriend.COLUMN_AppDependenciesDesc);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadAppDependenciesTable()

    /**
     * Loads application properties table data
     */
    private void loadAppPropTable() {
        FTable table = ((FriendlySnmpMibFriend)mibBase).getAppPropEntry();
        Map<String, String> hm = agent.getConfig().getAppProp();
        try {
            table.deleteAll();
            for (Entry<String, String> entry : hm.entrySet()) {
                FID idRow = table.addRowNext();
                table.setValueAt(entry.getKey(),  
                        idRow, FriendlySnmpMibFriend.COLUMN_AppPropKey);
                table.setValueAt(entry.getValue(),
                        idRow, FriendlySnmpMibFriend.COLUMN_AppPropValue);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadAppPropTable()

    /**
     * Loads application configuration table data
     */
    private void loadAppConfigTable() {
        final String[] a_sSecret = {
            FConstant.KEY_V3_PASSWORD_KEY,
            FConstant.KEY_V3_PASSWORD_AUTH,
            FConstant.KEY_V3_PASSWORD_PRIV
        };
        FTable table = ((FriendlySnmpMibFriend)mibBase).getAppConfigEntry();
        Map<String, String> hm = agent.getConfig().getConfigProp();
        try {
            table.deleteAll();
            for (Entry<String, String> entry : hm.entrySet()) {
                FID idRow = table.addRowNext();
                String key = entry.getKey();
                table.setValueAt(key, 
                        idRow, FriendlySnmpMibFriend.COLUMN_AppConfigKey);
                String val = entry.getValue();
                for (int i = 0;  i < a_sSecret.length;  i++) {
                    if (key.indexOf(a_sSecret[i]) == 0  &&  val.length() > 0) {
                        val = "****";
                        break;
                    }
                }
                table.setValueAt(val, 
                        idRow, FriendlySnmpMibFriend.COLUMN_AppConfigValue);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadAppConfigTable()
    
    /**
     * Loads system properties table data
     */
    private void loadSysPropTable() {
        FTable table = ((FriendlySnmpMibFriend)mibBase).getSysPropEntry();
        Map<String, String> hm = new TreeMap<String, String>();
        Properties prop = System.getProperties();
        Set<String> hsKey = prop.stringPropertyNames();
        for (String key : hsKey) {
            String val = prop.getProperty(key);
            hm.put(key, val);
        }
        try {
            table.deleteAll();
            for (Entry<String, String> entry : hm.entrySet()) {
                FID idRow = table.addRowNext();
                table.setValueAt(entry.getKey(),  
                        idRow, FriendlySnmpMibFriend.COLUMN_SysPropKey);
                table.setValueAt(entry.getValue(),
                        idRow, FriendlySnmpMibFriend.COLUMN_SysPropValue);
            }
        } catch (FException e) {
            mibBase.exceptionThrown(table, e);
        }
    } // loadSysPropTable()
    
} // class CoreAppInfoHandler
