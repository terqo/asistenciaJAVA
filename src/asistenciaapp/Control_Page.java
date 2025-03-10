package asistenciaapp;

import asistenciaapp.conexion.conexionDB;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import net.glxn.qrgen.*;
import net.glxn.qrgen.image.ImageType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author oscar arroyo 13/03/2023 iztapalapa para el mundo
 * terqo company
 */
public class Control_Page extends javax.swing.JFrame {

    PreparedStatement ps;
    
    private void limpiarCajas(){
        txtApellido.setText(null);
        txtNombre.setText(null);
        txtArea.setText(null);
        txtCargo.setText(null);
        sexoCombo.setSelectedIndex(0);
    }
    //icono form
    public void iconImage(){
        ImageIcon icono = new ImageIcon("src/asistenciaapp/img/inTime2.png");
        this.setIconImage(icono.getImage());
    }
    public Control_Page() {
        initComponents();   
        tablaActualizarFalse();
        mostrarDatosSeleccionados();
        iconImage();
    }
    
    private void tablaActualizarFalse(){
        label2.setVisible(false);
        label3.setVisible(false);
        txtID.setVisible(false);
        txtLabel.setVisible(false);
        jLabel2.setVisible(false);
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        txtNombre.setVisible(false);
        txtApellido.setVisible(false);
        sexoCombo.setVisible(false);
        txtCargo.setVisible(false);
        txtArea.setVisible(false);
        btnRegister.setVisible(false);
        btnDelete.setVisible(false);
        tableDatos.setVisible(false);
        btnEliminar.setVisible(false);
        btnLimpiar.setVisible(false);
        btnDescargar.setVisible(false);

    }
    private void tablaActualizarTrue(){
        label2.setVisible(true);
        label3.setVisible(true);
        txtID.setVisible(true);
        txtLabel.setVisible(true);
        jLabel2.setVisible(true);
        jLabel4.setVisible(true);
        jLabel5.setVisible(true);
        jLabel6.setVisible(true);
        txtNombre.setVisible(true);
        txtApellido.setVisible(true);
        sexoCombo.setVisible(true);
        txtCargo.setVisible(true);
        txtArea.setVisible(true);
        btnRegister.setVisible(true);
        btnDelete.setVisible(true);
        btnEliminar.setVisible(true);
        tableDatos.setVisible(true);
        btnDescargar.setVisible(true);
        btnLimpiar.setVisible(true);
    }
    public void mostrarDatosTabla(String tabla){
        String sql = "select * from "+ tabla;
        Statement st;
        conexionDB con = new conexionDB();
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
     public void mostrarDatosTablaRegistros(String tabla){
        String sql = "select * from "+ tabla;
        Statement st;
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        System.out.println(sql);    
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Apellido");
        model.addColumn("Fecha/Hora");
        tableDatos.setModel(model);
        
        
        String [] datos = new String [4];
        try{
            st = dbAG.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                datos[0]= rs.getString(1);
                datos[1]= rs.getString(2);
                datos[2]= rs.getString(3);

                model.addRow(datos);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR" + e.toString());
        }
    }
    public void modificarDatos(){
        try{
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        ps= dbAG.prepareStatement("UPDATE empleado SET nombre=?,apellido=?,sexo=?,cargo=?,area=? WHERE id=?");
        
        ps.setString(1, txtNombre.getText().toUpperCase());
        ps.setString(2, txtApellido.getText().toUpperCase());
        ps.setString(3, sexoCombo.getSelectedItem().toString());
        ps.setString(4, txtCargo.getText().toUpperCase());
        ps.setString(5, txtArea.getText().toUpperCase());
        ps.setString(6, txtID.getText().toUpperCase());

        
        int res = ps.executeUpdate();
        if(res >0){
            JOptionPane.showMessageDialog(null,"Empleado Modificado!");
          
            limpiarCajas();
            mostrarDatosTabla("empleado");
        } else{
            JOptionPane.showMessageDialog(null, "Error al modificar el empleado ");
            limpiarCajas();
        }
        dbAG.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(null, "Error al guardar el empleado. Error: " + e);
        System.out.println(e);
    }
    }
    public void eliminarDatos(){
        try{
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        ps= dbAG.prepareStatement("DELETE FROM empleado WHERE id=?");
        
        ps.setString(1, txtID.getText());
        
        int res = ps.executeUpdate();
        if(res >0){
            JOptionPane.showMessageDialog(null,"Empleado Eliminado!");
          
            limpiarCajas();
            mostrarDatosTabla("empleado");
        } else{
            JOptionPane.showMessageDialog(null, "Error al eliminar el empleado ");
            limpiarCajas();
        }
        dbAG.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(null, "Error al guardar el empleado. Error: " + e);
        System.out.println(e);
    }
    }
    public void actualizarDatosTabla(){
    try{
        conexionDB con = new conexionDB();
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
    public void exportarExcel(JTable t) throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de excel", "xls");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Guardar archivo");
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().toString().concat(".xls");
            try {
                File archivoXLS = new File(ruta);
                if (archivoXLS.exists()) {
                    archivoXLS.delete();
                }
                archivoXLS.createNewFile();
                Workbook libro = new HSSFWorkbook();
                FileOutputStream archivo = new FileOutputStream(archivoXLS);
                Sheet hoja = libro.createSheet("Mi hoja de trabajo 1");
                hoja.setDisplayGridlines(false);
                for (int f = 0; f < t.getRowCount(); f++) {
                    Row fila = hoja.createRow(f);
                    for (int c = 0; c < t.getColumnCount(); c++) {
                        Cell celda = fila.createCell(c);
                        if (f == 0) {
                            celda.setCellValue(t.getColumnName(c));
                        }
                    }
                }
                int filaInicio = 1;
                for (int f = 0; f < t.getRowCount(); f++) {
                    Row fila = hoja.createRow(filaInicio);
                    filaInicio++;
                    for (int c = 0; c < t.getColumnCount(); c++) {
                        Cell celda = fila.createCell(c);
                        if (t.getValueAt(f, c) instanceof Double) {
                            celda.setCellValue(Double.parseDouble(t.getValueAt(f, c).toString()));
                        } else if (t.getValueAt(f, c) instanceof Float) {
                            celda.setCellValue(Float.parseFloat((String) t.getValueAt(f, c)));
                        } else {
                            celda.setCellValue(String.valueOf(t.getValueAt(f, c)));
                        }
                    }
                }
                libro.write(archivo);
                archivo.close();
                Desktop.getDesktop().open(archivoXLS);
            } catch (IOException | NumberFormatException e) {
                throw e;
            }
        }
    }
    public  void GenerarCodigoQR(){
	ByteArrayOutputStream out=QRCode.from(this.txtID.getText()).to(ImageType.PNG).stream();
	ImageIcon imageIcon=new ImageIcon(out.toByteArray());
	this.lblcodigoQR.setIcon(imageIcon);
        }
    public void mostrarDatosSeleccionados(){
        tableDatos.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = tableDatos.getSelectedRow();
                if (selectedRow != -1) { // Si se seleccionó una fila
                    // Obtener los datos de la fila seleccionada
                    
                    
                    Object id = tableDatos.getValueAt(selectedRow, 0);
                    Object nombre = tableDatos.getValueAt(selectedRow, 1);
                    Object apellido = tableDatos.getValueAt(selectedRow, 2);
                    Object gen = tableDatos.getValueAt(selectedRow, 3);
                    Object cargo = tableDatos.getValueAt(selectedRow, 4);
                    Object area = tableDatos.getValueAt(selectedRow, 5);
                    
                    
                    // Mostrar los datos en los JTextFields correspondientes
                    txtID.setText(id.toString());
                    txtNombre.setText(nombre.toString());
                    txtApellido.setText(apellido.toString());
                    sexoCombo.setSelectedItem(gen.toString());
                    txtCargo.setText(cargo.toString());
                    txtArea.setText(area.toString());
                    btnDescargar.setEnabled(true);
                    GenerarCodigoQR();
                }
            }
        });
    }
    private void descargarPNG(){
    
        Icon icon = lblcodigoQR.getIcon();
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        
        icon.paintIcon(null, image.getGraphics(), 0, 0);
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("imagen PNG", "png");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Guardar archivo");
         chooser.setAcceptAllFileFilterUsed(false);
         if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().toString().concat(".png");
        try {
        File file = new File(ruta);
        file.createNewFile();
        ImageIO.write(image, "png", file);
        JOptionPane.showMessageDialog(null, "imagen guardada" );
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "error en el guardado de imagen" + e);
    }
  }
}
   
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDatos = new javax.swing.JTable();
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
        txtLabel = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        label3 = new javax.swing.JLabel();
        lblcodigoQR = new javax.swing.JLabel();
        btnDescargar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuAsistencia = new javax.swing.JMenu();
        menuReportAsisten = new javax.swing.JMenuItem();
        MenuUser = new javax.swing.JMenu();
        menuNewUser = new javax.swing.JMenuItem();
        menuUpdateUser = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Control Asistencia");
        setBackground(new java.awt.Color(51, 0, 102));
        setResizable(false);

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

        btnRegister.setText("Modificar");
        btnRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnDelete.setText("Cancelar");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Apellidos:");

        label2.setText("Nombres:");

        jLabel4.setText("Sexo:");

        sexoCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno", "H", "M" }));

        jLabel5.setText("Cargo:");

        jLabel6.setText("Area:");

        txtLabel.setText("Codigo QR");

        txtID.setEnabled(false);

        label3.setText("ID:");

        btnDescargar.setText("Descargar");
        btnDescargar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDescargar.setEnabled(false);
        btnDescargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDescargarActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminar.setText("Eliminar");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Control Manager");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(26, 26, 26))
        );

        jMenuBar1.setForeground(new java.awt.Color(255, 255, 255));

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

        jMenu1.setText("Sesión");

        jMenuItem1.setText("Cerrar Sesión");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(label2)
                                        .addComponent(label3))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sexoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel6))
                                    .addGap(35, 35, 35)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRegister)
                        .addGap(27, 27, 27)
                        .addComponent(btnEliminar)
                        .addGap(67, 67, 67)
                        .addComponent(btnDelete)
                        .addGap(58, 58, 58)
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDescargar)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLabel)
                            .addComponent(lblcodigoQR, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(76, 76, 76))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                            .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDelete)
                            .addComponent(btnRegister)
                            .addComponent(btnDescargar)
                            .addComponent(btnEliminar)
                            .addComponent(btnLimpiar))
                        .addGap(51, 51, 51))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblcodigoQR, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuReportAsistenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReportAsistenActionPerformed
        tableDatos.setVisible(true);
        mostrarDatosTablaRegistros("registro");
       
        try {
            exportarExcel(tableDatos);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }

        
    }//GEN-LAST:event_menuReportAsistenActionPerformed

    private void menuUpdateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUpdateUserActionPerformed
        tablaActualizarTrue();
        mostrarDatosTabla("empleado");
        
    }//GEN-LAST:event_menuUpdateUserActionPerformed

    private void menuNewUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewUserActionPerformed
        this.setVisible(false);
        new Registro_Page().setVisible(true);
    }//GEN-LAST:event_menuNewUserActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
       modificarDatos();
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        this.setVisible(false);
        new Control_Page().setVisible(true);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnDescargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescargarActionPerformed
        descargarPNG();
    }//GEN-LAST:event_btnDescargarActionPerformed
    int opc;
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        int mesj;
		if (opc == 0) {
			mesj = JOptionPane.showConfirmDialog(null, "Esta Seguro Que Desea Eliminar Este Registro ", "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (mesj == JOptionPane.YES_OPTION) {
				eliminarDatos();
				JOptionPane.showMessageDialog(this, "Registro Eliminado Exitosamente");
				mostrarDatosTabla("empleado");
			} else {
				JOptionPane.showMessageDialog(this, "Registro No Eliminado", "Sistema", JOptionPane.ERROR_MESSAGE);
			}
		}     
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCajas();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        int mesj;
		if (opc == 0) {
			mesj = JOptionPane.showConfirmDialog(null, "Esta Seguro Que Desea Cerrar Sesión ", "Cerrar Sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (mesj == JOptionPane.YES_OPTION) {        
                                this.setVisible(false);
                                new Home_Page().setVisible(true);
				JOptionPane.showMessageDialog(this, "Sesion Cerrada Exitosamente","Hasta Luego",JOptionPane.INFORMATION_MESSAGE);
				
			} else {
				JOptionPane.showMessageDialog(this, "Sesión NO Cerrada", "Sistema", JOptionPane.ERROR_MESSAGE);
			}
		} 
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
            java.util.logging.Logger.getLogger(Control_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Control_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Control_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Control_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Control_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MenuUser;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDescargar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegister;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel lblcodigoQR;
    private javax.swing.JMenu menuAsistencia;
    private javax.swing.JMenuItem menuNewUser;
    private javax.swing.JMenuItem menuReportAsisten;
    private javax.swing.JMenuItem menuUpdateUser;
    private javax.swing.JComboBox<String> sexoCombo;
    private javax.swing.JTable tableDatos;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JTextField txtID;
    private javax.swing.JLabel txtLabel;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
