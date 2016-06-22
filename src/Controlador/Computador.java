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
public class Computador {
    private int codMaq;
    private String nomeMaq;
    private int statusMaq;
    private int codGroup;

    public int getCodMaq() {
        return codMaq;
    }

    public void setCodMaq(int codMaq) {
        this.codMaq = codMaq;
    }

    public String getNomeMaq() {
        return nomeMaq;
    }

    public void setNomeMaq(String nomeMaq) {
        this.nomeMaq = nomeMaq;
    }

    public int getStatusMaq() {
        return statusMaq;
    }

    public void setStatusMaq(int statusMaq) {
        this.statusMaq = statusMaq;
    }

    public int getCodGroup() {
        return codGroup;
    }

    public void setCodGroup(int codGroup) {
        this.codGroup = codGroup;
    }
    
    //Criar uma instância com singleton para criar um objeto do tipo LOG.
    
    public int incluir(){
        
        //gerarLog();
        return 0;
    }
    
    public void consultar(){
    
    }
    
    public int excluir(){
    
        //gerarLog();
        return 0;
    }
}
