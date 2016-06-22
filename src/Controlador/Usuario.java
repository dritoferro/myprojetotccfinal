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
public class Usuario {
    private int codCadColab;
    private String nomeColab;
    private int codColab;
    private String funcColab;
    private String loginColab;
    private String senhaColab;
    private boolean ativo;
    private char nivelAcesso;

    public int getCodCadColab() {
        return codCadColab;
    }

    public void setCodCadColab(int codCadColab) {
        this.codCadColab = codCadColab;
    }

    public String getNomeColab() {
        return nomeColab;
    }

    public void setNomeColab(String nomeColab) {
        this.nomeColab = nomeColab;
    }

    public int getCodColab() {
        return codColab;
    }

    public void setCodColab(int codColab) {
        this.codColab = codColab;
    }

    public String getFuncColab() {
        return funcColab;
    }

    public void setFuncColab(String funcColab) {
        this.funcColab = funcColab;
    }

    public String getLoginColab() {
        return loginColab;
    }

    public void setLoginColab(String loginColab) {
        this.loginColab = loginColab;
    }

    public String getSenhaColab() {
        return senhaColab;
    }

    public void setSenhaColab(String senhaColab) {
        this.senhaColab = senhaColab;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public char getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(char nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
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
    
    public void consultar(){
    
    }
    
}
