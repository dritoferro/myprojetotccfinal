/*
 * File: PersistEntryScalar.java
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
 * $Id: PersistEntryScalar.java,v 1.23 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.persist;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.friendlysnmp.FConverter;
import org.friendlysnmp.FException;
import org.friendlysnmp.FScalar;
import org.friendlysnmp.ThrowableFormatter;
import org.friendlysnmp.ValueSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.smi.Variable;

/**
 * Persistence storage entry class.
 * 
 * @version $Revision: 1.23 $
 */
public class PersistEntryScalar extends PersistEntryNode {
    
    /**
     * Generated UID. This UID should be changed only after 
     * serialized member variables modification. 
     * 
     * This UID is valid for two serialized member variables: 
     * <br>(1) Object scalarValue; 
     * <br>(2) ValueSyntax scalarSyntax;
     */
    private static final long serialVersionUID = 152235457062999765L;

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(PersistEntryScalar.class);

    private Object scalarValue; // Java object (not org.snmp4j.smi.Variable)
    private ValueSyntax scalarSyntax;
    
    /**
     * Default ctor for deserialization
     */
    public PersistEntryScalar() {
        super();
    }
    
    public PersistEntryScalar(FScalar scalar) {
        super(scalar);
        MOScalar<?> mo = scalar.getMOScalar();
        Variable var = mo.getValue();
        scalarValue = FConverter.toJava(var);
        scalarSyntax = ValueSyntax.find(var.getSyntax());
    }
    
    public Object getScalarValue() {
        return scalarValue;
    }
    
    public ValueSyntax getScalarSyntax() {
        return scalarSyntax;
    }
    
    /**
     * Loading persistent value into {@link FScalar}. 
     * The {@link FScalar} removes this value from persistent storage
     * in case of value syntax mismatch.
     * 
     * @param scalar scalar object which loads value from persistent storage
     * @return flag true if the value was loaded
     * @throws FException attempt to load wrong scalar
     */
    public boolean loadPersist2Scalar(FScalar scalar) {
        if (!id.equals(scalar.getFID())) {
            logger.error(String.format(
                "Cannot load scalar %s -mismatch FID %s", scalar, id));
            return false;
        }
        MOScalar<?> mo = scalar.getMOScalar();
        if (!mo.getAccess().isAccessibleForWrite()) {
            logger.error(String.format(
                "Failure to load persistent scalar %s (not read-write)", 
                scalar));
            return false;
        }
        
        if (scalar.isVolatile()) {
            logger.error(String.format(
                "Failure to load persistent scalar %s (volatile)", 
                scalar.getFIDtoString()));
            return false;
        }
        
        int n1 = scalarSyntax.toInt();
        int n2 = mo.getValue().getSyntax();
        if (n1 != n2) {
            logger.error(String.format(
                 "Failure to load persistent scalar %s; " +
                 "Syntax does not match: %s <-> %s",
                 scalar.getFIDtoString(), scalarSyntax, ValueSyntax.find(n2)));
            return false;
        }
        
        try {
            scalar.setValue(scalarValue);
            logger.debug(String.format(
                 "Scalar %s: loaded persistent value: '%s' Syntax: %s", 
                    id, scalarValue, scalarSyntax));
            return true; // single valid return
        } catch (FException e) {
            logger.error(ThrowableFormatter.format(
                    "Failure to load persistent scalar " + scalar.getFIDtoString(), e)); 
            return false;
        }
    } // loadPersist2Scalar()

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PersistEntryScalar) {
            PersistEntryScalar that = (PersistEntryScalar)obj;
            if (!this.id.equals(that.id)) {
                return false;
            }
            if (!this.id.getName().equals(that.id.getName())) {
                return false;
            }
            if (!this.scalarValue.equals(that.scalarValue)) {
                return false;
            }
            if (!this.scalarSyntax.equals(that.scalarSyntax)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s Value: %s Syntax: ", 
                getClass().getSimpleName(), id, scalarValue, scalarSyntax);
    }
    
    @Override
    public void readExternal(ObjectInput in) 
    throws IOException, ClassNotFoundException 
    {
        super.readExternal(in);
        scalarValue = in.readObject();
        int syntax = in.readInt();
        scalarSyntax = ValueSyntax.find(syntax);
    }

    @Override
    public void writeExternal(ObjectOutput out) 
    throws IOException 
    {
        super.writeExternal(out);
        out.writeObject(scalarValue);
        out.writeInt(scalarSyntax.toInt());
    }
    
} // class PersistEntryScalar
