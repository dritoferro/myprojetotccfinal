/*
 * File: PersistStorage.java
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
 * $Id: PersistStorage.java,v 1.13 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import org.friendlysnmp.FException;
import org.friendlysnmp.FID;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract persistence storage class which gives an option for various
 * persistency implementations.
 * 
 * @version $Revision: 1.13 $
 */
public abstract class PersistStorage {
    /**
     * Logger object
     */
    private static final Logger logger = LoggerFactory.getLogger(PersistStorage.class);

    /**
     * Persistence policy
     */
    protected PersistPolicy persistPolicy;
    
    /**
     * Shutdown flag 
     */
    private boolean shutdown;
    
    //--------------------------------separator--------------------------------
    static int ______SCALARS;

    /**
     * Saves scalar in persistence storage.
     * 
     * @param scalar scalar to be saved in persistent storage
     * @throws FException
     */
    public abstract void put(FScalar scalar) throws FException;
    
    /**
     * Returns persistent scalar entry or null if not exist.
     * 
     * @param id id of the stored object
     * @return stored persistent scalar entry
     */
    public abstract PersistEntryScalar getPersistScalar(FID id);
    
    /**
     * Returns array of persistent entries with scalars 
     * in this persistence storage.
     * 
     * @return array of stored persistent entries with scalars 
     * in this persistence storage.
     */
    public abstract PersistEntryScalar[] getPersistScalars();
    
    //--------------------------------separator--------------------------------
    static int ______TABLES;

    /**
     * Saves table in persistence storage.
     * 
     * @param table table to be saved in persistent storage
     * @throws FException
     */
    public abstract void put(FTable table) throws FException;
    
    /**
     * Returns persistent scalar entry or null if not exist.
     * 
     * @param id id of the stored object
     * @return stored persistent scalar entry
     */
    public abstract PersistEntryTable getPersistTable(FID id);
    
    /**
     * Returns array of persistent entries with scalars 
     * in this persistence storage.
     * 
     * @return array of stored persistent entries with scalars 
     * in this persistence storage.
     */
    public abstract PersistEntryTable[] getPersistTables();
    
    //--------------------------------separator--------------------------------
    static int ______ALL;

    /**
     * Removes object from persistence storage. 
     * 
     * @param id
     */
    public abstract void remove(FID id) throws FException;
    
    /**
     * Saves persistence storage. Default implementation does nothing.
     * 
     * @throws FException
     */
    public abstract void save() throws FException;
    
    /**
     * Sets persistence policy
     * 
     * @param p persistence policy
     */
    public void setPersistPolicy(PersistPolicy p) {
        logger.debug("Persistency policy: " + p);
        persistPolicy = p;
    } // setPersistPolicy()
    
    /**
     * Returns persistence policy
     * 
     * @return persistence policy
     */
    public PersistPolicy getPersistPolicy() {
        return persistPolicy;
    } // getPersistPolicy()

    /**
     * Shutdowns persistence storage.
     * 
     * @throws FException
     */
    public void shutdown() throws FException {
        if (!shutdown  &&  persistPolicy == PersistPolicy.ON_EXIT) {
            save();
        }
        shutdown = true;
    } // shutdown()   
    
} // class PersistStorage
