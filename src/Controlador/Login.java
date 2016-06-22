/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import javax.swing.ImageIcon;

/**
 *
 * @author Adriano
 */
public class Login {

    private String user;
    private static Login instance;

    private Login() {

    }

    public static Login getInstance() {
        if (instance == null) {
            instance = new Login();
        }
        return instance;
    }

    public void logar(String nome, String senha){
        if(nome.equals(nome)){
            if(senha.equals(senha)){
                Login logado = getInstance();
                logado.user = nome;
            }
        }
        
    }
}
