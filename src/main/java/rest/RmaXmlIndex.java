package rest;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Felix Meyenhofer
 */
class RmaXmlIndex {

    private File file;
    private HashMap<String, String> index;


    RmaXmlIndex(File file) throws IOException {
        this.file = file;

        if (!file.exists()) {
            new BufferedWriter(new FileWriter(file)).close();
        }

        load();
    }

    private void load() throws FileNotFoundException {
        index = new HashMap<>();
        Scanner reader = new Scanner(file);
        while (reader.hasNext()) {
            String line = reader.nextLine();
            String[] parts = line.split("\t");
            index.put(parts[0].trim(), parts[1].trim());
        }
    }

    String getRmaQuery(String xmlFilename) {
        for (String key : index.keySet()) {
            String value = index.get(key).trim();
            if (value.equals(xmlFilename)) {
                return value;
            }
        }

        return null;
    }

    String getXmlFilename(String rma) throws IOException {
        if (index.keySet().contains(rma)) {
            return index.get(rma);
        }

        return addEntry(rma);
    }

    private String addEntry(String rma) throws IOException {
        String xml = AllenAPI.RMA.url2filename(rma);

        FileWriter writer = new FileWriter(file, true);
        BufferedWriter buffer = new BufferedWriter(writer);
        PrintWriter printer = new PrintWriter(buffer);
        printer.println(rma + "\t" + xml);
        printer.close();

        index.put(rma, xml);

        return xml;
    }
}
