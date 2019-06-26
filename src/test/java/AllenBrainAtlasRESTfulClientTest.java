import file.AllenImage;
import file.AllenXml;
import meta.RefVolAttribute;
import org.jdom2.Element;
import rest.AllenAPI;
import status.StatusReceiver;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;

class AllenBrainAtlasRESTfulClientTest {

    public static void main(String[] args)
            throws TransformerException, IOException, URISyntaxException, InterruptedException {

        referenceVolumeDownload();
        System.out.println("\n\n");
        listProducts();
    }

    private static void referenceVolumeDownload() throws TransformerException, IOException, URISyntaxException, InterruptedException {
        AllenBrainAtlasRESTfulClient client = AllenBrainAtlasRESTfulClient.getInstance();

        StatusReceiver observer = new StatusReceiver();
        client.addObserver(observer);

        AllenImage img = client.getReferenceVolume(RefVolAttribute.Modality.AUTOFLUO, RefVolAttribute.VoxelResolution.FIFTY);
        Thread.sleep(1500);
        client.setStatus(img.getStatusMessage());
    }

    private static void listProducts() throws IOException, TransformerException, URISyntaxException {
        AllenBrainAtlasRESTfulClient client = new AllenBrainAtlasRESTfulClient();
        StatusReceiver observer = new StatusReceiver();
        client.addObserver(observer);

        // List all the products for mouse and the associated datasets
        AllenXml mouse_products = client.cache.getResponseXml(AllenAPI.RMA.createProductQueryUrl(RefVolAttribute.Species.MOUSE));
        client.setStatus("Mouse products:");
        for (Element product : mouse_products.getElements()) {
            String product_name = product.getChild("name").getValue();
            String product_id = product.getChild("id").getValue();

            if (product_name.contains("Reference")) {
                AllenXml datasets = client.cache.getResponseXml(
                        AllenAPI.RMA.createSectionDataSetsQuery(Integer.parseInt(product_id), RefVolAttribute.PlaneOfSection.CORONAL));
                for (Element dataset : datasets.getElements()) {
                    String dataset_id = dataset.getChild("id").getValue();
                    client.setStatus("\t\t" + dataset_id);
                }
            }
        }
    }
}