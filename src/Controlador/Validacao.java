/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

/**
 *
 * @author Adriano
 */
public class Validacao {

    public void validaLetra(java.awt.event.KeyEvent evt) {

        String numeros = "abcdefghijklmnopqrstuvwyxzABCDEFGHIJKLMNOPQRSTUVWYXZáéíóúÁÉÍÓÚâêîôûÂÊÎÔÛ ";
        if (!numeros.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }

    }

    public void validaNumero(java.awt.event.KeyEvent evt) {

        String numeros = "1234567890";
        if (!numeros.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }

    }

    public void validaCaracter(java.awt.event.KeyEvent evt) {

        String numeros = "/!@#$%¨&*()_+-*,.:;{}[]|'ªº?\"=¹²³£¢¬§\\><`";
        if (numeros.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }

    }

    public void validaControl(java.awt.event.KeyEvent evt) {
        int pressed = evt.getKeyCode();
        if ((pressed == 17) || (pressed == 67) || (pressed == 86))  {
            evt.consume();
        }
    }
}
