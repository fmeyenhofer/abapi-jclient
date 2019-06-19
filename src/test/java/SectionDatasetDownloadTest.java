import gui.SectionDatasetDownloadDialog;
import meta.RefVolAttribute;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;


public class SectionDatasetDownloadTest {

    public static void main(String[] args) throws TransformerException, IOException, URISyntaxException {

        AllenBrainAtlasRESTfulClient client = AllenBrainAtlasRESTfulClient.getInstance();

        HashMap<String, HashMap<String, List<String>>> map = new HashMap<>();
        for (String species : RefVolAttribute.Species.getNames()) {
            HashMap<String, List<String>> submap = new HashMap<>();
            System.out.println("species: " + species);
            for (String product : client.getProductList(species)) {
                String pid = AllenBrainAtlasRESTfulClient.extractId(product);
                List<String> datasets = client.getDatasetList(pid);
                System.out.println(" product: " + product);
                System.out.println("          " + datasets.size() + " datasets");
                submap.put(product, datasets);
            }
            map.put(species, submap);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SectionDatasetDownloadDialog dialog = SectionDatasetDownloadDialog.createAndShow(map);
                System.out.println("Selection:\ndataset: " + dialog.getDatasetId()
                        + "\nquality: " + dialog.getQuality()
                        + "\ndownsampling: " + dialog.getSampling());
                System.exit(0);
            }
        });
    }
}