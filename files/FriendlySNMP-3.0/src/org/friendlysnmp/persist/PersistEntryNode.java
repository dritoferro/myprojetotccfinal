/*
 * File: PersistEntryNode.java
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
 * $Id: PersistEntryNode.java,v 1.9 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.friendlysnmp.FID;
import org.friendlysnmp.FNode;

/**
 * Persistence storage entry class.
 * 
 * @version $Revision: 1.9 $
 */
public abstract class PersistEntryNode extends PersistExternalizable {

    /**
     * Generated UID. This UID should be changed only after 
     * serialized member variables modification. 
     * 
     * This UID is valid for a single serialized member variable: 
     * <br>(1) FID id; 
     */
    private static final long serialVersionUID = 328889926701628815L;
    
    protected FID id;
   
    protected PersistEntryNode() {
        super(true);
    }
    
    protected PersistEntryNode(FNode node) {
        super(false);
        id = node.getFID();
    }
    
    public FID getFID() {
        return id;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        id = new FID();
        id.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        id.writeExternal(out);
    }
    
} // class PersistEntryNode
