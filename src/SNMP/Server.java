/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SNMP;

import java.util.Properties;
import org.friendlysnmp.FriendlyAgent;

/**
 *
 * @author Adriano
 */
public class Server {

    public void novoServer() {
        try {
            Properties prop = new Properties();
            prop.put("snmp.address.ge-set", "127.0.0.1/161");
            prop.put("snmp.address.send-notification", "127.0.0.1/162");
            prop.put("snmp.v2.community", "public");

            FriendlyAgent agent1 = new FriendlyAgent("SGLAB", "V 1.0", prop);
            agent1.init();
            agent1.start();
            
            
        } catch (Exception e) {
        }

    }
}
