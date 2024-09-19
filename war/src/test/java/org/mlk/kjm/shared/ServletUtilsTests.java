package org.mlk.kjm.shared;

import java.util.Map;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class ServletUtilsTests extends TestCase{
    
    @Test
    public void test_createLink_easy() throws Exception {
        String contextPath = "myContextPath";

        // LinkedHashMap maintains insertion order, makes it easier for us to test
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("k1", "v1");
        params.put("k2", "v2");
        params.put("k3", "v3");
        
        String expected = "/myContextPath?k1=v1&k2=v2&k3=v3";
        String actual = ServletUtils.createLink(contextPath, params);
        
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void test_createLink_hard() throws Exception {
        String contextPath = "myContextPath";
        // LinkedHashMap maintains insertion order, makes it easier for us to test
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("first-name", "Matt");
        params.put("last-name", "Kuhn");
        params.put("date", "01/01/1990");

        String expected = "/myContextPath?first-name=Matt&last-name=Kuhn&date=01%2F01%2F1990";
        String actual = ServletUtils.createLink(contextPath, params);

        Assert.assertEquals(expected, actual);
    }
}
