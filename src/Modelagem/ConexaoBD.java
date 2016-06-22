/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelagem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Adriano
 */
public class ConexaoBD {

    public static Connection abrirConn() throws Exception {
        String url;
        Connection conn;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            url = "jdbc:jtds:sqlserver://localhost;DataBaseName=SGLab";
            conn = DriverManager.getConnection(url, "Adriano", "300695");
            return conn;
        } catch (SQLException e) {
            throw new Exception("Aconteceu o erro ao conectar ao banco: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Aconteceu o erro: " + e.getMessage());
        }
    }
}
