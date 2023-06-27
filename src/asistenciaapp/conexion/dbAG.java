package asistenciaapp.conexion;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author oscar
 */
public class dbAG {
    //conexion a mysql
    public Connection con;
    //valores
    public Statement stm;
    public ResultSet rs;
    //datos de conexion a la bd
    private String driver = "com.mysql.jbdc.Driver";
    private String user = "root";
    private String pass = "";
    private String url = "jdbc:mysql://localhost:3306/asistenciaDB";
    
    //funcion conectarse a la bd de mysql
    public Connection Conectar(){
        con = null;
        try{
            con = (Connection) DriverManager.getConnection(url, user,pass);
            
            if(con!=null){
                
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "error"+e.toString());
        }
        return con;
    }
    
    
    
    
    
}
