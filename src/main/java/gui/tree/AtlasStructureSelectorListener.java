package gui.tree;

import meta.AtlasStructure;

import java.util.HashMap;

/**
 * @author Felix Meyenhofer
 */
public interface AtlasStructureSelectorListener {

    void valueChanged(HashMap<Integer, AtlasStructure> structures);

    void validationAction(HashMap<Integer, AtlasStructure> structures);

}
