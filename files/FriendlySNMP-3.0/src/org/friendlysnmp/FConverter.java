/*
 * File: FConverter.java
 * 
 * Copyright (C) 2014 FriendlySNMP.org; All Rights Reserved.
 * 
 * Thanks to Matthias Wiesmann for his code published at
 * http://ddsg.jaist.ac.jp/~wiesmann/share/snmp4j-extensions
 * http://ddsg.jaist.ac.jp/~wiesmann/share/snmp4j-extensions/Extensions.jar
 * which was used as a basic for this converter. 
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
 * $Id: FConverter.java,v 1.21 2014/01/17 03:22:24 mg Exp $
 */
package org.friendlysnmp;

import java.net.InetAddress;
import java.util.BitSet;

import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Opaque;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;

/**
 * This class contains collection of static methods to convert 
 * <code>java.lang</code> objects to and from <code>org.snmp4j.smi.Variable</code>
 * <p>
 * The code is based on converter used in 
 * SNMP-FD - Failure Detection service based on SNMP
 * by Matthias Wiesmann
 * http://ddg.jaist.ac.jp/en/projects/snmp-fd/
 * @version $Revision: 1.21 $
 */
public class FConverter {

    private FConverter() {
        // Do not instantiate
    } // FConverter()
    
    /**
     * Returns default values of different types, like <code>0</code> for 
     * numbers, empty string, <code>0.0.0.0</code> for IP address.
     * 
     * @param syntax SMI syntax
     * @return default value of specified SMI syntax
     */
    public static final Object defaultJavaValue(int syntax) {
        switch (syntax) {
            case SMIConstants.SYNTAX_INTEGER32:
                return Integer.valueOf(0);
            case SMIConstants.SYNTAX_COUNTER32: // extends UNSIGNED_INTEGER32
            case SMIConstants.SYNTAX_TIMETICKS: // extends UNSIGNED_INTEGER32     
            case SMIConstants.SYNTAX_UNSIGNED_INTEGER32: // == GAUGE32
            case SMIConstants.SYNTAX_COUNTER64:
                return Long.valueOf(0);
            case SMIConstants.SYNTAX_IPADDRESS:
                return "0.0.0.0";
            case SMIConstants.SYNTAX_OCTET_STRING: // == SYNTAX_BITS:
            case SMIConstants.SYNTAX_OBJECT_IDENTIFIER:
            case SMIConstants.SYNTAX_OPAQUE: // extends OCTET_STRING
                return "";
            case SMIConstants.SYNTAX_NULL: // ???
            default:
                return null;
        }
    } // defaultJavaValue()
    
    /** 
     * Converts a SMI variable to the <code>java.lang</code> object.
     * 
     * @param var a SMI variable
     * @return a <code>java.lang</code> object
     */
    public static final Object toJava(Variable var) {
        if (var == null) { 
            return null; 
        }
        int syntax = var.getSyntax(); 
        switch (syntax) {
            case SMIConstants.SYNTAX_INTEGER32:
                return ((Integer32)var).getValue(); // Integer autoboxing
            case SMIConstants.SYNTAX_COUNTER32: // extends UNSIGNED_INTEGER32
            case SMIConstants.SYNTAX_TIMETICKS: // extends UNSIGNED_INTEGER32     
            case SMIConstants.SYNTAX_UNSIGNED_INTEGER32: // == GAUGE32
                // TimeTicks, Gauge32, Counter32
                return ((UnsignedInteger32)var).getValue(); // Long autoboxing 
            case SMIConstants.SYNTAX_COUNTER64:
                return ((Counter64)var).getValue(); // Long autoboxing
            case SMIConstants.SYNTAX_IPADDRESS:
            case SMIConstants.SYNTAX_OCTET_STRING: // == SYNTAX_BITS:
            case SMIConstants.SYNTAX_OBJECT_IDENTIFIER:
            case SMIConstants.SYNTAX_OPAQUE: // extends OCTET_STRING    
            case SMIConstants.SYNTAX_NULL: // ???
            default:
                return var.toString();
        } 
    } // toJava()

    /**
     * Converts a <code>java.lang</code> object into a SMI object by calling 
     * the appropriate 'parseXXX' methods. 
     *  
     * @param obj the <code>java.lang</code> object to convert 
     * @param syntax the SMI syntax of this scalar to apply to the object
     * @return converted object
     * @throws FException
     */
    public static final Variable toVariable(Object obj, ValueSyntax syntax) 
    throws FException 
    {
        return toVariable(obj, syntax.toInt());
    } // toVariable()
    
