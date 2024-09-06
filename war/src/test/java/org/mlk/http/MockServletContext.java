package org.mlk.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.FilterRegistration.Dynamic;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

public class MockServletContext implements ServletContext {

    private final Map<String, Object> attributes;
    public MockServletContext() {
        this.attributes = new HashMap<String, Object>();
    }

    public MockServletContext(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public InputStream getResourceAsStream(String arg0) {
        try {
            return getClass().getClassLoader().getResource(arg0).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Beats me!!
        return null;
    }

    @Override
    public Object getAttribute(String arg0) { return attributes.get(arg0); }

    @Override
    public Enumeration<String> getAttributeNames() { return Collections.enumeration(attributes.keySet()); }

    @Override
    public void removeAttribute(String arg0) { attributes.remove(arg0); }

    @Override
    public void setAttribute(String arg0, Object arg1) { attributes.put(arg0, arg1); }

    // ===== Do not need to implement =====
    public URL getResource(String arg0) throws MalformedURLException { return null; }
    public Dynamic addFilter(String arg0, String arg1) { return null; }
    public Dynamic addFilter(String arg0, Filter arg1) { return null; }
    public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) { return null; }
    public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String arg0, String arg1) { return null; }
    public void addListener(String arg0) { }
    public <T extends EventListener> void addListener(T arg0) { }
    public void addListener(Class<? extends EventListener> arg0) { }
    public jakarta.servlet.ServletRegistration.Dynamic addServlet(String arg0, String arg1) { return null; }
    public jakarta.servlet.ServletRegistration.Dynamic addServlet(String arg0, Servlet arg1) { return null; }
    public jakarta.servlet.ServletRegistration.Dynamic addServlet(String arg0, Class<? extends Servlet> arg1) { return null; }
    public <T extends Filter> T createFilter(Class<T> arg0) throws ServletException { return null; }
    public <T extends EventListener> T createListener(Class<T> arg0) throws ServletException { return null; }
    public <T extends Servlet> T createServlet(Class<T> arg0) throws ServletException { return null; }
    public void declareRoles(String... arg0) { }
    public ClassLoader getClassLoader() { return null; }
    public ServletContext getContext(String arg0) { return null; }
    public String getContextPath() { return null; }
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() { return null; }
    public int getEffectiveMajorVersion() { return 0; }
    public int getEffectiveMinorVersion() { return 0; }
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() { return null; }
    public FilterRegistration getFilterRegistration(String arg0) { return null; }
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() { return null; }
    public String getInitParameter(String arg0) { return null; }
    public Enumeration<String> getInitParameterNames() { return null; }
    public JspConfigDescriptor getJspConfigDescriptor() { return null; }
    public int getMajorVersion() { return 0; }
    public String getMimeType(String arg0) { return null; }
    public int getMinorVersion() { return 0; }
    public RequestDispatcher getNamedDispatcher(String arg0) { return null; }
    public String getRealPath(String arg0) { return null; }
    public String getRequestCharacterEncoding() { return null; }
    public RequestDispatcher getRequestDispatcher(String arg0) { return null; }
    public Set<String> getResourcePaths(String arg0) { return null; }
    public String getResponseCharacterEncoding() { return null; }
    public String getServerInfo() { return null; }
    public Servlet getServlet(String arg0) throws ServletException { return null; }
    public String getServletContextName() { return null; }
    public Enumeration<String> getServletNames() { return null; }
    public ServletRegistration getServletRegistration(String arg0) { return null; }
    public Map<String, ? extends ServletRegistration> getServletRegistrations() { return null; }
    public Enumeration<Servlet> getServlets() { return null; }
    public SessionCookieConfig getSessionCookieConfig() { return null; }
    public int getSessionTimeout() { return 0; }
    public String getVirtualServerName() { return null; }
    public void log(String arg0) { }
    public void log(Exception arg0, String arg1) { }
    public void log(String arg0, Throwable arg1) { }
    public boolean setInitParameter(String arg0, String arg1) { return false; }
    public void setRequestCharacterEncoding(String arg0) { }
    public void setResponseCharacterEncoding(String arg0) { }
    public void setSessionTimeout(int arg0) { }
    public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) { }
}
