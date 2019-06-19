import file.AllenXml;
import gui.tree.AtlasStructureSelector;
import gui.tree.AtlasStructureSelectorListener;
import meta.AtlasStructure;
import org.jdom2.Element;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;


public class StructureSelectorTest {

    public static void main(String[] args) throws TransformerException, IOException, URISyntaxException {
        AllenBrainAtlasRESTfulClient client = AllenBrainAtlasRESTfulClient.getInstance();
        AllenXml structuresMetadata = client.getAtlasAnnotationMetadata("12");

        // Fetch the data from the xml
        HashMap<Integer, AtlasStructure> graph = new HashMap<>();
        for (Element element : structuresMetadata.getElements()) {
            AtlasStructure structure = new AtlasStructure(element);
            graph.put(structure.getId(), structure);
        }

        AtlasStructureSelector tree = new AtlasStructureSelector(graph);

        tree.addStructureSelectionListener(new AtlasStructureSelectorListener() {
            @Override
            public void valueChanged(HashMap<Integer, AtlasStructure> structures) {
                System.out.println("change");
            }

            @Override
            public void validationAction(HashMap<Integer, AtlasStructure> structures) {
                for (Integer key : structures.keySet()) {
                    System.out.println(key + ": " + structures.get(key).getName());
                }
            }
        });

        JDialog dialog = new JDialog();
        dialog.setTitle("Atlas Structure selection");
        dialog.add(tree);
        dialog.setModal(true);
        dialog.setSize(new Dimension(400, 600));
        dialog.setLocationByPlatform(true);
        dialog.setVisible(true);
        dialog.toFront();
        dialog.pack();
    }
}