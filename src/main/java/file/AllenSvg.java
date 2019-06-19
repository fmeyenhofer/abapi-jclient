package file;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle the SVG files downloaded from the Allen API
 *
 * @author Felix Meyenhofer
 */
public class AllenSvg extends AllenFile {

    /** SVG content */
    private Document dom;

    /** path attribute to identify the contour */
    private static final String PATH_CONTOUR_ATTRIBUTE = "structure_id";

    /** path attribute value to identify the contour */
    private static final String PATH_CONTOUR_VALUE = "8";


    /**
     * Download to memory
     *
     * @param url remote file
     */
    public AllenSvg(URL url) throws TransformerException, IOException, URISyntaxException {
        super(url, null);
    }

    /**
     * {@inheritDoc}
     */
    public AllenSvg(URL url, File file) throws IOException, URISyntaxException, TransformerException {
        super(url, file);
    }

    /**
     * {@inheritDoc}
     */
    public AllenSvg(File file) throws IOException, URISyntaxException {
        super(file);

    }

    private Document getDom() {
        return dom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void load(File file) throws IOException, URISyntaxException {
        load(file.toURI().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void load(URL url) throws IOException {
        load(url.toString());
    }

    /**
     * Loading always works with the URI...
     *
     * @throws IOException
     */
    private void load(String uri) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        this.dom = factory.createDocument(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void save() throws TransformerException, FileNotFoundException {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(getDom()), new StreamResult(new FileOutputStream(getFile())));
    }

    public void printNodes() {
        printNodes(getDom().getDocumentElement());
    }

    /**
     * print the xml element and all its children
     *
     * @param node xml
     */
    public void printNodes(Node node) {
        printNodes(node, "");
    }

    /**
     * Recursively print all the elements of the xml document.
     * The lines are indented according the hierarchie.
     *
     * @param node xml element to print
     * @param indent of the line
     */
    private void printNodes(Node node, String indent) {
        System.out.println(indent + node2string(node));
        indent += "\t";
        NodeList children = node.getChildNodes();
        for (int c = 0; c < children.getLength(); c++) {
            Node child = children.item(c);


            if (child.hasChildNodes()) {
                printNodes(child, indent);
            } else {
                System.out.println(indent + node2string(child));
            }
        }
    }

    private String node2string(Node node) {
        StringBuilder str = new StringBuilder();
        str.append(node.getNodeName());
        str.append(" (");

        if (node.hasAttributes()) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node item = node.getAttributes().item(i);
                str.append(item.getNodeName());
                str.append("; ");
                str.append(item.getNodeValue());
            }
        } else {
            str.append("-");
        }

        str.append("): ");
        str.append(node.getNodeValue());

        return str.toString();
    }

    /**
     * Get the contour over all the annotations (brain section contours)
     *
     * @return SVG containing the brain section contours
     */
    public Document createGrayMatterSvg(File file) throws TransformerException, FileNotFoundException {
        if (dom == null) {
            throw (new RuntimeException("No document loaded. use the load() method."));
        }

        // Filter
        Map<String, String[]> filters = new HashMap<>(1);
        filters.put("path", new String[]{"structure_id", "8"});
        filters.put("#text", null);
        Node node = selectiveClone(dom.getDocumentElement(), filters);

        // Create a new document
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        Document doc = impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        Element root = doc.getDocumentElement();

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item((i));
            doc.adoptNode(child);
            root.appendChild(child);
        }

        // Write to file
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));

        return doc;
    }

    /**
     * Find a path element among the children of the {@param node} file with an attribute {@param name} and its
     * {@param value}.
     *
     * @param node SCG node
     * @param name node attribute name
     * @param value node attribute value
     * @return child node
     */
    private Node findPath(Node node, String name, String value) {
        NodeList children = node.getChildNodes();
        for (int c = 0; c < children.getLength(); c++) {
            Node child = children.item(c);

            if (child.hasChildNodes()) {
                return findPath(child, name, value);
            } else {
                if (child.getNodeName().equals("path")) {
                    if (child.getAttributes().getNamedItem(name).getNodeValue().equals(value)) {
                        return child;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Find a path element among the children of the root element of this SVG file ({@link #dom})
     * using an node attribute {@param name} and its {@param value}.
     *
     * @param name node attribute name
     * @param value node attribute value
     * @return child node
     */
    private Node findPath(String name, String value) {
        return findPath(dom.getDocumentElement(), name, value);
    }

    /**
     * Clone a node and its children, provided that there is no filter that chucks them out. This method is recursive
     * whenever the node has child nodes.
     *
     * @param node input document element
     * @param filters {@link Map} where the key is a node name, and the values are attribute-value tuples.
     * @return clone of the tree from node
     */
    private Node selectiveClone(Node node, Map<String, String[]> filters) {
        Node copy = node.cloneNode(false);

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item((i));

            boolean skip = false;
            for (String filter : filters.keySet()) {
                String[] attributeValue = filters.get(filter);
                if (child.getNodeName().equals(filter)) {
                    if ((attributeValue == null) || !child.getAttributes().getNamedItem(attributeValue[0]).getNodeValue().equals(attributeValue[1])) {
                        skip = true;
                        break;
                    }
                }
            }

            if (!skip) {
                Node copy2;
                if (node.hasChildNodes()) {
                    copy2 = selectiveClone(child, filters);
                } else {
                    copy2 = child.cloneNode(false);
                }
                copy.appendChild(copy2);
            }
        }

        return copy;
    }
}