/*
 * File: FPlugin.java
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
 * $Id: FPlugin.java,v 1.12 2014/01/11 02:19:27 mg Exp $
 */
package org.friendlysnmp.plugin;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FException;
import org.friendlysnmp.mib.BaseMib;

public abstract class FPlugin {

    protected BaseMib mibBase;
    
    /** 
     * SNMP agent. 
     */
    protected AgentWorker agent;
    
    public FPlugin(BaseMib mib) {
        this.mibBase = mib;
    }
    
    public BaseMib getMib() {
        return mibBase;
    }
    
    public void setAgent(AgentWorker agent) {
        this.agent = agent;
    }
    
    /**
     * The agent calls this method to initialize plugin.
     * 
     * @throws FException
     */
    public abstract void initPlugin() throws FException;

    /**
     * The agent calls this method to load plugin specific default properties.
     */
    public void loadDefaultProperties() {
        // Default implementation does nothing.
    }
    
    /**
     * Call this method in the derived class in the overriden 
     * method {@link FPlugin#loadDefaultProperties()}.
     * The method could be called multiple times (or not at all)
     * to load multiple default properties. The convention for the property 
     * keys is "plugin.name.key", where "name" is a plugin name.
     * This naming style eliminates potential keys collisions.
     * 
     * @param key
     * @param value
     */
    protected void loadDefaultProperty(String key, String value) {
        agent.getConfig().addDefaultProperty(key, value);
    }
    
} // class FPlugin
