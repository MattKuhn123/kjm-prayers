package org.mlk.kjm.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamUtils {

    public static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        
        return buffer.toByteArray();
    }
}
