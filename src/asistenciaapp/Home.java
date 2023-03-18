package asistenciaapp;

import asistenciaapp.conexion.dbAG;

import java.sql.*;
import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;
/**
 *
 * @author oscar
 */
public class Home extends javax.swing.JFrame {

    PreparedStatement ps;
    
    private void limpiarCajas(){
        txtApellido.setText(null);
        txtNombre.setText(null);
        txtArea.setText(null);
        txtCargo.setText(null);
        sexoCombo.setSelectedIndex(0);
    }
    
    public Home() {
        initComponents();   
        tablaActualizarFalse();
    }
    
    private void tablaActualizarFalse(){
        label1.setVisible(false);
        label2.setVisible(false);
        jLabel2.setVisible(false);
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        txtBuscador.setVisible(false);
        txtNombre.setVisible(false);
        txtApellido.setVisible(false);
        sexoCombo.setVisible(false);
        txtCargo.setVisible(false);
        txtArea.setVisible(false);
        btnBuscador.setVisible(false);
        btnRegister.setVisible(false);
        btnDelete.setVisible(false);
        tableDatos.setVisible(false);
    }
    private void tablaActualizarTrue(){
        label1.setVisible(true);
        label2.setVisible(true);
        jLabel2.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        txtBuscador.setVisible(true);
        txtNombre.setVisible(true);
        txtApellido.setVisible(true);
        sexoCombo.setVisible(true);
        txtCargo.setVisible(true);
        txtArea.setVisible(true);
        btnBuscador.setVisible(true);
        btnRegister.setVisible(true);
        btnDelete.setVisible(true);
        tableDatos.setVisible(true);
    }
    public void mostrarDatosTabla(String tabla){
        String sql = "select * from "+ tabla;
        Statement st;
        dbAG con = new dbAG();
        Connection dbAG = con.Conectar();
        System.out.println(sql);    
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Apellido");
        model.addColumn("Sexo");
        model.addColumn("Cargo");
        model.addColumn("Area");
        tableDatos.setModel(model);
        
        
        String [] datos = new String [6];
        try{
            st = dbAG.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                datos[0]= rs.getString(1);
                datos[1]= rs.getString(2);
                datos[2]= rs.getString(3);
                datos[3]= rs.getString(4);
                datos[4]= rs.getString(5);
                datos[5]= rs.getString(6);
                model.addRow(datos);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR" + e.toString());
        }
    }
    public void actualizarDatosTabla(){
    try{
        dbAG con = new dbAG();
        Connection dbAG = con.Conectar();

        ps= dbAG.prepareStatement("INSERT INTO empleado (nombre,apellido,sexo,cargo,area) VALUES (?,?,?,?,?)");
        ps.setString(1, txtNombre.getText());
        ps.setString(2, txtApellido.getText());
        ps.setString(3, sexoCombo.getSelectedItem().toString());
        ps.setString(4, txtCargo.getText());
        ps.setString(5, txtArea.getText());
        int res = ps.executeUpdate();
        if(res >0){
            JOptionPane.showMessageDialog(null,"Empleado Guardado!");
            limpiarCajas();
        } else{
            JOptionPane.showMessageDialog(null, "Error al guardar el empleado ");
            limpiarCajas();
        }
        dbAG.close();
    }catch(SQLException e){
        System.out.println(e);
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDatos = new javax.swing.JTable();
        btnBuscador = new javax.swing.JButton();
        label1 = new javax.swing.JLabel();
        txtBuscador = new javax.swing.JTextField();
        btnRegister = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        sexoCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtCargo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtArea = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuAsistencia = new javax.swing.JMenu();
        menuReportAsisten = new javax.swing.JMenuItem();
        MenuUser = new javax.swing.JMenu();
        menuNewUser = new javax.swing.JMenuItem();
        menuUpdateUser = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tableDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tableDatos);

        btnBuscador.setText("BUSCAR");

        label1.setText("BUSCAR A:");

        txtBuscador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscadorActionPerformed(evt);
            }
        });

        btnRegister.setText("Registrar");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnDelete.setText("Cancelar");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Apellidos:");

        label2.setText("Nombres:");

        jLabel4.setText("Sexo:");

        sexoCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno", "Hombre", "Mujer" }));

        jLabel5.setText("Cargo:");

        jLabel6.setText("Area:");

        menuAsistencia.setText("Control de Asistencia");

        menuReportAsisten.setText("Reporte de asistencias");
        menuReportAsisten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuReportAsistenActionPerformed(evt);
            }
        });
        menuAsistencia.add(menuReportAsisten);

        jMenuBar1.add(menuAsistencia);

        MenuUser.setText("Gestion de usuarios ");

        menuNewUser.setText("Nuevo usuario");
        menuNewUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewUserActionPerformed(evt);
            }
        });
        MenuUser.add(menuNewUser);

        menuUpdateUser.setText("Actualizar usuario");
        menuUpdateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuUpdateUserActionPerformed(evt);
            }
        });
        MenuUser.add(menuUpdateUser);

        jMenuBar1.add(MenuUser);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnRegister)
                                .addGap(34, 34, 34)
                                .addComponent(btnDelete)
                                .addGap(12, 12, 12))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(sexoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(label2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel6))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtCargo)
                                        .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label1)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(btnBuscador)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscador)
                    .addComponent(label1)
                    .addComponent(txtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label2)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(sexoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegister)
                            .addComponent(btnDelete)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(52, 52, 52))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuReportAsistenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReportAsistenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuReportAsistenActionPerformed

    private void menuUpdateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUpdateUserActionPerformed
        tablaActualizarTrue();
        mostrarDatosTabla("empleado");
        
    }//GEN-LAST:event_menuUpdateUserActionPerformed

    private void menuNewUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewUserActionPerformed
        this.setVisible(false);
        new Registro().setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_menuNewUserActionPerformed

    private void txtBuscadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscadorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscadorActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        this.setVisible(false);
        new Home().setVisible(true);
    }//GEN-LAST:event_btnDeleteActionPerformed

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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MenuUser;
    private javax.swing.JButton btnBuscador;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRegister;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JMenu menuAsistencia;
    private javax.swing.JMenuItem menuNewUser;
    private javax.swing.JMenuItem menuReportAsisten;
    private javax.swing.JMenuItem menuUpdateUser;
    private javax.swing.JComboBox<String> sexoCombo;
    private javax.swing.JTable tableDatos;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtBuscador;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
