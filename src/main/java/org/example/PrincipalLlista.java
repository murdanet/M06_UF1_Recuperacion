package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PrincipalLlista {
    //Miguel Urdaneta
    public static void main(String[] args) throws Exception {
        List<ArticlesCompra> listaCompra = capturarAtributos();
        generarXML(listaCompra);
        escribirSerializado(listaCompra);
        leerXML("lista_compra.xml");
        deserializarListaCompra("lista_compra.ser");
        mostrarDatosSerializados("lista_compra.ser");
    }

    private static List<ArticlesCompra> capturarAtributos() throws IOException {
        List<ArticlesCompra> listaCompra = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("------Bienvenido a Supermercados Urdaneta--------");
        System.out.println("Introduce los datos de los artículos de la lista de compra:");
        System.out.println("Para finalizar la captura de datos, deja en blanco el campo de descripción.");

        while (true) {
            System.out.println("-------------------");
            System.out.print("Descripción del producto: ");
            String descripcion = reader.readLine();
            if (descripcion.isEmpty()) {
                break;
            }

            System.out.print("Cantidad: ");
            double cantidad = Double.parseDouble(reader.readLine());

            System.out.print("Unidad: ");
            String unidad = reader.readLine();

            System.out.print("Sección: ");
            String seccion = reader.readLine();

            System.out.print("Precio: ");
            double precio = Double.parseDouble(reader.readLine());

            ArticlesCompra articulo = new ArticlesCompra(descripcion, cantidad, unidad, seccion, precio);
            listaCompra.add(articulo);
        }

        return listaCompra;
    }

    private static void generarXML(List<ArticlesCompra> listaCompra) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element llistaCompraElement = document.createElement("llistacompra");
            document.appendChild(llistaCompraElement);

            for (ArticlesCompra articulo : listaCompra) {
                Element articleElement = document.createElement("articulo");
                llistaCompraElement.appendChild(articleElement);

                Element descripcionElement = document.createElement("descripcion");
                descripcionElement.appendChild(document.createTextNode(articulo.getDescripcion()));
                articleElement.appendChild(descripcionElement);

                Element cantidadElement = document.createElement("cantidad");
                cantidadElement.appendChild(document.createTextNode(String.valueOf(articulo.getCantidad())));
                articleElement.appendChild(cantidadElement);

                Element unidadElement = document.createElement("unitad");
                unidadElement.appendChild(document.createTextNode(articulo.getUnidad()));
                articleElement.appendChild(unidadElement);

                Element seccionElement = document.createElement("seccion");
                seccionElement.appendChild(document.createTextNode(articulo.getSeccion()));
                articleElement.appendChild(seccionElement);


                Element precioElement = document.createElement("precio");
                precioElement.appendChild(document.createTextNode(String.valueOf(articulo.getPrecio())));
                articleElement.appendChild(precioElement);

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("lista_compra.xml"));
            transformer.transform(source, result);

            System.out.println("Archivo XML generado con éxito.");
        } catch (Exception e) {
            System.out.println("Error al generar el archivo XML: " + e.getMessage());
        }
    }

    private static void escribirSerializado(List<ArticlesCompra> listaCompra) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("lista_compra.ser"))) {
            outputStream.writeObject(listaCompra);
            System.out.println("Lista de compra serializada y guardada en archivo.");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo serializado: " + e.getMessage());
        }
    }

    private static void leerXML(String archivoXML) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new FileInputStream(archivoXML));

        NodeList articleNodes = document.getElementsByTagName("article");
        List<ArticlesCompra> listaCompra = new ArrayList<>();

        for (int i = 0; i < articleNodes.getLength(); i++) {
            Element articleElement = (Element) articleNodes.item(i);

            String descripcion = articleElement.getElementsByTagName("descripcio").item(0).getTextContent();
            double cantidad = Double.parseDouble(articleElement.getElementsByTagName("quantitat").item(0).getTextContent());
            String unidad = articleElement.getElementsByTagName("unitat").item(0).getTextContent();
            String seccion = articleElement.getElementsByTagName("seccio").item(0).getTextContent();

            ArticlesCompra articulo = new ArticlesCompra(descripcion, cantidad, unidad, seccion, 0);
            listaCompra.add(articulo);
        }

        System.out.println("Datos leídos desde el archivo XML:");
        for (ArticlesCompra articulo : listaCompra) {
            System.out.println("-------------------");
            System.out.println("Descripción: " + articulo.getDescripcion());
            System.out.println("Cantidad: " + articulo.getCantidad());
            System.out.println("Unidad: " + articulo.getUnidad());
            System.out.println("Sección: " + articulo.getSeccion());
            System.out.println("Precio: " + articulo.getPrecio());
            System.out.println("-------------------");
        }
    }

    private static List<ArticlesCompra> deserializarListaCompra(String archivoSerializado) throws Exception {
        List<ArticlesCompra> listaCompra = new ArrayList<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(archivoSerializado))) {
            listaCompra = (List<ArticlesCompra>) inputStream.readObject();
        }

        return listaCompra;
    }

    private static void mostrarDatosSerializados(String archivoSerializado) throws Exception {
        List<ArticlesCompra> listaCompra = deserializarListaCompra(archivoSerializado);

        System.out.println("Datos leídos desde el archivo serializado:");
        System.out.println("*-------------------LISTA DE COMPRAS-------------------*");
        for (ArticlesCompra articulo : listaCompra) {
            System.out.println("*-------------------*");
            System.out.println("Descripción: " + articulo.getDescripcion());
            System.out.println("Cantidad: " + articulo.getCantidad());
            System.out.println("Unidad: " + articulo.getUnidad());
            System.out.println("Sección: " + articulo.getSeccion());
            System.out.println("Precio: " + articulo.getPrecio());
            System.out.println("*--------------------------------------------------------*");
        }
    }
}
