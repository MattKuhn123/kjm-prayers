package org.mlk.kjm.greeting;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mlk.http.MockHttpServletRequest;
import org.mlk.http.MockHttpServletResponse;

import junit.framework.TestCase;

public class GreetingServletTest extends TestCase {
	public static final String pTag = "p";

    @Test
    public void test_Get_GreetingServlet() throws Exception {
        String method = "GET";
        String contentType = "text/html";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, contentType);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        GreetingServlet servlet = new GreetingServlet();
        servlet.service(req, resp);

        String resAsString = resp.getResponseAsString();
        Document actualDoc = Jsoup.parse(resAsString);
        
        Element actualP = actualDoc.selectFirst(pTag);
        String expectedPText = "Hello, World!!";
        assertEquals(expectedPText, actualP.text());
    }
}
