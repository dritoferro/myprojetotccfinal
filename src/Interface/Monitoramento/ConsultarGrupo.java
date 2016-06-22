/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface.Monitoramento;

import Controlador.Validacao;
import Interface.MenuPrincipal;

/**
 *
 * @author Adriano
 */
public class ConsultarGrupo extends javax.swing.JFrame {
    
    Validacao v = new Validacao();
    int op = 1;
    /**
     * Creates new form ConsultarGrupo
     */
    public ConsultarGrupo() {
        initComponents();
        txtNomeCod.requestFocus();
        getRootPane().setDefaultButton(btnPesquisar);
        pnInfo.setVisible(false);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cbNomeCod = new javax.swing.JComboBox();
        txtNomeCod = new javax.swing.JTextField();
        btnPesquisar = new javax.swing.JButton();
        pnInfo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblNomeLab = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblNumSala = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblNumMicros = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblNumMicrosConect = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblNumSis = new javax.swing.JLabel();
        btnLab = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        btnVoltar = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Consultar Grupo Monitoramento");
        setName("Consultar Grupo Monitoramento"); // NOI18N
        setResizable(false);

        cbNomeCod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nome LAB", "Número Sala", "Número LAB" }));
        cbNomeCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNomeCodActionPerformed(evt);
            }
        });

        txtNomeCod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNomeCodKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomeCodKeyTyped(evt);
            }
        });

        btnPesquisar.setText("Pesquisar");
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        jLabel2.setText("Nome LAB:");

        lblNomeLab.setText("NomeLab");

        jLabel4.setText("Número Sala:");

        lblNumSala.setText("NumSala");

        jLabel3.setText("Número de Micros:");

        lblNumMicros.setText("NumMicros");

        jLabel5.setText("Número de Micros Conectados:");

        lblNumMicrosConect.setText("NumMicrosConect");

        jLabel6.setText("Número Sistema:");

        lblNumSis.setText("NumSis");

        btnLab.setText("Exibir LAB");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Interface/Monitoramento/network.png"))); // NOI18N

        javax.swing.GroupLayout pnInfoLayout = new javax.swing.GroupLayout(pnInfo);
        pnInfo.setLayout(pnInfoLayout);
        pnInfoLayout.setHorizontalGroup(
            pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnInfoLayout.createSequentialGroup()
                        .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNomeLab)
                            .addComponent(lblNumSala)
                            .addComponent(lblNumMicros)
                            .addComponent(lblNumMicrosConect)
                            .addComponent(lblNumSis))
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnInfoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLab)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(jLabel7))
        );
        pnInfoLayout.setVerticalGroup(
            pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnInfoLayout.createSequentialGroup()
                        .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblNomeLab))
                        .addGap(18, 18, 18)
                        .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lblNumSala))
                        .addGap(18, 18, 18)
                        .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lblNumMicros))
                        .addGap(18, 18, 18)
                        .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lblNumMicrosConect)))
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(pnInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblNumSis))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLab)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnVoltar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbNomeCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtNomeCod, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnPesquisar))
                            .addComponent(pnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbNomeCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeCod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar))
                .addGap(18, 18, 18)
                .addComponent(pnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(btnVoltar)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        MenuPrincipal menu = new MenuPrincipal();
        menu.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void txtNomeCodKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeCodKeyTyped
        if(op == 1){
            v.validaCaracter(evt);
        }
        if(op == 2){
            v.validaNumero(evt);
        }
    }//GEN-LAST:event_txtNomeCodKeyTyped

    private void cbNomeCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNomeCodActionPerformed
        String pressed;
        pressed = cbNomeCod.getSelectedItem().toString();
        if(pressed.equals("NOME LAB")){
            op = 1;
            txtNomeCod.setText("");
            txtNomeCod.requestFocus();
        }
        if((pressed.equals("Número LAB")) || (pressed.equals("Número Sala"))){
            op = 2;
            txtNomeCod.setText("");
            txtNomeCod.requestFocus();
        }
    }//GEN-LAST:event_cbNomeCodActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        pnInfo.setVisible(true);
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void txtNomeCodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeCodKeyPressed
        v.validaControl(evt);
    }//GEN-LAST:event_txtNomeCodKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConsultarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultarGrupo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultarGrupo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLab;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JComboBox cbNomeCod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblNomeLab;
    private javax.swing.JLabel lblNumMicros;
    private javax.swing.JLabel lblNumMicrosConect;
    private javax.swing.JLabel lblNumSala;
    private javax.swing.JLabel lblNumSis;
    private javax.swing.JPanel pnInfo;
    private javax.swing.JTextField txtNomeCod;
    // End of variables declaration//GEN-END:variables
}
