package gui;

import meta.RefVolAttribute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

/**
 * Dialog to select a SectionDataset from the {@link ../AllenBrainAtlasRESTfulClient}.
 *
 * @author Felix Meyenhofer
 */
public class SectionDatasetDownloadDialog extends JPanel implements ActionListener {

    private final String SPECIES_SELECTOR_NAME = "Species";
    private final String PRODUCT_SELECTOR_NAME = "Product";
    private final String DATASET_SELECTOR_NAME = "Dataset";
    private final String SAMPLING_SELECTOR_NAME = "Down-sampling";
    private final String QUALITY_SELECTOR_NAME = "JPEG quality";
    private final String OK_BUTTON_NAME = "OK";
    private final String CANCEL_BUTTON_NAME = "Cancel";

    private JComboBox<String> speciesSelector;
    private JComboBox<String> productSelector;
    private JComboBox<String> datasetSelector;
    private JSpinner samplingSelector;
    private JSlider qualitySelector;

    private HashMap<String, HashMap<String, List<String>>> catalog;


    private SectionDatasetDownloadDialog(HashMap<String, HashMap<String, List<String>>> data) {
        super();

        this.catalog = data;

        qualitySelector = new JSlider(0, 100, 100);
        qualitySelector.setName(QUALITY_SELECTOR_NAME);
        qualitySelector.setMajorTickSpacing(10);
        qualitySelector.setPaintLabels(true);
        qualitySelector.setPaintTicks(true);

        samplingSelector = new JSpinner(new SpinnerNumberModel(4, 0, 10, 1));
        samplingSelector.setName(SAMPLING_SELECTOR_NAME);

        datasetSelector = new JComboBox<>();
        datasetSelector.setName(DATASET_SELECTOR_NAME);
        datasetSelector.addActionListener(this);

        productSelector = new JComboBox<>();
        productSelector.setName(PRODUCT_SELECTOR_NAME);
        productSelector.addActionListener(this);

        speciesSelector = new JComboBox<>();
        speciesSelector.setName(SPECIES_SELECTOR_NAME);
        for (String item : RefVolAttribute.Species.getNames()) {
            speciesSelector.addItem(item);
        }
        speciesSelector.setSelectedIndex(1); // default to mouse
        speciesSelector.addActionListener(this);

        JButton button2 = new JButton(OK_BUTTON_NAME);
        button2.setName(OK_BUTTON_NAME);
        button2.addActionListener(this);
        JButton button1 = new JButton(CANCEL_BUTTON_NAME);
        button1.setName(CANCEL_BUTTON_NAME);
        button1.addActionListener(this);
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(button1);
        panel.add(button2);

        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(new JLabel(SPECIES_SELECTOR_NAME), new GridBagConstraints(0, 0,
                1, 1,
                0, 0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0));
        this.add(speciesSelector, new GridBagConstraints(1,0,
                1,1,
                0,0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0,0,0, 0),
                0, 0));
        this.add(new JLabel(PRODUCT_SELECTOR_NAME), new GridBagConstraints(0,1,
                1,1,
                0,0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0,0,0, 0),
                0, 0));
        this.add(productSelector, new GridBagConstraints(1,1,
                1,1,
                0,0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0,0,0, 0),
                0, 0));
        this.add(new JLabel(DATASET_SELECTOR_NAME), new GridBagConstraints(0,2,
                1,1,
                0,0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0,0,0, 0),
                0, 0));
        this.add(datasetSelector, new GridBagConstraints(1, 2,
                1, 1,
                0, 0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0),
                0, 0));
        this.add(new JLabel(SAMPLING_SELECTOR_NAME), new GridBagConstraints(0,3,
                1,1,
                0,0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0,0,0, 0),
                0, 0));
        this.add(samplingSelector, new GridBagConstraints(1, 3,
                1, 1,
                1, 0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0),
                0, 0));
        this.add(new JLabel(QUALITY_SELECTOR_NAME), new GridBagConstraints(0,4,
                1,1,
                0,0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0,0,0, 0),
                0, 0));
        this.add(qualitySelector, new GridBagConstraints(1, 4,
                2, 1,
                1, 0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0));
        this.add(panel, new GridBagConstraints(1, 5,
                1, 1,
                1, 0,
                GridBagConstraints.EAST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0),
                0, 0));
    }

    public static SectionDatasetDownloadDialog createAndShow(HashMap<String, HashMap<String, List<String>>> data) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        SectionDatasetDownloadDialog dialog = new SectionDatasetDownloadDialog(data);
        dialog.update();
        
        JDialog frame = new JDialog();
        frame.setTitle("Select a SectionDataset");
        frame.add(dialog);
        frame.setModal(true);
        frame.setSize(new Dimension(500, 250));
        frame.setLocation(dim.width / 2 - 250, dim.height / 2 - 125);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.pack();

        return dialog;
    }

    public String getDatasetId() {
        return (String) datasetSelector.getSelectedItem();
    }

    public int getSampling() {
        return (int) samplingSelector.getValue();
    }

    public int getQuality() {
        return qualitySelector.getValue();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update(((Component) e.getSource()).getName());
    }

    private void update() {
        update(this.SPECIES_SELECTOR_NAME);
    }

    private void update(String control) {
        JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);

        switch (control) {

            case SPECIES_SELECTOR_NAME:
                loadProducts();
                break;

            case PRODUCT_SELECTOR_NAME:
                loadDatasets();
                break;

            case DATASET_SELECTOR_NAME:
                break;

            case SAMPLING_SELECTOR_NAME:
                break;

            case QUALITY_SELECTOR_NAME:
                break;

            case OK_BUTTON_NAME:
                int index = datasetSelector.getSelectedIndex();
                if (index < 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "You have to select all the way through to a quality.");
                    break;
                }
                dialog.dispose();
                break;

            case CANCEL_BUTTON_NAME:
                datasetSelector.setSelectedIndex(-1);
                dialog.dispose();
                break;
        }
    }

    private void loadProducts() {
        int parentIndex = speciesSelector.getSelectedIndex();
        if (parentIndex > -1) {
            productSelector.removeAllItems();
            String species = speciesSelector.getItemAt(parentIndex);

            for (String product : catalog.get(species).keySet()) {
                productSelector.addItem(product);
            }
        }
    }

    private void loadDatasets() {
        int grandParentIndex = speciesSelector.getSelectedIndex();
        if (grandParentIndex > -1) {
            String species = speciesSelector.getItemAt(grandParentIndex);

            int parentIndex = productSelector.getSelectedIndex();
            if (parentIndex > -1) {
                datasetSelector.removeAllItems();

                String product = productSelector.getItemAt(parentIndex);
                for (String dataset : catalog.get(species).get(product)) {
                    datasetSelector.addItem(dataset);
                }
            }
        }
    }
}
