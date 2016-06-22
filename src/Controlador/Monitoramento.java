/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Interface.MonitoramentoLab;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 *
 * @author Adriano
 */
public class Monitoramento {
    private int codGroup;
    private String nomeGroup;
    private int labGroup;

    public int getCodGroup() {
        return codGroup;
    }

    public void setCodGroup(int codGroup) {
        this.codGroup = codGroup;
    }

    public String getNomeGroup() {
        return nomeGroup;
    }

    public void setNomeGroup(String nomeGroup) {
        this.nomeGroup = nomeGroup;
    }

    public int getLabGroup() {
        return labGroup;
    }

    public void setLabGroup(int labGroup) {
        this.labGroup = labGroup;
    }
    
    //Criar uma instância com singleton para criar um objeto do tipo LOG.
    
    
    
    public int incluir(){
    
        //gerarLog();
        return 0;
    }
    
    public int alterar(){
    
        //gerarLog();
        return 0;
    }
    
    public int excluir(){
    
        //gerarLog();
        return 0;
    }
    
    public void consultar(){
        
    }
    
    
    
    
}
