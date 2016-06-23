/*
 * File: ServerLookupAdapter.java
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
 * $Id: ServerLookupAdapter.java,v 1.10 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import org.snmp4j.agent.MOServerLookupEvent;
import org.snmp4j.agent.MOServerLookupListener;

/**
 * An abstract adapter class for receiving server lookup events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Extend this class to create a <code>MOServerLookupEvent</code> listener 
 * and override the methods for the events of interest. If you implement the 
 * <code>MOServerLookupListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines empty methods for all of them,
 * so you can only have to define methods for events you care about.
 * <P>
 * Create a listener object using the extended class and then register it with 
 * a component using the component's <code>addLookupListener</code> 
 * method.  
 * 
 * @version $Revision: 1.10 $
 */
abstract class ServerLookupAdapter implements MOServerLookupListener {
    
    /**
     * Default implementation does nothing.
     */
    public void lookupEvent(MOServerLookupEvent event) {
    }

    /**
     * Default implementation does nothing.
     */
    public void queryEvent(MOServerLookupEvent event) {
    }
    
} // class ServerLookupAdapter
