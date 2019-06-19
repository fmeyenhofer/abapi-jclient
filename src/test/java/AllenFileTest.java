import file.AllenJson;
import file.AllenSvg;
import file.AllenXml;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class AllenFileTest {

    public static void main(String[] args) throws TransformerException, IOException, URISyntaxException {
        downloadJSON();
        downloadXML();
        downloadSVG();
    }

    private static void downloadJSON() throws IOException, TransformerException, URISyntaxException {
        AllenJson json = new AllenJson(new URL("http://api.brain-map.org/api/v2/data/query.json?criteria=model::AtlasImage,rma::criteria,data_set[id$eq100048576]"));
        System.out.println("Number of lines: " + json.getResponseSize());
    }

    private static void downloadXML() throws IOException, TransformerException, URISyntaxException {
        URL url = new URL("http://api.brain-map.org/api/v2/data/query.xml?criteria=model::Product,rma::criteria,[name$il*Reference*]");
        AllenXml xml = new AllenXml(url);
        xml.printElements(xml.getDom().getRootElement());

        System.out.println("\nGet the value of the first 'name' tag: " + xml.getValue("name").toString());

        System.out.println("Get all the values of the 'name' tags: ");
        for (Object obj : xml.getValues("name")) {
            System.out.println("\t" + obj.toString());
        }

        System.out.println("\nData model: " + xml.getResponseModel());
    }

    private static void downloadSVG() throws IOException, TransformerException, URISyntaxException {
        URL query = new URL("http://api.brain-map.org/api/v2/svg_download/100960333?downsample=3");

        AllenSvg svg = new AllenSvg(query);

        svg.printNodes();
    }
}