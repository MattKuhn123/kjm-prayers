package org.mlk.test;

import static org.junit.Assert.assertThrows;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mlk.MyServlet;
import org.mlk.http.MockHttpServletRequest;
import org.mlk.http.MockHttpServletResponse;

import junit.framework.TestCase;

public class MyServletTest extends TestCase {

    @Test
    public void test_Get_MyServlet_ExpectTitle_ExpectAnchor() throws Exception {
        String method = "GET";
        String pathInfo = "/MyServlet";
        String contentType = "text/html";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, pathInfo, contentType);
        MockHttpServletResponse res = new MockHttpServletResponse();
        
        MyServlet servlet = new MyServlet();
        servlet.service(req, res);

        String resAsString = res.getResAsString();
        Document doc = Jsoup.parse(resAsString);
        
        // Html
        String expectedLang = "en";
        String actualLang = doc.select(MyServlet.htmlTag).attr(MyServlet.langAttr);
        assertEquals(expectedLang, actualLang);

        // Head
        Element actualHead = doc.selectFirst(MyServlet.headTag);
        assertNotNull(actualHead);
        assertTrue(actualHead.child(0).is(MyServlet.titleTag));
        
        // Title
        Element actualTitle = doc.selectFirst(MyServlet.titleTag);
        assertNotNull(actualTitle);
        assertEquals(actualTitle.text(), MyServlet.title);
        
        // Body
        Element actualBody = doc.selectFirst(MyServlet.bodyTag);
        assertNotNull(actualBody);
        assertTrue(actualBody.child(0).is(MyServlet.anchorTag));
        
        // Anchor
        Element actualAnchor = doc.selectFirst(MyServlet.anchorTag);
        assertNotNull(actualAnchor);
        assertEquals(actualAnchor.text(), MyServlet.anchorText);
        assertEquals(actualAnchor.attr(MyServlet.hrefAttr), MyServlet.anchorHref);
    }

    @Test
    public void test_Get_MyServlet_ApplicationJson_ExpectError() throws Exception {
        String method = "GET";
        String pathInfo = "/MyServlet";
        String contentType = "application/json";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, pathInfo, contentType);
        MockHttpServletResponse res = new MockHttpServletResponse();
        
        MyServlet servlet = new MyServlet();
        Exception e = assertThrows(Exception.class, () -> servlet.service(req, res));
        assertEquals(e.getMessage(), MyServlet.getContentTypeError().getMessage());
    }
}
