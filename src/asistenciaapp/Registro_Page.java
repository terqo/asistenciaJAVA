package asistenciaapp;

import static asistenciaapp.Huella_Page.TEMPLATE_PROPERTY;
import asistenciaapp.conexion.conexionDB;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import static com.digitalpersona.onetouch.processing.DPFPTemplateStatus.TEMPLATE_STATUS_FAILED;
import static com.digitalpersona.onetouch.processing.DPFPTemplateStatus.TEMPLATE_STATUS_READY;
import com.digitalpersona.onetouch.readers.DPFPReaderDescription;
import com.digitalpersona.onetouch.readers.DPFPReadersCollection;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 *
 * @author oscar arroyo 13/03/2023 iztapalapa para el mundo
 * terqo company
 */
public class Registro_Page extends javax.swing.JFrame {
   
    public PreparedStatement ps;
    
    //Variables de Digital Persona 4500
    private DPFPCapture lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    
    public DPFPFeatureSet featuresIncripcion;
    public DPFPFeatureSet featuresVerificacion;
    
    //icono form
    public void iconImage(){
        ImageIcon icono = new ImageIcon("src/asistenciaapp/img/inTime2.png");
        this.setIconImage(icono.getImage());
    }
    private void limpiarCajas(){
        txtApellido.setText(null);
        txtNombre.setText(null);
        txtArea.setText(null);
        txtCargo.setText(null);
        txtID.setText(null);
        sexoCombo.setSelectedIndex(0);
    }
    
