import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public class Conexion {
    //variables para la conexión
    //Driver para eXist-DB
    final static String driver = "org.exist.xmldb.DatabaseImpl";
    final static String URI = "xmldb:exist://localhost:8181/exist/xmlrpc/db/examenMarzo"; //URI colección
    final static String usu = "admin"; //Usuario
    final static String usuPwd = ""; //Clave
    static Collection col = null; //colección para almacenar las consultas
    public static Collection conectar(){
        try {
            Class cl = Class.forName(driver); //Cargar del driver
            Database database = (Database) cl.newInstance(); //Instancia de la BD
            DatabaseManager.registerDatabase(database); //Registro del driver
            col = DatabaseManager.getCollection(URI, usu, usuPwd);
            return col;
        } catch (XMLDBException e) {
            System.out.println("Error al inicializar la BD eXist.");
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error en el driver.");
            //e.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("Error al instanciar la BD.");
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("Error al instanciar la BD.");
            //e.printStackTrace();
        }
        return null;
    }
    public static void desconectar() throws XMLDBException {
        col.close();
    }
}
