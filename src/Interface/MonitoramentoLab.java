/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Controlador.Monitoramento;
import java.awt.GridLayout;

/**
 *
 * @author Adriano
 */
public class MonitoramentoLab extends javax.swing.JFrame {
    
    
    /**
     * Creates new form Monitoramento
     */
    public MonitoramentoLab() {
        initComponents();
        
        
        //Formato que precisa ficar para fazer uma boa visualização
        // Realizar um select no banco para saber quantos grupos possuem para que possa receber como variavel na linha abaixo para definir como será a grid
        pnGrupo.setLayout(new GridLayout(2, 3, 1, 1));
        //criar uma grid para cada lab, e para cada lab fazer select para verificar quantos computadores pertencem a aquele lab, para ser definido no grid
        // o numero de linhas não pode ser maior do que o numero de colunas senao ficara feio
        // o intervalo entre o numero de linhas pelo numero de colunas não pode ser maior que 2
        // criar uma classe calcula monitoramento tanto para a grid do lab quanto para a grid do grupo
        //para realizar o calculo de linhas e colunas deve retornar um obj da propria classe, deve conter 2 atrib. linha e coluna.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        pnGrupo = new javax.swing.JPanel();
        lblCod = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Monitoramento Principal");
        setName("Monitoramento Principal"); // NOI18N
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("Pressione ESC para voltar");

        lblCod.setText("jLabel1");

        javax.swing.GroupLayout pnGrupoLayout = new javax.swing.GroupLayout(pnGrupo);
        pnGrupo.setLayout(pnGrupoLayout);
        pnGrupoLayout.setHorizontalGroup(
            pnGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGrupoLayout.createSequentialGroup()
                .addGap(190, 190, 190)
                .addComponent(lblCod)
                .addContainerGap(805, Short.MAX_VALUE))
        );
        pnGrupoLayout.setVerticalGroup(
            pnGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnGrupoLayout.createSequentialGroup()
                .addContainerGap(412, Short.MAX_VALUE)
                .addComponent(lblCod)
                .addGap(130, 130, 130))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(829, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addContainerGap())
            .addComponent(pnGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(1037, 636));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        int pressed;
        pressed = evt.getKeyCode();
        if(pressed == 27)
        {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
            this.setVisible(false);
        }
        lblCod.setText(String.valueOf(pressed));
    }//GEN-LAST:event_formKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MonitoramentoLab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitoramentoLab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitoramentoLab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitoramentoLab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MonitoramentoLab().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblCod;
    public javax.swing.JPanel pnGrupo;
    // End of variables declaration//GEN-END:variables
}