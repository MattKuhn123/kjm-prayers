package org.mlk.kjm.greeting;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mlk.http.MockHttpServletRequest;
import org.mlk.http.MockHttpServletResponse;

import junit.framework.TestCase;

public class GreetingServletTest extends TestCase {

    @Test
    public void test_Get_GreetingServlet() throws Exception {
        String method = "GET";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        GreetingServlet servlet = new GreetingServlet();
        servlet.service(req, resp);

        String resAsString = resp.getResponseAsString();
        Document actualDoc = Jsoup.parse(resAsString);
        
        String ul = "ul";
        Element actualUl = actualDoc.selectFirst(ul);
        int expectedChildren = 0;
        assertEquals(expectedChildren, actualUl.childrenSize());
    }
}