    public Registro_Page() {
        initComponents();
        iconImage();
        listReaders();
        Iniciar();
	start();
        EstadoHuellas();
    }
    
        
    public void registrarDatos(){
    try{
        //Obtiene los datos del template de la huella actual
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize());
        Integer tamañoHuella=template.serialize().length;
        
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        ps= dbAG.prepareStatement("INSERT INTO empleado (id,nombre,apellido,sexo,cargo,area,huella) VALUES (?,?,?,?,?,?,?)");
        ps.setString(1, txtID.getText().toUpperCase());
        ps.setString(2, txtNombre.getText().toUpperCase());
        ps.setString(3, txtApellido.getText().toUpperCase());
        ps.setString(4, sexoCombo.getSelectedItem().toString());
        ps.setString(5, txtCargo.getText().toUpperCase());
        ps.setString(6, txtArea.getText().toUpperCase());
        ps.setBinaryStream(7, datosHuella,tamañoHuella);
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
        JOptionPane.showMessageDialog(null, "Error al guardar el empleado. Error: " + e);
        System.out.println(e);
    }
}
    public void  listReaders(){
        DPFPReadersCollection readers = DPFPGlobal.getReadersFactory().getReaders();
        if (readers == null || readers.size() == 0){
            System.out.println("no se encontraron lectores");
            huellaDevice.setText("NO SE ENCONTRARON LECTORES");
            huellaDevice.setForeground(Color.red);
            return;
        }
        System.out.println("Lectores");
        for (DPFPReaderDescription readerDescription : readers){
            System.out.println(readerDescription.getSerialNumber());
            huellaDevice.setText("Lector: "+ readerDescription.getSerialNumber());
        }
        
    }
    
    //Varible que permite iniciar el dispositivo de lector de huella conectado
    // con sus distintos metodos.
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();

    //Varible que permite establecer las capturas de la huellas, para determina sus caracteristicas
    // y poder estimar la creacion de un template de la huella para luego poder guardarla
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    //Esta variable tambien captura una huella del lector y crea sus caracteristcas para auntetificarla
    // o verificarla con alguna guardada en la BD
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();

    //Variable que para crear el template de la huella luego de que se hallan creado las caracteriticas
    // necesarias de la huella si no ha ocurrido ningun problema

    protected void Iniciar(){
       Lector.addDataListener(new DPFPDataAdapter() {
        @Override public void dataAcquired(final DPFPDataEvent e) {
        SwingUtilities.invokeLater(new Runnable() {	public void run() {
        //EnviarTexto("La Huella Digital ha sido Capturada");
        ProcesarCaptura(e.getSample());
        }});}
       });

       Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
        @Override public void readerConnected(final DPFPReaderStatusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {	public void run() {
        EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
        }});}
        @Override public void readerDisconnected(final DPFPReaderStatusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {	public void run() {
        EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
        }});}
       });

       Lector.addSensorListener(new DPFPSensorAdapter() {
        @Override public void fingerTouched(final DPFPSensorEvent e) {
        SwingUtilities.invokeLater(new Runnable() {	public void run() {
        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
        EnviarTexto("Realizando lectura dactilar...");
        }});}
        @Override public void fingerGone(final DPFPSensorEvent e) {
        SwingUtilities.invokeLater(new Runnable() {	public void run() {
        //EnviarTexto("El dedo ha sido quitado del Lector de Huella");
        }});}
       });

       Lector.addErrorListener(new DPFPErrorAdapter(){
        public void errorReader(final DPFPErrorEvent e){
        SwingUtilities.invokeLater(new Runnable() {  public void run() {
        EnviarTexto("Error: "+e.getError());
        }});}
       });
    }

    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;

    public  void ProcesarCaptura(DPFPSample sample)
    {
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null)
            try{
            System.out.println("Las Caracteristicas de la Huella han sido creada");
            Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear

            // Dibuja la huella dactilar capturada.
            Image image=CrearImagenHuella(sample);
            DibujarHuella(image);

            try {
                identificarHuella();
            } catch (IOException ex) {
                Logger.getLogger(Registro_Page.class.getName()).log(Level.SEVERE, null, ex);
            }


            }catch (DPFPImageQualityException ex) {
            System.err.println("Error: "+ex.getMessage());
            }

            finally {
            EstadoHuellas();
            // Comprueba si la plantilla se ha creado.
               switch(Reclutador.getTemplateStatus())
               {
                   case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                   stop();
                   setTemplate(Reclutador.getTemplate());
                   EnviarTexto("La Plantilla de la Huella ha Sido Creada, ya puede Guardarla");
                   btnRegister.setEnabled(true);
                   btnRegister.grabFocus();
                   break;

                   case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                   Reclutador.clear();
                   stop();
                   EstadoHuellas();
                   setTemplate(null);
                   JOptionPane.showMessageDialog(Registro_Page.this, "La Plantilla de la Huella no pudo ser creada, Repita el Proceso", "Inscripcion de Huellas Dactilares", JOptionPane.ERROR_MESSAGE);
                   start();
                   break;
               }
                    }
    }

     public  DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose){
         DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
         try {
          return extractor.createFeatureSet(sample, purpose);
         } catch (DPFPImageQualityException e) {
          return null;
         }
    }

    public  Image CrearImagenHuella(DPFPSample sample) {
            return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    public void DibujarHuella(Image image) {
            lblHuella.setIcon(new ImageIcon(
            image.getScaledInstance(lblHuella.getWidth(), lblHuella.getHeight(), Image.SCALE_DEFAULT)));
            repaint();
    }

    public  void EstadoHuellas(){
            EnviarTexto("Plantillas necesarias para guardar una nueva huella : "+ Reclutador.getFeaturesNeeded());
    }

    public void EnviarTexto(String string) {
            textArea.append(string + "\n");
    }

    public  void start(){
            Lector.startCapture();
            EnviarTexto("Utilizando el Lector de Huella Dactilar ");
    }

    public  void stop(){
            Lector.stopCapture();
            //EnviarTexto("No se está usando el Lector de Huella Dactilar ");
    }

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
            DPFPTemplate old = this.template;
            this.template = template;
            firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

    conexionDB cn=new conexionDB();
    
    /**
      * Identifica a una persona registrada por medio de su huella digital
      */
      public void identificarHuella() throws IOException{
         try {
           //Establece los valores para la sentencia SQL
           Connection c= cn.Conectar();

           //Obtiene todas las huellas de la bd
           PreparedStatement identificarStmt = c.prepareStatement("SELECT nombre,huella FROM empleado");
           ResultSet rs = identificarStmt.executeQuery();

           //Si se encuentra el nombre en la base de datos
           while(rs.next()){
           //Lee la plantilla de la base de datos
           byte templateBuffer[] = rs.getBytes("huella");
           String nombre=rs.getString("nombre");
           //Crea una nueva plantilla a partir de la guardada en la base de datos
           DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
           //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
           setTemplate(referenceTemplate);

           // Compara las caracteriticas de la huella recientemente capturda con la
           // alguna plantilla guardada en la base de datos que coincide con ese tipo
           DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());

           //compara las plantilas (actual vs bd)
           //Si encuentra correspondencia dibuja el mapa
           //e indica el nombre de la persona que coincidió.
           if (result.isVerified()){
           //Envia el registro al servidor
           Reclutador.clear();
           stop();
           setTemplate(null);
           lblHuella.setIcon(null);
           start();
           return;
                                   }
           }
           //Si no encuentra alguna huella correspondiente al nombre lo indica con un mensaje
           EnviarTexto("Empleado No Existe");
           setTemplate(null);
           } catch (SQLException e) {
           //Si ocurre un error lo indica en la consola
           System.err.println("Error al identificar huella dactilar."+e.getMessage());
           }finally{
           cn.desconectar();
           }
    }
    
    int codi;
    String autcod;
    public void auto_cod() {
        try {
            conexionDB con = new conexionDB();
            Connection dbAG = con.Conectar();
            Statement stm = dbAG.createStatement();
            System.out.println("todo bien ");
            String sql = "SELECT ID FROM empleado WHERE ID<>'E000' ORDER BY id";
            ResultSet rs = con.stm.executeQuery(sql);
            System.out.println("todo bien");
            while (con.rs.next()) {
                autcod = con.rs.getString("ID");
                codi = 1 + Integer.parseInt(autcod.substring(1, 4));
                txtID.setText("E" + String.format("%03d", codi));
            }
        } catch (Exception e) {
             System.out.println(e);
        }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        sexoCombo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtCargo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtArea = new javax.swing.JTextField();
        btnRegister = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        huellaDevice = new javax.swing.JLabel();
        lblHuella = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Registro");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Registro usuario");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(584, 584, 584))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        sexoCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione uno", "H", "M" }));

        jLabel4.setText("Cargo:");

        jLabel6.setText("Area:");

        btnRegister.setText("Registrar");
        btnRegister.setEnabled(false);
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel2.setText("Apellidos:");

        jLabel1.setText("Nombres:");

        jLabel7.setText("ID:");

        jLabel3.setText("Sexo:");

        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        huellaDevice.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        huellaDevice.setText("Device");

        lblHuella.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asistenciaapp/img/lector_conectado.png"))); // NOI18N

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        jScrollPane2.setViewportView(textArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sexoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(72, 72, 72)
                                        .addComponent(btnRegister)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnCancelar))))
                            .addComponent(jLabel6)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblHuella)
                                    .addComponent(huellaDevice))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(huellaDevice))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(sexoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegister)
                            .addComponent(btnCancelar)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblHuella)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.setVisible(false);
        new Control_Page().setVisible(true);
        stop();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        registrarDatos();
        this.dispose();
        new Control_Page().setVisible(true);
        stop();
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed

    }//GEN-LAST:event_txtIDActionPerformed

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
            java.util.logging.Logger.getLogger(Registro_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registro_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registro_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registro_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registro_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel huellaDevice;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JComboBox<String> sexoCombo;
    private javax.swing.JTextArea textArea;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
