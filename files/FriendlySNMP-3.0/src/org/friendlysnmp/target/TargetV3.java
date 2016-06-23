/*
 * File: TargetV3.java
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
 * $Id: TargetV3.java,v 1.16 2014/01/11 02:19:25 mg Exp $
 */
package org.friendlysnmp.target;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.friendlysnmp.AgentWorker;
import org.friendlysnmp.FConfig;
import org.friendlysnmp.FConstant;
import org.friendlysnmp.FException;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

/**
 * SNMP v3 target implementation.
 * 
 * @version $Revision: 1.16 $
 */
public class TargetV3 extends TargetBase {

    private enum AuthProtocol {
        NONE(null),
        MD5(AuthMD5.ID),
        SHA(AuthSHA.ID);
        private OID oid;
        private AuthProtocol(OID oid) {
            this.oid = oid;
        }
        static AuthProtocol find(String s) throws FException { 
            if (s == null) {
                return NONE;
            }
            s = s.trim();
            if (s.length() == 0) {
                return NONE;
            }
            for (AuthProtocol p : AuthProtocol.values()) {
                if (p.name().equals(s)) {
                    return p;
                }
            }
            throw new FException("Not valid '%s' authentication protocol", s);
        }
    } // enum AuthProtocol
    
    private enum PrivProtocol {
        NONE(null),
        DES(PrivDES.ID),
        AES128(PrivAES128.ID),
        AES192(PrivAES192.ID),
        AES256(PrivAES256.ID);
        private OID oid;
        private PrivProtocol(OID oid) {
            this.oid = oid;
        }
        static PrivProtocol find(String s) throws FException {
            if (s == null) {
                return NONE;
            }
            s = s.trim();
            if (s.length() == 0) {
                return NONE;
            }
            for (PrivProtocol p : PrivProtocol.values()) {
                if (p.name().equals(s)) {
                    return p;
                }
            }
            throw new FException("Not valid '%s' privileges protocol", s);
        }
    } // enum PrivProtocol
    
    /** Encryption salt */
    private final static byte[] SALT = { -48, 24, -53, 19, -72, 2, -76, -78 }; // 8
    
    /** Authentication password */
    private OctetString octPasswordAuth;
    
    /** Privileges password */
    private OctetString octPasswordPriv;
    
    /** Authentication protocol */
    private AuthProtocol protocolAuth;
    
    /** Privileges protocol */
    private PrivProtocol protocolPriv;
    
    /**
     * Constructor
     * 
     * @param targetName target (user / security name)
     */
    public TargetV3(String targetName) {
        super(targetName);
        setSNMP(SecurityModel.SECURITY_MODEL_USM, 
                MessageProcessingModel.MPv3,
                SecurityLevel.AUTH_PRIV);
    } // TargetV3()

    /**
     * {@inheritDoc}
     * 
     * @throws FException exception if decryption fails
     * 
     * @see org.friendlysnmp.target.TargetBase#init(org.friendlysnmp.AgentWorker)
     */
    @Override
    public void init(AgentWorker agent) throws FException {
        super.init(agent);
        FConfig config = agent.getConfig();
        String sey = config.getConfigString(FConstant.KEY_V3_PASSWORD_KEY);
        octPasswordAuth = decrypt(sey, config.getConfigString(FConstant.KEY_V3_PASSWORD_AUTH));
        octPasswordPriv = decrypt(sey, config.getConfigString(FConstant.KEY_V3_PASSWORD_PRIV));
        
        protocolAuth = AuthProtocol.find(config.getConfigString(FConstant.KEY_V3_PROTOCOL_AUTH));
        protocolPriv = PrivProtocol.find(config.getConfigString(FConstant.KEY_V3_PROTOCOL_PRIV));
    } // init()
    