    /**
     * Converts a <code>java.lang</code> object into a SMI object by calling 
     * the appropriate 'parseXXX' methods. 
     *  
     * @param obj the <code>java.lang</code> object to convert 
     * @param moScalar the SMI syntax of this scalar to apply to the object
     * @return converted object
     * @throws FException
     */
    public static final Variable toVariable(Object obj, MOScalar<?> moScalar) 
    throws FException 
    {
        int nSyntax = moScalar.getValue() == null ?
                SMIConstants.SYNTAX_NULL : moScalar.getValue().getSyntax(); 
        return toVariable(obj, nSyntax);
    } // toVariable()
    
    /**
     * Converts a <code>java.lang</code> object into a SMI object by calling 
     * the appropriate 'parseXXX' methods. 
     *  
     * @param obj the <code>java.lang</code> object to convert 
     * @param moColumn the SMI syntax of this column to apply to the object
     * @return converted object
     * @throws FException
     */
    public static final Variable toVariable(Object obj, MOColumn<?> moColumn) 
    throws FException 
    {
        return toVariable(obj, moColumn.getSyntax());
    } // toVariable()
    
    /** 
     * Converts a <code>java.lang</code> object into a SMI object by calling 
     * the appropriate 'parseXXX' methods. 
     *  
     * @param obj the <code>java.lang</code> object to convert 
     * @param syntax the SMI syntax to apply to the object
     * @return converted object
     * @throws FException throws if obj cannot be parsed as a Number
     * for SYNTAX_INTEGER32, SYNTAX_COUNTER64 and SYNTAX_UNSIGNED_INTEGER32
     */
    public static final Variable toVariable(Object obj, int syntax) 
    throws FException 
    {
        if (obj == null) {
            syntax = SMIConstants.SYNTAX_NULL;
        }
        if (obj instanceof Variable) {
            return (Variable)obj; // smart application already knows format
        }
        switch (syntax) {
            case SMIConstants.SYNTAX_OCTET_STRING: // == SYNTAX_BITS
                return obj == null ? new OctetString() : new OctetString(obj.toString());
            case SMIConstants.SYNTAX_OPAQUE: // extends OCTET_STRING
                OctetString o = obj == null ? new OctetString() : new OctetString(obj.toString());
                return new Opaque(o.toByteArray());
            case SMIConstants.SYNTAX_INTEGER32:
                return parseInteger32(obj); // throws
            case SMIConstants.SYNTAX_COUNTER64:
                return parseCounter64(obj);  // throws
            case SMIConstants.SYNTAX_COUNTER32:
                return parseCounter32(obj);  // throws
            case SMIConstants.SYNTAX_TIMETICKS:     
                return parseTimeTicks(obj);  // throws
            case SMIConstants.SYNTAX_UNSIGNED_INTEGER32: // == GAUGE32 
                return parseUnsignedInteger32(obj, syntax); // throws
            case SMIConstants.SYNTAX_OBJECT_IDENTIFIER:
                return parseOID(obj);
            case SMIConstants.SYNTAX_IPADDRESS:
                return parseIpAddress(obj); 
            case SMIConstants.SYNTAX_NULL:
                return new Null();
            default:
                throw new FException(
                        "Cannot convert '%s' with Syntax=%d", obj, syntax); 
        }
    } // toVariable()
   
