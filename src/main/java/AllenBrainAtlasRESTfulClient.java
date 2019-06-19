
import file.AllenXml;
import file.AllenImage;
import meta.AtlasCatalog;
import meta.RefVolAttribute;
import meta.AtlasStructure;
import meta.AtlasStructureGraph;

import org.jdom2.Element;
import rest.AllenAPI;
import rest.AllenCache;
import status.StatusBroadcaster;

import javax.xml.transform.TransformerException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RESTful client for the http://atlas.brain-map.org
 *
 * This class exposes a comprehensible set of methods to interact with the
 * Allen Brain Atlas API.
 *
 * This is the singleton layer on top of the {@link AllenCache}. The cache takes care
 * of downloading the data (once) if it is queried.
 *
 * It aggregates the interactions that have to be executed with the Allen API
 * and exposes typical actions that are needed in the context of ImageJ
 *
 * For reference:
 * http://help.brain-map.org/display/api/Allen+Brain+Atlas+API
 *
 * @author Felix Meyenhofer
 */
public class AllenBrainAtlasRESTfulClient extends StatusBroadcaster {

    /** Singleton instance of the class */
    private static AllenBrainAtlasRESTfulClient singleton = new AllenBrainAtlasRESTfulClient();

    /** Local cache for atlas data */
    protected AllenCache cache = new AllenCache();

    /** Pattern for "[name] ([id])" strings */
    private static Pattern pattern = Pattern.compile("(.*)(\\d+)\\)$");


    /**
     * Constructor
     */
    AllenBrainAtlasRESTfulClient() {}

    public static AllenBrainAtlasRESTfulClient getInstance() {
        return singleton;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        cache.addObserver(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
        cache.deleteObserver(o);
    }

    public File getSectionImageDirectory() {
        return cache.getDirectory(AllenCache.DataType.img);
    }

    public File getAnnotationGrid(String resolution) throws TransformerException, IOException, URISyntaxException {
        AllenImage allenImage = this.cache.getAnnotationGrid(resolution);
        return allenImage.getFile();
    }

    public AllenXml getDatasetMetadata(String product_name, String dataset_id)
            throws TransformerException, IOException, URISyntaxException {
        return cache.getImageMetadataXml(product_name, dataset_id);
    }

    public AllenXml getSectionImageMetadata(String product_name, String dataset_id, String section_id)
            throws TransformerException, IOException, URISyntaxException {
        return cache.getImageMetadataXml(product_name, dataset_id, section_id);
    }

    public AllenXml getAtlasAnnotationMetadata(String product_id)
            throws IOException, TransformerException, URISyntaxException {
        return cache.getResponseXml(AllenAPI.RMA.createAtlasStructuresQuery(product_id));
    }

    public AtlasStructureGraph getAnnotationStructureGraph(AtlasCatalog atlas)
            throws IOException, TransformerException, URISyntaxException {
        String graph_id = atlas.getStructureGraphId().toString();
        AllenXml xml = cache.getStructureGraphXml("StructureGraph", graph_id);

        HashMap<Integer, AtlasStructure> graph = new HashMap<>();
        Element root = xml.getDom().getRootElement().getChild("structure");
        parseStructureXmlElements(graph, root, "/");

        return new AtlasStructureGraph(graph);
    }

    public AllenImage getReferenceVolume(RefVolAttribute.Modality modality, RefVolAttribute.VoxelResolution resolution)
            throws TransformerException, IOException, URISyntaxException, InterruptedException {

        return cache.getReferenceVolume(modality, resolution);
    }

    public List<String> getProductList(String species) throws IOException, TransformerException, URISyntaxException {
        AllenXml mouse_products = cache.getResponseXml(AllenAPI.RMA.createProductQueryUrl(RefVolAttribute.Species.get(species)));

        List<String> products = new ArrayList<>();
        for (Element product : mouse_products.getElements()) {
            String product_name = product.getChild("name").getValue();
            String product_id = product.getChild("id").getValue();
            products.add(product_name + " (" + product_id + ")");
        }

        return products;
    }

    public List<String> getDatasetList(String product_id) throws IOException, TransformerException, URISyntaxException {
        URL query = AllenAPI.RMA.createSectionDataSetsQuery(Integer.parseInt(product_id), RefVolAttribute.PlaneOfSection.CORONAL);
        AllenXml datasets = cache.getResponseXml(query);

        List<String> list = new ArrayList<>();
        for (Element dataset_element : datasets.getElements()) {
            String dataset_id = dataset_element.getChild("id").getValue();
            list.add(dataset_id);
        }

        return list;
    }

    private void parseStructureXmlElements(HashMap<Integer, AtlasStructure> collector, Element element, String path) {
        AtlasStructure structure = new AtlasStructure(element);
        path += structure.getId() + "/";
        structure.setGraphPath(path);
        collector.put(structure.getId(), structure);

        Element children = element.getChild("children");
        if (children != null) {
            for (Object obj : children.getChildren()) {
                Element child = (Element) obj;
                parseStructureXmlElements(collector, child, path);
            }
        }
    }

    /**
     * Download a reference volume (average template) image
     *
     * @param resolution pixel resolution in um
     */
    private void downloadMouseRefVol(RefVolAttribute.VoxelResolution resolution)
            throws IOException, URISyntaxException, TransformerException, InterruptedException {
        RefVolAttribute.Modality[] types = {
                RefVolAttribute.Modality.AUTOFLUO,
                RefVolAttribute.Modality.NISSEL,
                RefVolAttribute.Modality.ANNOTATION};

        for (RefVolAttribute.Modality type : types) {
            String msg = cache.getReferenceVolume(type, resolution).getStatusMessage();
            setStatus(msg);
        }
    }

    /**
     * Download all the section images of a section dataset
     *
     * @param dataset_id id of the dataset
     * @param downsample down sampling of the images
     * @param quality jpg quality
     */
    public void downloadSectionDataSet(String dataset_id, int downsample, int quality)
            throws IOException, TransformerException, URISyntaxException {

        URL query = AllenAPI.RMA.createSectionDataSetQuery(dataset_id);
        AllenXml datasetXml = cache.getResponseXml(query);
        Element dataset_element = datasetXml.getElements().get(0);
        String product_name = dataset_element.getChild("products").getChild("product").getChild("abbreviation").getValue();

        setStatus("Downloading SectionDataset " + dataset_id, 0,0);

        AllenXml sub_images = cache.getResponseXml(AllenAPI.RMA.createSectionImagesQuery(dataset_id));
        int N = sub_images.getElements().size();
        int n = 1;
        for (Element image_element : sub_images.getElements()) {
            String image_id = image_element.getChild("id").getValue();
            cache.getImageMetadataXml(dataset_element, product_name, dataset_id, image_id);
            cache.getImage(downsample, quality, product_name, dataset_id, image_id);
            setStatus( "Downloaded SectionImage " + image_id, n++, N);
            n++;
        }

        setStatus("Downloaded SectionDataset " + dataset_id, N, N);
    }

    /**
     * Extract ID substring
     *
     * @param input string of the type "[name] ([ID])"
     * @return ID
     */
    static String extractId(String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return matcher.group(2);
        }

        throw new RuntimeException("String '" + input + "' doe not contain ([ID]) at the end.");
    }
}
