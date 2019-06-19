package meta;

/**
 * Basic information about the Allen atlases.
 * see http://help.brain-map.org/display/api/Atlas+Drawings+and+Ontologies
 *
 * @author Felix Meyenhofer
 */
public enum AtlasCatalog {
    MOUSE3D         ("Adult Mouse, 3D Coronal"                 ,602630314, 1,  "28"),
    MOUSEP56C       ("Mouse, P56, Coronal"                     ,1        , 1,  "28,159226751"),
    MOUSEP56S       ("Mouse, P56, Sagital"                     ,2        , 1,  "28,159226751"),
    DEVMOUSEP56     ("Developing Mouse, P56"                   ,181276165, 17, "32,167675952"),
    DEVMOUSEP14     ("Developing Mouse, P14"                   ,181276164, 17, "32"),
    DEVMOUSEP4      ("Developing Mouse, P4"                    ,181276162, 17, "32"),
    DEVMOUSEE185    ("Developing Mouse, E18.5"                 ,181276160, 17, "32"),
    DEVMOUSEE155    ("Developing Mouse, E15.5"                 ,181276151, 17, "32"),
    DEVMOUSEE135    ("Developing Mouse, E13.5"                 ,181276130, 17, "32"),
    DEVMOUSEPE115   ("Developing Mouse, E11.5"                 ,181275741, 17, "32,126768961"),
    HUMAN34G        ("Human, 34 years, Cortex - Gyral"         ,138322605, 16, "31,113753815,113753816,141667008"),
    HUMAN34M        ("Human, 34 years, Cortex - Mod. Brodmann" ,265297126, 16, "31,113753816,141667008,265297118"),
    HUMAN21         ("Human, 21 pcw"                           ,3        , 16, "31,113753816,141667008"),
    HUMAN21BS       ("Human, 21 pcw - Brainstem"               ,287730656, 16, "31,141667008"),
    HUMAN15         ("Human, 15 pcw"                           ,138322603, 16, "31,141667008"),
    HUMANBRAINAG    ("Human Brain RefVolAttribute Guide "            ,265297125, 16, "265297119,266932194,266932196,266932197");

    private String name;
    private Integer id;
    private Integer structure_graph_id;
    private String groupId;

    AtlasCatalog(String name, int id, int graph_id, String group) {
        this.name = name;
        this.id = id;
        this.structure_graph_id = graph_id;
        this.groupId = group;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getStructureGraphId() {
        return this.structure_graph_id;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getDonor() {
        String[] parts = name.split(", ");
        return parts[0];
    }

    public String getAge() {
        String[] parts = name.split(", ");
        return parts[1];
    }

    public String getPerspective() {
        String[] parts = name.split(", ");
        return parts[2];
    }

    public static AtlasCatalog getByName(String name) {
        for (AtlasCatalog atlas : AtlasCatalog.values()) {
            if (atlas.getName().equals(name)) {
                return atlas;
            }
        }
        return null;
    }

    public static String[] getNames() {
        String[] names = new String[AtlasCatalog.values().length];
        int i = 0;
        for (AtlasCatalog atlas : AtlasCatalog.values()) {
            names[i++] = atlas.getName();
        }

        return names;
    }
}