    /**
     * Parses 16 bit integer to <code>OctetString</code>
     * 
     * @param val 16 bit integer
     * @return <code>OctetString</code>
     */
    public static final OctetString parseBITS16(int val) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)( val >>> 24);
        bytes[1] = (byte)((val & 0x00FF0000) >> 16);
        return new OctetString(bytes);
    } // parseBITS16()
    
    /**
     * Parses 32 bit integer to <code>OctetString</code>
     * 
     * @param val 32 bit integer
     * @return <code>OctetString</code>
     */
    public static final OctetString parseBITS32(int val) {
        //  SMIConstants.SYNTAX_BITS type interpreted as:
        //  1: '0100 0000 0000 0000 0000 0000 0000 0000' OctetString: 40:00:00:00
        //  9: '0000 0000 0100 0000 0000 0000 0000 0000' OctetString: 00:40:00:00
        // 22: '0000 0000 0000 0000 0000 0010 0000 0000' OctetString: 00:00:02:00
        // 23: '0000 0000 0000 0000 0000 0001 0000 0000' OctetString: 00:00:01:00
        // 30: '0000 0000 0000 0000 0000 0000 0000 0010' OctetString: 00:00:00:02
        // 31: '0000 0000 0000 0000 0000 0000 0000 0001' OctetString: 00:00:00:01
        // Bits are counted from left to right.
        byte[] bytes = new byte[4];
        bytes[0] = (byte)( val >>> 24);
        bytes[1] = (byte)((val & 0x00FF0000) >> 16);
        bytes[2] = (byte)((val & 0x0000FF00) >> 8);
        bytes[3] = (byte)( val & 0x000000FF);
        return new OctetString(bytes);
    } // parseBITS32()
    
    /**
     * Parses 64 bit integer to <code>OctetString</code>
     * 
     * @param val 64 bit integer
     * @return <code>OctetString</code>
     */
    public static final OctetString parseBITS64(long val) {
        throw new RuntimeException("Not implemented");
    } // parseBITS64()
    
    /** 
     * Parses Integer or String to Integer32.
     * 
     * @param obj the object to convert
     * @return an instance of Integer32
     */
    public static final Integer32 parseInteger32(Object obj) throws FException {
        if (obj instanceof Number) {
            return new Integer32(((Number)obj).intValue()); 
        }
        try {
            return new Integer32(Integer.parseInt(obj.toString())); 
        } catch (Exception e) {
            // ClassCastException, NumberFormatException
            throw new FException("Failure to parse as Integer32", e); 
        }
    } // parseInteger32()
   
    /** 
     * Parses Long or String to Counter64.
     * 
     * @param obj the object to convert
     * @return an instance of Counter64
     */
    public static final Counter64 parseCounter64(Object obj) throws FException {
        if (obj instanceof Number) {
            return new Counter64(((Number)obj).longValue()); 
        } 
        try {
            return new Counter64(Long.parseLong(obj.toString())); 
        } catch (Exception e) {
            // ClassCastException, NumberFormatException
            throw new FException("Failure to parse as Counter64", e); 
        } 
    } // parseCounter64()
   
    /** 
     * Parses Long or String to Counter32.
     * 
     * @param obj the object to convert
     * @return an instance of Counter32
     */
    public static final Counter32 parseCounter32(Object obj) throws FException {
        if (obj instanceof Number) {
            return new Counter32(((Number)obj).longValue()); 
        } 
        try {
            return new Counter32(Long.parseLong(obj.toString())); 
        } catch (Exception e) {
            // ClassCastException, NumberFormatException
            throw new FException("Failure to parse as Counter32", e); 
        } 
    } // parseCounter32()
   
    /** 
     * Parses Long or String to TimeTicks.
     * 
     * @param obj the object to convert
     * @return an instance of TimeTicks
     */
    public static final TimeTicks parseTimeTicks(Object obj) throws FException {
        if (obj instanceof Number) {
            return new TimeTicks(((Number)obj).longValue()); 
        } 
        try {
            return new TimeTicks(Long.parseLong(obj.toString())); 
        } catch (Exception e) {
            // ClassCastException, NumberFormatException
            throw new FException("Failure to parse as TimeTicks", e); 
        } 
    } // parseTimeTicks()
   
    /** 
     * Parses Long or String to UnsignedInteger32.
     * 
     * @param obj the object to convert
     * @param syntax variable syntax as defined by SNMP4J (this value is 
     * native to scalar or table cell and is defined in MIB) 
     * @return an instance of UnsignedInteger32
     * @throws FException
     */
    public static final UnsignedInteger32 parseUnsignedInteger32(Object obj, int syntax) 
    throws FException 
    {
        if (obj instanceof Number) {
            return new UnsignedInteger32(((Number)obj).longValue()); 
        } 
        try {
            return new UnsignedInteger32(Long.parseLong(obj.toString())); 
        } catch (NumberFormatException e) {
            // ClassCastException, NumberFormatException
            throw new FException("Failure to parse as UnsignedInteger32", e); 
        } 
    } // parseUnsignedInteger32()
    
    /** 
     * Parses Number[], int[] or String to OID.
     * 
     * @param obj the object to convert
     * @return an instance of OID
     */
    public static final OID parseOID(Object obj) {
       if (obj instanceof Number[]) {
           Number[] numberAll = (Number[])obj; 
           int[] intAll = new int[numberAll.length]; 
           for (int i = 0;  i < numberAll.length; i++) {
               intAll[i] = numberAll[i].intValue(); 
           }
           return new OID(intAll); 
       } 
       if (obj instanceof int[]) {
           return new OID((int[])obj); 
       }
       return new OID(obj.toString()); 
   } // parseOID()
   
    /** 
     * Parses InetAddress or String to IpAddress.
     * 
     * @param obj the object to convert
     * @return an instance of IpAddress
     */
    public static final IpAddress parseIpAddress(Object obj) {
       if (obj instanceof InetAddress) {
           return new IpAddress((InetAddress)obj); 
       } 
       return new IpAddress(obj.toString());
    } // parseIpAddress()
 
    /**
     * SNMP enumerates bits from left-to-right with 0-based index. The bit 0 
     * is a sign bit, and so on. Only first 32 bits from <code>BitSet</code> 
     * parameter are used to set bits for resulting <code>int</code> value.  
     * 
     * @param bs <code>BitSet</code> with first 32 bits considered 
     * @return int value with bits set
     */
    public static final int toInt(BitSet bs) {
        int value = 0; 
        for (int i = 0;  i < 32;  i++ ) {
            if (bs.get(i)) {
                value |= (0x80000000 >>> i);
            }
        }
        return value;
    } // toInt()
    
} // class FConverter
