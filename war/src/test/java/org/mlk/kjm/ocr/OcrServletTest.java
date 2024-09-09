package org.mlk.kjm.ocr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.mlk.http.MockHttpServletRequest;
import org.mlk.http.MockHttpServletResponse;

import jakarta.servlet.http.Part;
import junit.framework.TestCase;

public class OcrServletTest extends TestCase {
    
    @Test
    public void test_Something() throws Exception {
        String method = "POST";
        String partName = OcrServlet.filePartName;

        MockHttpServletRequest req = new MockHttpServletRequest(method, partName, getPart());
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        OcrServlet servlet = new OcrServlet();
        servlet.service(req, resp);

        String resAsString = resp.getResponseAsString();
        assertNotNull(resAsString);
    }

    private static Part getPart() {
        return new Part() {
            public InputStream getInputStream() throws IOException {
                String filePath = "/prayer.jpg";
                InputStream inputStream = OcrServiceTest.class.getResourceAsStream(filePath);
                return inputStream;
            }

            public String getContentType() { return "img/jpg"; }
            public String getName() { return "prayer.jpg"; }
            public String getSubmittedFileName() { return "prayer.jpg"; }
            public long getSize() { return -1; }
            public void write(String fileName) throws IOException { }
            public void delete() throws IOException { }
            public String getHeader(String name) { return ""; }
            public Collection<String> getHeaders(String name) { return new ArrayList<String>(); }
            public Collection<String> getHeaderNames() { return new ArrayList<String>(); }
        };
    }
}
