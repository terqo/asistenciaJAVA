package asistenciaapp;

import asistenciaapp.conexion.conexionDB;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * author oscar arroyo 13/03/2023 iztapalapa para el mundo
 * terqo company
 */
public class Qr_Page extends javax.swing.JFrame {
    
    private static String emailFrom = "";
    private static String passwordFrom = "";
    
    private String emailTo;
    private String subject;
    private String content;

    private Properties mProperties;
    private Session mSession;
    private MimeMessage mCorreo;
    
    private File[] mArchivosAdjuntos;
    private String nombres_archivos;
    
    public PreparedStatement ps;
    public ResultSet rs = null;
    public Statement stmt = null;
    public Connection con;
    public int contador = 0;
    
    private long lastReadTime = 0;
    private final long READ_TIMEOUT = 10000; // tiempo de espera de 10 segundos
    private WebcamPanel panel = null;
    private Webcam webcam = null; //Generate Webcam Object
    
    /**
     * Creates new form Asistencia
     */
    public Qr_Page() {
        initComponents();
        initTimer();
        initWebcam();
        mProperties = new Properties();
        mostrarDatosTablaRegistros("registro");
        iconImage();
    }
    //icono form
    public void iconImage(){
        ImageIcon icono = new ImageIcon("src/asistenciaapp/img/inTime2.png");
        this.setIconImage(icono.getImage());
    }
    private void initTimer() {
        //registro de la hora en tiempo real   
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date fecha = new Date();
                DateFormat formatoFecha = DateFormat.getDateTimeInstance();
                String horaFecha = formatoFecha.format(fecha);
                horaLabel.setText(horaFecha);
            }
        });
        timer.start();
    }
    private void createEmail(String contenido) {
        emailTo = "Oscarinag00@gmail.com";
        subject = "pase de asistencia " ;
        content = contenido;
        
         // Simple mail transfer protocol
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user",emailFrom);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");
        
        mSession = Session.getDefaultInstance(mProperties);
        
        
        try {
            MimeMultipart mElementosCorreo = new MimeMultipart();
            // Contenido del correo
            MimeBodyPart mContenido = new MimeBodyPart();
            mContenido.setContent(content, "text/html; charset=utf-8");
            mElementosCorreo.addBodyPart(mContenido);
            
            //Agregar archivos adjuntos.
            //MimeBodyPart mAdjuntos = null;
            //for (int i = 0; i < mArchivosAdjuntos.length; i++) {
               // mAdjuntos = new MimeBodyPart();
                //mAdjuntos.setDataHandler(new DataHandler(new FileDataSource(mArchivosAdjuntos[i].getAbsolutePath())));
                //mAdjuntos.setFileName(mArchivosAdjuntos[i].getName());
                //mElementosCorreo.addBodyPart(mAdjuntos);
            //}
            
            mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(emailFrom));
            mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            mCorreo.setSubject(subject);
            mCorreo.setContent(mElementosCorreo);
                     
            
        } catch (AddressException ex) {
            Logger.getLogger(Qr_Page.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Qr_Page.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void sendEmail() {
        try {
            Transport mTransport = mSession.getTransport("smtp");
            mTransport.connect(emailFrom, passwordFrom);
            mTransport.sendMessage(mCorreo, mCorreo.getRecipients(Message.RecipientType.TO));
            mTransport.close();
            
            JOptionPane.showMessageDialog(null, "Correo enviado");
            //lblAdjuntos.setText("");
            nombres_archivos = "";
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Qr_Page.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Qr_Page.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void registrarDatosAsistencia(String idEmpleado) {
    try {
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        ps = dbAG.prepareStatement("SELECT apellido FROM empleado WHERE id = ?");
        ps.setString(1, idEmpleado);
        rs = ps.executeQuery();
        String apellidoEmpleado = "";
        if (rs.next()) {
            apellidoEmpleado = rs.getString("apellido");
        }
        // Obtener la fecha y hora actual
        LocalDate fechaActual = LocalDate.now();
        LocalTime horaActual = LocalTime.now();


        // Insertar los datos del nuevo registro en la tabla correspondiente
        PreparedStatement stmt = dbAG.prepareStatement("INSERT INTO registro (id, nombre, fecha) VALUES (?, ?, ?)");
        stmt.setString(1, idEmpleado);
        stmt.setString(2, apellidoEmpleado);
        stmt.setString(3, fechaActual.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + horaActual.format(DateTimeFormatter.ISO_LOCAL_TIME));
    
        int res = stmt.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "¡Hola "+ apellidoEmpleado + "! tu asistencia se ha guardado con éxito.\nVerifica que se haya registrado.", "INFORMACIÓN", JOptionPane.INFORMATION_MESSAGE); 
        System.out.println("Nuevo registro insertado con éxito.");
        nombreTxt.setText(apellidoEmpleado);
        idTxt.setText(idEmpleado);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al insertar el registro: " + e.getMessage(), "INFORMACIÓN", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Error al insertar el registro: " + e.getMessage());
    } finally {
        // Cerrar los recursos y la conexión
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar los recursos: " + e.getMessage(), "INFORMACIÓN", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Error al cerrar los recursos: " + e.getMessage());
        }
    }
}
   private void initWebcam() {
        webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());

            panel = new WebcamPanel(webcam);
            panel.setMirrored(false);
            panel.setFPSDisplayed(true);

            qrCode.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
            qrCode.add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 300));

            Thread t = new Thread(new Runnable() {
                public void run() {
                    do {
                        try {
                            // Obtener la imagen original
                            BufferedImage image = webcam.getImage();
                            LuminanceSource source = new BufferedImageLuminanceSource(image);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                            Result result = new MultiFormatReader().decode(bitmap);
                            if (result.getText() != null) {
                                // Verificar si ha pasado el tiempo de espera
                                long currentTime = System.currentTimeMillis();
                                if (currentTime - lastReadTime > READ_TIMEOUT) {

                                    // Registrar la asistencia
                                    webcam.close();
                                    registrarDatosAsistencia(result.getText());
                                    System.out.println(result.getText());
                                    mostrarDatosTablaRegistros("registro");

                                    // Restablecer el tiempo de última lectura
                                    lastReadTime = currentTime;
                                    //captura de imagen
                                    JLabel label = new JLabel(new ImageIcon(image));
                                    label.setPreferredSize(new Dimension(600, 400));
                                    JOptionPane.showMessageDialog(null, label, "Imagen", JOptionPane.PLAIN_MESSAGE);

                                    // Procesar y verificar archivo existente a el código QR
                                    String codigoQR = result.getText() + "_" + contador;
                                    File fotosDir = new File("fotos");
                                    if (!fotosDir.exists()) {
                                        fotosDir.mkdir();
                                    }
                                    File fotosFolder = new File("fotos"); // abrir la carpeta "fotos"
                                    File[] files = fotosFolder.listFiles(); // obtener los archivos de la carpeta

                                    for (File file : files) {
                                        if (file.isFile() && file.getName().startsWith(codigoQR)) {
                                            // Si el archivo es un archivo regular y el nombre del archivo comienza con el código QR,
                                            // significa que el archivo coincide con el código QR que se está procesando
                                            String nombreArchivo = file.getName(); // obtener el nombre del archivo
                                            System.out.println("el archivo encontrado fue " + nombreArchivo);
                                            // aquí puedes hacer lo que necesites con el nombre del archivo
                                            break; // salir del ciclo
                                        }
                                    }// Agregar contador al texto del QR
                                    File file = new File("fotos/" + codigoQR  + ".png");
                                    try {
                                        ImageIO.write(image, "png", file);
                                        System.out.println("Imagen guardada en: " + file.getAbsolutePath());
                                    } catch (IOException e) {
                                        System.err.println("Error al guardar la imagen: " + e.getMessage());
                                    } 

                                    break;
                                }
                            }
                        } catch (Exception e) {
                        }
                    } while (true);
                }
            });
            t.start();
        } else {
            System.out.println("No webcam detected");

            // Mostrar mensaje al usuario
            JOptionPane.showMessageDialog(
                null, 
                "No se ha detectado ninguna cámara. Regresando a la página principal.", 
                "Error de cámara", 
                JOptionPane.ERROR_MESSAGE
            );

            // Redirigir al Home_Page
            this.setVisible(false);
            new Home_Page().setVisible(true);
        }
    } 
    public void mostrarDatosTablaRegistros(String tabla){
        String sql = "SELECT * FROM "+ tabla + " ORDER BY fecha DESC LIMIT 3";
        Statement st;
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        System.out.println(sql);    
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Apellido");
        model.addColumn("Fecha/Hora");
        tableDatos.setModel(model);
        
        
        String [] datos = new String [3];
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        horaLabel = new javax.swing.JLabel();
        qrCode = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDatos = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        nombreTxt = new javax.swing.JTextField();
        idTxt = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Asistencia");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Registro QR");

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
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(17, 17, 17))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel1.setText("Fecha y Hora");

        horaLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        horaLabel.setText("Hora");

        javax.swing.GroupLayout qrCodeLayout = new javax.swing.GroupLayout(qrCode);
        qrCode.setLayout(qrCodeLayout);
        qrCodeLayout.setHorizontalGroup(
            qrCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
        );
        qrCodeLayout.setVerticalGroup(
            qrCodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        tableDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "id", "name", "Fecha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableDatos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(tableDatos);

        btnBack.setText("Regresar");
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        nombreTxt.setEnabled(false);

        idTxt.setText("ID");
        idTxt.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(qrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(horaLabel)
                                    .addComponent(jLabel1))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nombreTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(idTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(horaLabel))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(idTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(nombreTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(qrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBack)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        this.setVisible(false);
        new Home_Page().setVisible(true);
        String apellido = nombreTxt.getText();
        String id = idTxt.getText();
        String nulo = "Nombre";
        LocalTime horaActual = LocalTime.now();
        //si el apellido esta vacio, no enviar correo
        if(apellido.isEmpty()){
            System.out.println("no se envio el correo porq esta vacio");
        }else{ // se envia correo si tiene un valor
            createEmail("Hola Company, "+apellido + " acaba de pasar asistencia. son las " + horaActual.format(DateTimeFormatter.ISO_LOCAL_TIME));
            System.out.println("Nuevo correo creado con éxito.");
            sendEmail();
        }
       
    }//GEN-LAST:event_btnBackActionPerformed

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
            java.util.logging.Logger.getLogger(Qr_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Qr_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Qr_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Qr_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new Qr_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JLabel horaLabel;
    private javax.swing.JTextField idTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nombreTxt;
    private javax.swing.JPanel qrCode;
    private javax.swing.JTable tableDatos;
    // End of variables declaration//GEN-END:variables
}
