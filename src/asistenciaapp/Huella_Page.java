package asistenciaapp;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author oscar arroyo 17/08/2023 iztapalapa para el mundo
 * terqo company
 */
public class Huella_Page extends javax.swing.JFrame {
    
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
    
    //Variables de Digital Persona
    private DPFPCapture lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    
    public DPFPFeatureSet featuresIncripcion;
    public DPFPFeatureSet featuresVerificacion;
    
    /**
     * Creates new form HuellaPage
     */
    public Huella_Page() {
        iconImage();
        initComponents();
        initTimer();
        mostrarDatosTablaRegistros("registro");
        listReaders();
        Iniciar();
	start();
        EstadoHuellas();
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
        ps = dbAG.prepareStatement("SELECT nombre FROM empleado WHERE id = ?");
        ps.setString(1, idEmpleado);
        rs = ps.executeQuery();
        String nombreEmpleado = "";
        if (rs.next()) {
            nombreEmpleado = rs.getString("nombre");
        }
        // Obtener la fecha y hora actual
        LocalDate fechaActual = LocalDate.now();
        LocalTime horaActual = LocalTime.now();


        // Insertar los datos del nuevo registro en la tabla correspondiente
        PreparedStatement stmt = dbAG.prepareStatement("INSERT INTO registro (id, nombre, fecha) VALUES (?, ?, ?)");
        stmt.setString(1, idEmpleado);
        stmt.setString(2, nombreEmpleado);
        stmt.setString(3, fechaActual.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + horaActual.format(DateTimeFormatter.ISO_LOCAL_TIME));
    
        int res = stmt.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "¡Hola "+ nombreEmpleado + "! tu asistencia se ha guardado con éxito.\nVerifica que se haya registrado.", "INFORMACIÓN", JOptionPane.INFORMATION_MESSAGE); 
        System.out.println("Nuevo registro insertado con éxito.");
        nombreTxt.setText(nombreEmpleado);
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
    public void mostrarDatosTablaRegistros(String tabla){
        String sql = "SELECT * FROM "+ tabla + " ORDER BY fecha DESC LIMIT 3";
        Statement st;
        conexionDB con = new conexionDB();
        Connection dbAG = con.Conectar();
        System.out.println(sql);    
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
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
        EnviarTexto("La Huella Digital ha sido Capturada");
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
                Logger.getLogger(Huella_Page.class.getName()).log(Level.SEVERE, null, ex);
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
                   //btnGuardar.setEnabled(true);
                   //btnGuardar.grabFocus();
                   break;

                   case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                   Reclutador.clear();
                   stop();
                   EstadoHuellas();
                   setTemplate(null);
                   JOptionPane.showMessageDialog(Huella_Page.this, "La Plantilla de la Huella no pudo ser creada, Repita el Proceso", "Inscripcion de Huellas Dactilares", JOptionPane.ERROR_MESSAGE);
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

     /*
      * Guarda los datos de la huella digital actual en la base de datos
      */
    public void guardarHuella(){
         //Obtiene los datos del template de la huella actual
         ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize());
         Integer tamañoHuella=template.serialize().length;

         //Pregunta el nombre de la persona a la cual corresponde dicha huella
         String nombre = JOptionPane.showInputDialog("Nombre Empleado:");
         try {
         //Establece los valores para la sentencia SQL
         Connection c = cn.Conectar(); //establece la conexion con la BD
         PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO empleado(nombre, huella) values(?,?)");

         guardarStmt.setString(1,nombre);
         guardarStmt.setBinaryStream(2, datosHuella,tamañoHuella);
         //Ejecuta la sentencia
         guardarStmt.execute();
         guardarStmt.close();
         JOptionPane.showMessageDialog(null,"Huella Guardada Correctamente");
         cn.desconectar();
         //btnGuardar.setEnabled(false);
         } catch (SQLException ex) {
         //Si ocurre un error lo indica en la consola
         System.err.println("Error al guardar los datos de la huella.");
         }finally{
         cn.desconectar();
         }
       }
        
     /**
      * Identifica a una persona registrada por medio de su huella digital
      */
      public void identificarHuella() throws IOException{
         try {
           //Establece los valores para la sentencia SQL
           Connection c= cn.Conectar();

           //Obtiene todas las huellas de la bd
           PreparedStatement identificarStmt = c.prepareStatement("SELECT id,huella FROM empleado");
           ResultSet rs = identificarStmt.executeQuery();

           //Si se encuentra el nombre en la base de datos
           while(rs.next()){
           //Lee la plantilla de la base de datos
           byte templateBuffer[] = rs.getBytes("huella");
           String idEmpleado=rs.getString("id");
           

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
            if (idEmpleado != null) {
                // Llama a la función para registrar los datos de asistencia
                registrarDatosAsistencia(idEmpleado);
            } else {
                EnviarTexto("Empleado No Existe"); // Opcional: mensaje si el empleado no se encuentra
            }
               mostrarDatosTablaRegistros("registro");
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
    
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        horaLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDatos = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        nombreTxt = new javax.swing.JTextField();
        idTxt = new javax.swing.JTextField();
        lblHuella = new javax.swing.JLabel();
        huellaDevice = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Registro Huella");

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
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(17, 17, 17))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel1.setText("Fecha y Hora");

        horaLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        horaLabel.setText("Hora");

        tableDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "id", "Nombre", "Fecha"
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

        lblHuella.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asistenciaapp/img/lector_conectado.png"))); // NOI18N

        huellaDevice.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        huellaDevice.setText("Device");

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(horaLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(nombreTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                    .addComponent(idTxt)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(huellaDevice)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(horaLabel))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(idTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nombreTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36)
                        .addComponent(huellaDevice)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBack)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        stop();
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
            java.util.logging.Logger.getLogger(Huella_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Huella_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Huella_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Huella_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Huella_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JLabel horaLabel;
    private javax.swing.JLabel huellaDevice;
    private javax.swing.JTextField idTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JTextField nombreTxt;
    private javax.swing.JTable tableDatos;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
