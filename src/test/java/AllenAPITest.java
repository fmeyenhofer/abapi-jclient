import file.AllenXml;
import meta.RefVolAttribute;
import org.jdom2.Element;
import rest.AllenAPI;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Felix Meyenhofer
 * created: 19.06.19.
 */
public class AllenAPITest {

    public static void main(String[] args) throws IOException, TransformerException, URISyntaxException {
        URL query = AllenAPI.RMA.createProductQueryUrl(RefVolAttribute.Species.MOUSE);
        URL adjusted = AllenAPI.RMA.adjustResponseSize(query);
        AllenXml xml = new AllenXml(adjusted);

        String filename = AllenAPI.RMA.url2filename(xml.getUrl());
        URL url = AllenAPI.RMA.filename2url(filename);

        System.out.println("URL handling");
        System.out.println("\toriginal url: " + query.toString());
        System.out.println("\tmodel: " + xml.getResponseModel());
        System.out.println("\tresponse size: " + xml.getResponseSize());
        System.out.println("\tadjusted url: " + xml.getUrl());
        System.out.println("\tfile name: " + filename);
        System.out.println("\trestored url: " + url.toString());

        System.out.println("Mouse products: ");
        for (Element product : xml.getElements()) {
            System.out.println("\t" + product.getChild("name").getValue() +
                    " (" + product.getChild("id").getValue() + ")");
        }
    }
}
