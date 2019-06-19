import gui.SectionDatasetSelector;

import javax.swing.*;


public class DatasetSelectorTest {

    public static void main(String[] args) {
        AllenBrainAtlasRESTfulClient client = AllenBrainAtlasRESTfulClient.getInstance();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SectionDatasetSelector dialog = SectionDatasetSelector.createAndShow(client.getSectionImageDirectory());
                System.out.println(dialog.getSelection());
                System.exit(0);
            }
        });
    }
}