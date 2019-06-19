package file;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class to handle the image files from the Allen API
 *
 * @author Felix Meyenhofer
 */
public class AllenImage extends AllenFile {
    /**
     * {@inheritDoc}
     */
    public AllenImage(URL url, File file) throws IOException, TransformerException, URISyntaxException {
        super(url, file);
    }

    /**
     * {@inheritDoc}
     */
    public AllenImage(File file) throws IOException, URISyntaxException {
        super(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void load(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
//        System.out.println("content length: " + connection.getContentLength());

        InputStream is = connection.getInputStream();
        OutputStream os = new FileOutputStream(getFile());

        byte[] b = new byte[65536];
        int len;
        while ((len = is.read(b)) != -1) {
            os.write(b, 0, len);
        }
        is.close();
        os.close();
        connection.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void load(File file) {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void save() {
        // not used
    }
}
