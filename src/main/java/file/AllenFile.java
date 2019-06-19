package file;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Base class for file handling.
 * The constructors allow to do the most common task during
 * the instantiation of the class (usually load or save the data).
 * The procedures are here implemented in the constructors. Subclasses
 * need then to implement the load and save methods.
 *
 * @author Felix Meyenhofer
 */
abstract class AllenFile {

    private enum Origin {
        LOCAL,
        REMOTE
    }


    /** Origin of the file */
    private Origin origin;

    /** Allen API URL to fetch an image file from the RESTful service */
    private URL url;

    /** Local copy of the image file */
    private File file;


    /**
     * Constructor to fetch a file from the Allen RESTful client and store
     * a local copy of it.
     *
     * @param url Allen API URL to fetch an image
     * @param file path to the local copy of the image
     */
    AllenFile(URL url, File file) throws TransformerException, IOException, URISyntaxException {

        setUrl(url);
        setFile(file);
        setOrigin(Origin.REMOTE);
        load(url);

        if (file != null) {
            save();
        }
    }

    /**
     * Load a local copy of an image File
     *
     * @param file path to the local copy of the image file
     */
    AllenFile(File file) throws IOException, URISyntaxException {
        setFile(file);
        setOrigin(Origin.LOCAL);
        load(file);
    }

    /**
     * Empty constructor. Does not provide any of the functionality.
     */
    AllenFile() {}

    /**
     * Load local copy {@link AllenFile#file}
     * respectively
     *
     * @param file path to the local file
     */
    abstract void load(File file) throws IOException, URISyntaxException;

    /**
     * Download the content using {@link AllenFile#url}
     *
     * @param url remote resource
     */
    abstract void load(URL url) throws IOException, TransformerException, URISyntaxException;

    /**
     * Save the downloaded (from {@link AllenFile#url} content to
     * the local file {@link AllenFile@file}
     */
    abstract void save() throws TransformerException, IOException;

    /**
     * Get the status message
     *
     * @return message
     */
    public String getStatusMessage() {
        if (isNew()) {
            return "Downloaded " + getUrl() + "\n... and cached the file as " + getFile();
        } else {
            return "Local file " + getFile();
        }
    }

    private void setOrigin(Origin src) {
        this.origin = src;
    }

    boolean isNew() {
        return this.origin == Origin.REMOTE;
    }

    private void setUrl(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    protected void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
