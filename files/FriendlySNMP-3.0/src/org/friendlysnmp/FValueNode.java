/*
 * File: FValueNode.java
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
 * $Id: FValueNode.java,v 1.15 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.friendlysnmp.event.FRestoreDefaultEvent;
import org.friendlysnmp.event.FRestoreDefaultListener;
import org.friendlysnmp.persist.PersistEntryTable;
import org.friendlysnmp.persist.PersistStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

/**
 * Abstract base class which represents MIB node with a value (scalar, table).
 * 
 * @version $Revision: 1.15 $
 */
public abstract class FValueNode extends FNode {
    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(FValueNode.class);

    /**
     * Flag with indication that scalar was loaded from persistent storage.
     */
    protected boolean persistLoaded;
    
    /**
     * Collection of scalar VALIDATION listeners. 
     */
    private Set<FRestoreDefaultListener> hsRestoreDefaultListener;
    
    public FValueNode(String name, OID oid) {
        super(name, oid);
        hsRestoreDefaultListener = new CopyOnWriteArraySet<FRestoreDefaultListener>(); 
    }
    
    /**
     * Registers the given observer to begin receiving "restore default" events
     * when changes are made to the managed object.
     *
     * @param l the observer to register
     */
    public void addRestoreDefaultListener(FRestoreDefaultListener l) {
        hsRestoreDefaultListener.add(l);
    } // addRestoreDefaultListener()
    
    /**
     * Unregisters the given observer to stop receiving "restore default" events
     * when changes are made to the managed object.
     *
     * @param l the observer to unregister
     */
    public void removeRestoreDefaultListener(FRestoreDefaultListener l) {
        hsRestoreDefaultListener.remove(l);
    } // removeRestoreDefaultListener()
    
    /**
     * Returns flag that this node was loaded from persistent storage.
     * 
     * @return flag that scalar was loaded from persistent storage.
     */
    public boolean isPersistLoaded() {
        return persistLoaded;
    }
    
    /**
     * Loads persistent value into the scalar/table. If for any reason the 
     * load fails the persistent entry is removed for clean start in the 
     * next application run. 
     */
    protected abstract void loadPersistValue();
    
    /**
     * Sets the volatile flag for this node. This method must be called
     * explicitly with parameter <code>false</code> to load persistent value
     * for scalars and tables. 
     * 
     * @param volatileNode
     *    set to <code>true</code> to prevent saving the state of this object 
     *    in persistency storage by agent when the value is modified 
     *    from the MIB browser.
     */
    public void setVolatile(boolean volatileNode) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "%s Volatile=%s", getFIDtoString(), volatileNode));
        }
        setVolatilePrivate(volatileNode);
        if (volatileNode) {
            // Make sure that persistent entry does not exist.
            // Next application start will not pick it up.
            PersistStorage storage = getPersistStorage();
            PersistEntryTable entry = storage.getPersistTable(getFID());
            if (entry != null) {
                try {
                    storage.remove(entry.getFID()); // throws
                } catch (FException e) {
                    logger.error(ThrowableFormatter.format(
                            "Failure to load persistent table " + getFID(), e));
                }
            }
        } else {
            loadPersistValue();
        }
    }

    protected abstract void setVolatilePrivate(boolean isVolatile);
    
    /**
     * Fires RESTORE DEFAULT event.
     */
    public synchronized void fireRestoreDefaultEvent() 
    throws FException 
    {
        if (logger.isDebugEnabled()) {
            logger.debug("FIRE Restore Default event for " + getFIDtoString());
        }
        for (FRestoreDefaultListener l : hsRestoreDefaultListener) {
            l.restoreDefault(new FRestoreDefaultEvent(this));
        }
    } // fireRestoreDefaultEvent()

    /**
     * Returns volatile flag. Volatile objects are not stored in persistency
     * storage after their modification via management protocol. 
     * 
     * @return volatile flag
     */
    public abstract boolean isVolatile();

} // class FValueNode
