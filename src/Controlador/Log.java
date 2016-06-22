/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.util.Date;

/**
 *
 * @author Adriano
 */
public class Log {
    private int codLog;
    private Date dataHora;
    private String funcao;
    private String loginUser;

    public int getCodLog() {
        return codLog;
    }

    public void setCodLog(int codLog) {
        this.codLog = codLog;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }
    
    public Log incluir(){
    //Deve retornar um tipo LOG pois aqui não será impresso nada, será na Interface.
        Log a = new Log();
    
    return a;
    }
    
    public void consultar(){
    
    }
}
