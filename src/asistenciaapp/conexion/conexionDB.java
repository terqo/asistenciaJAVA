package asistenciaapp.conexion;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author oscar
 */
public class conexionDB {
    //conexion a mysql
    public Connection con;
    //valores
    public Statement stm;
    public ResultSet rs;
    
    public String puerto="3306";
    public String nombreServidor="localhost";
    public String db="asistenciaDB";
    public String ruta = "jdbc:mysql://";
    public String servidor = nombreServidor+":"+puerto+"/";
    //datos de conexion a la bd
    private String driver = "com.mysql.jbdc.Driver";
    private String user = "root";
    private String pass = "";
    
    //funcion conectarse a la bd de mysql
    public Connection Conectar(){
        con = null;
        try{
            con = (Connection) DriverManager.getConnection(ruta+servidor+db, user,pass);
            
            if(con!=null){
                
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "error"+e.toString());
        }
        return con;
    }
    
    public void desconectar(){
        con = null;
        System.out.println("Desconexion a base de datos listo...");
    }
    
    
    
}
