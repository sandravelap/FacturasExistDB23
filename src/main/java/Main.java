import org.w3c.dom.Node;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class Main {
    static Collection col = null;
    static Node nodo = null;
    static XMLResource nodoDom = null;
    public static void main(String[] args) throws XMLDBException {
        col = Conexion.conectar();
        if (col!=null) {
            System.out.println("conectado");
        }
        //subirArchivo();
        listarArtVendidos();

        //Conexion.desconectar();
        col = Conexion.conectar();
        if (col!=null) {
            System.out.println("conectado");
        }
        cargarFacturas1();
        Conexion.desconectar();
    }

    private static void subirArchivo() {
        File archivo = new File("src/main/resources/detalleFacturas.xml");
        if (col!=null){
            try {
                Resource res = col.createResource(archivo.getName(),"XMLResource");
                res.setContent(archivo);
                col.storeResource(res);
            } catch (XMLDBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void cargarFacturas1() {
        if (col!= null) {
            try {
                XPathQueryService facturasCod1;
                facturasCod1 = (XPathQueryService) col.getService("XPathQueryService", "3.1");
                col.setProperty("indent", "yes");
                facturasCod1.setProperty("indent", "yes");
                ResourceSet result = facturasCod1.query("for $facturas in doc(\"detalleFacturas.xml\")//factura\n" +
                        "where some $p in $facturas/producto\n" +
                        "    satisfies ($p/codigo=1)\n" +
                        "return \n" +
                        "    <factura>{$facturas/codigo}</factura>\n");

                ResourceIterator i;
                i = result.getIterator();
                if (!i.hasMoreResources()) {
                    System.out.println(" LA CONSULTA NO DEVUELVE NADA O ESTÁ MAL ESCRITA");
                }
                while (i.hasMoreResources()) {
                    Resource r = i.nextResource();
                    System.out.println("--------------------------------------------");
                    System.out.println((String) r.getContent());
                    System.out.println(r.getResourceType());
                    //creamos la variables XMLResource que nos permite extraer luego un objeto Node
                }
                //crear un archivo que lo contiene
                col.close();
            } catch (XMLDBException e) {
                System.out.println(" ERROR AL CONSULTAR DOCUMENTO.");
                e.printStackTrace();
            }
        }
    }

    private static void listarArtVendidos() {
        if (col!= null) {
            try {
                XPathQueryService servicio;
                servicio = (XPathQueryService) col.getService("XPathQueryService", "3.0");
//                col.setProperty("indent", "yes");
//                servicio.setProperty("indent","yes");
                ResourceSet result = servicio.query("let $codigos := distinct-values(doc(\"detalleFacturas.xml\")//factura/producto/codigo)\n" +
                        "let $ventas := doc(\"detalleFacturas.xml\")//producto\n" +
                        "return \n" +
                        "    <ventas>\n" +
                        "        {for $codigo in $codigos\n" +
                        "        return\n" +
                        "        <articulo codigo=\"{$codigo}\">\n" +
                        "            <unidades_vendidas>{sum($ventas[codigo=$codigo]/unidades)}</unidades_vendidas>\n" +
                        "            \n" +
                        "        </articulo>\n" +
                        "        }\n" +
                        "    </ventas>");
                // recorrer los datos del recurso.
                ResourceIterator i;
                i = result.getIterator();
                if (!i.hasMoreResources()) {
                    System.out.println(" LA CONSULTA NO DEVUELVE NADA O ESTÁ MAL ESCRITA");
                }
                while (i.hasMoreResources()) {
                    Resource r = i.nextResource();
                    System.out.println("--------------------------------------------");
                    System.out.println((String) r.getContent());
                    nodoDom = (XMLResource) r;
                    nodo = nodoDom.getContentAsDOM();
                }
                File archivo = new File("target/ventas.xml");
                Source source = new DOMSource(nodo); //se crea la fuente xml a partir del resultado de la consulta (Node)
                Result resultado = new StreamResult(archivo);  //se crea el resultado en el archivo
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http:xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(source, resultado);
                System.out.println(nodo);
                Resource rCol = col.createResource(archivo.getName(), "XMLResource");
                rCol.setContent(archivo);
                col.storeResource(rCol);
                col.close();
            } catch (XMLDBException e) {
                System.out.println(" ERROR AL CONSULTAR DOCUMENTO.");
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Error en la conexión. Comprueba datos.");
        }

    }
}
