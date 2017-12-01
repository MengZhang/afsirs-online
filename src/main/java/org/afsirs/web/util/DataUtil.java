package org.afsirs.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import static org.afsirs.web.Main.LOG;

/**
 *
 * @author Meng Zhang
 */
public class DataUtil {

    // save uploaded file to new location
    public static void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        revisePath(uploadedFileLocation);
        File dir = new File(uploadedFileLocation);
        LOG.info("File was wrote to {} ...", dir.getAbsolutePath());
        try (OutputStream out = new FileOutputStream(dir)) {
            
            int read;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            LOG.info("File was wrote to {} done!", dir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public HashMap<String, Object> toMap(Object data) throws JsonProcessingException, IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        String reqJson = mapper.writeValueAsString(data);
        HashMap<String, Object> ret = mapper.readValue(reqJson, HashMap.class);
        return ret;
    }
    
    public static String revisePath(String path) {
        if (!path.trim().equals("")) {
            File f = new File(path);
            if (!f.isDirectory()) {
                f = f.getParentFile();
                path = f.getPath();
            }
            if (!f.exists()) {
                f.mkdirs();
            }
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
        }
        return path;
    }
}
