/*
 * File: TransportSnmp.java
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
 * $Id: TransportSnmp.java,v 1.8 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import org.friendlysnmp.TransportDomain;
import org.snmp4j.agent.mo.snmp.TransportDomains;
import org.snmp4j.smi.OID;

/**
 * Enumeration of allowed transports. 
 */
public enum TransportSnmp {
        /** UDP transport with list of transport domains */
        UDP(TransportDomains.transportDomainUdpIpv4,
            TransportDomains.transportDomainUdpIpv4z,
            TransportDomains.transportDomainUdpIpv6,
            TransportDomains.transportDomainUdpIpv6z),
        /** TCP transport with list of transport domains */
        TCP(TransportDomains.transportDomainTcpIpv4,
            TransportDomains.transportDomainTcpIpv4z,
            TransportDomains.transportDomainTcpIpv6,
            TransportDomains.transportDomainTcpIpv6z)
        ;
    private OID oidIPV4;
    private OID oidIPV4Z;
    private OID oidIPV6;
    private OID oidIPV6Z;
    private TransportSnmp(OID oidIPV4, OID oidIPV4Z, OID oidIPV6, OID oidIPV6Z) {
        this.oidIPV4 = oidIPV4;
        this.oidIPV4Z = oidIPV4Z;
        this.oidIPV6 = oidIPV6;
        this.oidIPV6Z = oidIPV6Z;
    }
    public OID getTransportDomainOID(TransportDomain td) {
        switch (td) {
            case IPV4:
                return oidIPV4;
            case IPV4Z:
                return oidIPV4Z;
            case IPV6:
                return oidIPV6;
            case IPV6Z:
                return oidIPV6Z;
        }
        throw new IllegalArgumentException("Not valid transport domain: " + td);
    }
    
} // class TransportSnmp
