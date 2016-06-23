/*
 * File: PersistExternalizable.java
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
 * $Id: PersistExternalizable.java,v 1.10 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import org.friendlysnmp.AgentWorker;

public abstract class PersistExternalizable 
// Switch temporarily to Serializable to use 
// Eclipse built-in serialVersionUID generator:  
//implements Serializable { 
implements Externalizable {

    private static final long serialVersionUID = -5293631090926040424L;

    public PersistExternalizable(boolean serialized) {
        if (!serialized) {
            return;
        }
        final String CLASS_OBJECT_INSTREAM = ObjectInputStream.class.getName(); 
        final String CLASS_AGENT_WORKER = AgentWorker.class.getName(); 
        StackTraceElement[] stAll = (new Throwable()).getStackTrace();
        for (StackTraceElement st : stAll) {
            String classFrom = st.getClassName();
            if (classFrom.equals(CLASS_OBJECT_INSTREAM)) {
                return;
            }
            if (classFrom.equals(CLASS_AGENT_WORKER)) {
                return;
            }
        }
        throw new RuntimeException("This ctor call is allowed only from deserialization.");
    }

    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        // Does nothing. Helps to switch Externalizable to Serializable
        // and do not have compilation errors.
    }

    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        // Does nothing. Helps to switch Externalizable to Serializable
        // and do not have compilation errors.
    }
    
} // class PersistExternalizable
