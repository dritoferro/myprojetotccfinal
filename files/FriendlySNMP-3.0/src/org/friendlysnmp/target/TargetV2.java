/*
 * File: TargetV2.java
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
 * $Id: TargetV2.java,v 1.17 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.target;

import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;

/**
 * SNMP v2c target implementation.
 * 
 * @version $Revision: 1.17 $
 */
public class TargetV2 extends TargetV1 {

    /**
     * Constructor
     * 
     * @param targetName target (V2 community name)
     */
    public TargetV2(String targetName) {
        super(targetName);
        setSNMP(SecurityModel.SECURITY_MODEL_SNMPv2c, 
                MessageProcessingModel.MPv2c, 
                SecurityLevel.NOAUTH_NOPRIV);
    } // TargetV2()
    
} // class TargetV2
