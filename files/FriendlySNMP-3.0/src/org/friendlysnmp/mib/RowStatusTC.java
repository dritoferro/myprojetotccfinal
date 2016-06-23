/*
 * File: RowStatusTC.java
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
 * $Id: RowStatusTC.java,v 1.4 2014/01/11 02:19:24 mg Exp $
 */
package org.friendlysnmp.mib;

/**
 * Convenience class to use RowStatus integer values. 
 * It cannot be converted to enum because its values are converted 
 * by FConverter from Integer to org.snmp4j.smi.Variable object.
 * 
 * Fields spelling and comments are from RFC-2579 SNMPv2-TC MIB. 
 */
public class RowStatusTC {
    public static final Integer active = 1;
    
    /* The following value is a state: this value may be read, but not written */
    public static final Integer notInService = 2;
    
    /* The following three values are actions: these values may be written, but are never read */
    public static final Integer notReady = 3;
    public static final Integer createAndGo = 4;
    public static final Integer createAndWait = 5;
    
    public static final Integer destroy = 6;
}
