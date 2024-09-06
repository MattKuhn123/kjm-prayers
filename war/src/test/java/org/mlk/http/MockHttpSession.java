package org.mlk.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

public class MockHttpSession implements HttpSession {

    public ServletContext servletContext;

    private final long creationTime;
    private final String id;

    public MockHttpSession(ServletContext servletContext) {
        this.servletContext = servletContext;
        creationTime = System.currentTimeMillis();
        id = UUID.randomUUID().toString();
    }

    @Override
    public Object getAttribute(String arg0) {
        return servletContext.getAttribute(arg0);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return servletContext.getAttributeNames();
    }

    @Override
    public void removeAttribute(String arg0) {
        servletContext.removeAttribute(arg0);
    }
    @Override
    public void setAttribute(String arg0, Object arg1) {
        servletContext.setAttribute(arg0, arg1);
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext; 
    }

    @Override
    public void invalidate() {
        servletContext = new MockServletContext(new HashMap<String, Object>());
    }

    // ===== Do not need to implement =====
    public long getCreationTime() { return creationTime; }
    public String getId() { return id; }
    public long getLastAccessedTime() { return 0; }
    public int getMaxInactiveInterval() { return 0; }
    public boolean isNew() { return false; }
    public void putValue(String arg0, Object arg1) { /* We don't use this, I don't think */ }

    @SuppressWarnings("deprecation")
    public jakarta.servlet.http.HttpSessionContext getSessionContext() { return null; }
    
    public Object getValue(String arg0) { /* We don't use this, I don't think */ return null; }
    public String[] getValueNames() { /* We don't use this, I don't think */ return null; }
    public void removeValue(String arg0) { /* We don't use this, I don't think */ }
    public void setMaxInactiveInterval(int arg0) { }
}
