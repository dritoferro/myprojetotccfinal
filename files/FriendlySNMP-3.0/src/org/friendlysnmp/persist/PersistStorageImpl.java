/*
 * File: PersistStorageImpl.java
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
 * $Id: PersistStorageImpl.java,v 1.21 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.friendlysnmp.FException;
import org.friendlysnmp.FID;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.FTable;
import org.friendlysnmp.ThrowableFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of <code>PersistStorage</code>, which provides persistence 
 * in plain <code>HashMap&lt;String,String&gt;</code> serialized into file.
 * 
 * @version $Revision: 1.21 $
 */
public class PersistStorageImpl extends PersistStorage {
    /**
     * Logger object
     */
    private static final Logger logger = LoggerFactory.getLogger(PersistStorageImpl.class);

    /**
     * File object for serialization data.
     */
    private File filePersist;
    
    /**
     * Scalars collection. 
     */
    private TreeMap<FID, PersistEntryScalar> hmScalar;
    
    /**
     * Tables collection. 
     */
    private TreeMap<FID, PersistEntryTable> hmTable;
    
    /**
     * Constructor
     * 
     * @param filename filename
     * @throws FException 
     */
    public PersistStorageImpl(String filename) throws FException {
        hmScalar = new TreeMap<FID, PersistEntryScalar>();
        hmTable  = new TreeMap<FID, PersistEntryTable>();
        logger.debug(String.format("Storage filename '%s'", filename));
        // Validation
        filename = filename.trim();
        if (filename.isEmpty()) {
            throw new FException("Persistent storage filename is empty.");
        }
        filePersist = new File(filename);
        String canonical;
        try {
            canonical = filePersist.getCanonicalPath();
        } catch (IOException e) {
            throw new FException("Not valid persistent storage filename " + filename, e);
        }
        
        if (!filePersist.isFile()) { // file does not exist or directory 
            // Validate filename by creating empty storage:
            save(new HashSet<PersistEntryNode>());
            return;
        }
        if (!filePersist.canWrite()) {
            // This check is not guarantee that file canWrite(): 
            // WinXP Java 1.6.0_05 for the file without "write"  
            // permission returns canWrite() true. The check works for folder.
            throw new FException("Cannot write into persistent storage file %s", canonical);
        }
        // Loading
        PersistEntryNode[] persist = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
                  new FileInputStream(filePersist));
            persist = (PersistEntryNode[])ois.readObject();
            ois.close();
        } catch (Exception e) {
            // FileNotFoundException, IOException, ClassCastException
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e1) {
                }
            }
            logger.error(ThrowableFormatter.format(
                    "Failure to load persistent storage. " +
                    "Old incompatible persistent storage is ignored.", e));
        }
        logger.info("Loaded persist file: " + canonical);
        if (persist == null) {
            // Not compatible format
            return;
        }
        for (PersistEntryNode p : persist) {
            FID id = p.getFID();
            if (p instanceof PersistEntryScalar) {
                hmScalar.put(id, (PersistEntryScalar)p);
            }
            if (p instanceof PersistEntryTable) {
                hmTable.put(id, (PersistEntryTable)p);
            }
        }
    } // PersistStorageImpl()
    
    //--------------------------------separator--------------------------------
    static int ______SCALARS;

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.persist.PersistStorage#put(org.friendlysnmp.FScalar)
     */
    @Override
    public void put(FScalar scalar) throws FException {
        FID id = scalar.getFID();
        PersistEntryScalar peOLD = getPersistScalar(id); // might be null
        PersistEntryScalar peNEW = new PersistEntryScalar(scalar);
        if (!peNEW.equals(peOLD)) {
            logger.debug(peNEW.toString());
            hmScalar.put(id, peNEW);
            if (persistPolicy == PersistPolicy.ON_CHANGE) {
                save();
            }
        }
    } // put()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.persist.PersistStorage#getPersistScalar(FID)
     */
    @Override
    public PersistEntryScalar getPersistScalar(FID id) {
        return hmScalar.get(id); 
    } // getPersistScalar()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.persist.PersistStorage#getPersistScalars()
     */
    @Override
    public PersistEntryScalar[] getPersistScalars() {
        return hmScalar.values().toArray(new PersistEntryScalar[hmScalar.size()]);
    } // getPersistScalars()
    
    //--------------------------------separator--------------------------------
    static int ______TABLES;

    /** 
     * {@inheritDoc}
     *
     * @see org.friendlysnmp.persist.PersistStorage#put(org.friendlysnmp.FTable)
     */
    @Override
    public void put(FTable table) throws FException {
        FID id = table.getFID();
        PersistEntryTable peOLD = getPersistTable(id); // might be null
        PersistEntryTable peNEW = new PersistEntryTable(table);
        if (!peNEW.equals(peOLD)) {
            logger.debug(peNEW.toString());
            hmTable.put(id, peNEW);
            if (persistPolicy == PersistPolicy.ON_CHANGE) {
                save();
            }
        }
    } // put()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.persist.PersistStorage#getPersistScalar(FID)
     */
    @Override
    public PersistEntryTable getPersistTable(FID id) {
        return hmTable.get(id); 
    } // getPersistTable()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.persist.PersistStorage#getPersistScalars()
     */
    @Override
    public PersistEntryTable[] getPersistTables() {
        return hmTable.values().toArray(new PersistEntryTable[hmTable.size()]);
    } // getPersistTables()
    
    //--------------------------------separator--------------------------------
    static int ______ALL;

    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.persist.PersistStorage#remove(org.friendlysnmp.FID)
     */
    @Override
    public void remove(FID id) throws FException {
        // Resides in one of collections:
        hmScalar.remove(id);
        hmTable.remove(id);
        if (persistPolicy == PersistPolicy.ON_CHANGE) {
            save();
        }
    } // remove()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.persist.PersistStorage#save()
     */
    @Override
    public void save() throws FException {
        Set<PersistEntryNode> hs = new HashSet<PersistEntryNode>();
        hs.addAll(hmScalar.values());
        hs.addAll(hmTable.values());
        save(hs);
    } // save()
    
    /**
     * Saves persistent storage collection
     * 
     * @param hs collection
     * @throws FException
     */
    private void save(Set<PersistEntryNode> hs) throws FException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                                     new FileOutputStream(filePersist));
            oos.writeObject(hs.toArray(new PersistEntryNode[hs.size()]));
            oos.close();
        } catch (Exception e) {
            // FileNotFoundException, IOException
            throw new FException(
                    "Failure to save/create persistence file " + filePersist, e);
        }
    }
    
} // class PersistStorageImpl