    /**
     * Decrypts AUTH and PRIV password
     * 
     * @param key decryption key
     * @param text encrypted password
     * @return decrypted password
     * @throws FException if decryption fails
     */
    private static OctetString decrypt(String key, String text) throws FException {
        try {
            byte[] a_by = new sun.misc.BASE64Decoder().decodeBuffer(text);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(SALT, 10);
            PBEKeySpec pbeKeySpec = new PBEKeySpec(key.toCharArray());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);
            byte[] bytes = cipher.doFinal(a_by);
            return new OctetString(new String(bytes));
        } catch (Exception e) {
            // NoSuchAlgorithmException, NoSuchPaddingException, 
            // InvalidKeyException, IOException, 
            // IllegalBlockSizeException, BadPaddingException        
            throw new FException("Failure to decrypt password", e);
        }
    } // decrypt()
    
    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#addViews(org.snmp4j.agent.mo.snmp.VacmMIB)
     */
    @Override
    public void addViews(VacmMIB vacm) {
        // Table vacmSecurityToGroupEntry in SNMP-VIEW-BASED-ACM-MIB:
        vacm.addGroup(
                getSecurityModel(),
                TypeName.SECURITY.createName(this),
                TypeName.GROUP.createName(this),
                storageVacmGroup.storageType);
        // Table vacmAccessEntry in SNMP-VIEW-BASED-ACM-MIB:
        vacm.addAccess(
                TypeName.GROUP.createName(this),
                TypeName.CONTEXT.createName(this), // context prefix
                getSecurityModel(),
                getSecurityLevel(),
                MutableVACM.VACM_MATCH_EXACT,
                TypeName.READ_VIEW.createName(this),
                TypeName.WRITE_VIEW.createName(this),
                TypeName.NOTIFY_VIEW.createName(this),
                storageVacmAccess.storageType);
        // Table vacmViewTreeFamilyEntry in SNMP-VIEW-BASED-ACM-MIB:
        vacm.addViewTreeFamily(
                TypeName.READ_VIEW.createName(this),
                new OID(FConstant.DEFAULT_OID_ROOT),
                new OctetString(), 
                VacmMIB.vacmViewIncluded,
                storageVacmViewTree.storageType);
        vacm.addViewTreeFamily(
                TypeName.WRITE_VIEW.createName(this),
                new OID(FConstant.DEFAULT_OID_ROOT),
                new OctetString(), 
                VacmMIB.vacmViewIncluded,
                storageVacmViewTree.storageType);
        vacm.addViewTreeFamily(
                TypeName.NOTIFY_VIEW.createName(this),
                new OID(FConstant.DEFAULT_OID_ROOT),
                new OctetString(), 
                VacmMIB.vacmViewIncluded,
                storageVacmViewTree.storageType);
    } // addViews()

    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#addUsmUser(org.snmp4j.security.USM)
     */
    @Override
    public void addUsmUser(USM usm) {
        // Table usmUserEntry in SNMP-USER-BASED-SM-MIB:
        // Not localized user
        UsmUser user = new UsmUser(
                TypeName.SECURITY.createName(this),
                // Authentication protocol: null, MD5, SHA
                protocolAuth == null ? null : protocolAuth.oid,
                octPasswordAuth,
                // Privacy protocol: null, DES, AES128, AES192(6?), AES256 
                protocolPriv == null ? null : protocolPriv.oid,
                octPasswordPriv);
        usm.addUser(user.getSecurityName(), null, user);
    } // addUsmUser()

    /**
     * {@inheritDoc}
     * 
     * @see org.friendlysnmp.target.TargetBase#addNotificationTargets(org.snmp4j.agent.mo.snmp.SnmpTargetMIB, org.snmp4j.agent.mo.snmp.SnmpNotificationMIB)
     */
    @Override
    public void addNotificationTargets(
            SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
        addTargetAddress(targetMIB);
        addTargetParams(targetMIB);
        addNotificationEntry(notificationMIB, NotificationType.INFORM);
        //addNotifyEntry(notificationMIB, NotifyType.TRAP);
    } // addNotificationTargets()

} // class TargetV3
