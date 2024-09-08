package org.mlk.kjm;

import static org.junit.Assert.assertThrows;
import static org.mlk.kjm.ServletHtmlUtils.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mlk.http.MockHttpServletRequest;
import org.mlk.http.MockHttpServletResponse;

import junit.framework.TestCase;

public class MyServletTest extends TestCase {
	public static final String pTag = "p";

    @Test
    public void test_Get_MyServlet() throws Exception {
        String method = "GET";
        String contentType = "text/html";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, contentType);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        MyServlet servlet = new MyServlet();
        servlet.service(req, resp);

        String resAsString = resp.getResponseAsString();
        Document actualDoc = Jsoup.parse(resAsString);
        
        Element actualP = actualDoc.selectFirst(pTag);
        String expectedPText = "Hello, World!!";
        assertEquals(expectedPText, actualP.text());
    }

    @Test
    public void test_Get_MyServlet_ApplicationJson_ExpectError() throws Exception {
        String method = "GET";
        String contentType = "application/json";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, contentType);
        MockHttpServletResponse res = new MockHttpServletResponse();
        
        MyServlet servlet = new MyServlet();
        Exception e = assertThrows(Exception.class, () -> servlet.service(req, res));
        assertEquals(e.getMessage(), getContentTypeError().getMessage());
    }
}
