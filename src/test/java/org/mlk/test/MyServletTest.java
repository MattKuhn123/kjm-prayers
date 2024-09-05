package org.mlk.test;

import static org.junit.Assert.assertThrows;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mlk.MyServlet;
import org.mlk.ServletHtmlUtils;
import org.mlk.http.MockHttpServletRequest;
import org.mlk.http.MockHttpServletResponse;

import junit.framework.TestCase;

import static org.mlk.ServletHtmlUtils.*;

public class MyServletTest extends TestCase {

    @Test
    public void test_Get_MyServlet_ExpectTitle_ExpectAnchor() throws Exception {
        String method = "GET";
        String contentType = "text/html";
        
        MockHttpServletRequest req = new MockHttpServletRequest(method, contentType);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        MyServlet servlet = new MyServlet();
        servlet.service(req, resp);

        String resAsString = resp.getResponseAsString();
        Document actualDoc = Jsoup.parse(resAsString);
        
        // Html
        String actualLang = actualDoc.select(htmlTag).attr(langAttr);
        assertEquals(langAttrVal, actualLang);

        // Head
        Element actualHead = actualDoc.selectFirst(headTag);
        int expectedHeadChildrenCount = 1;
        assertEquals(expectedHeadChildrenCount, actualHead.children().size());
        int titleChildIndex = 0;
        assertTrue(actualHead.child(titleChildIndex).is(titleTag));
        
        // Title
        Element actualTitle = actualDoc.selectFirst(titleTag);
        assertEquals(MyServlet.title, actualTitle.text());
        
        // Body
        Element actualBody = actualDoc.selectFirst(bodyTag);
        int expectedBodyChildrenCount = 1;
        assertEquals(expectedBodyChildrenCount, actualBody.children().size());
        int anchorChildIndex = 0;
        assertTrue(actualBody.child(anchorChildIndex).is(anchorTag));
        
        // Anchor
        Element actualAnchor = actualDoc.selectFirst(anchorTag);
        assertEquals(MyServlet.anchorText, actualAnchor.text());
        assertEquals(MyServlet.anchorHref, actualAnchor.attr(hrefAttr));
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
