package org.mlk.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

public class MockHttpServletRequest implements HttpServletRequest {

    private final Map<String, Object> attributes;
    private final Map<String, String> parameters;
    private final HttpSession session;
    private final Cookie[] cookies;
    private final String method;
    private final String pathInfo;
    private final String contentType;

    public MockHttpServletRequest(
        String method,
        String pathInfo,
        String contentType) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), new Cookie[0], method, pathInfo, contentType);
    }

    public MockHttpServletRequest(
        Optional<Map<String, Object>> attributes,
        Optional<Map<String, String>> parameters,
        Optional<HttpSession> session,
        Cookie[] cookies,
        String method,
        String pathInfo,
        String contentType) {
        if (attributes.isPresent()) {
            this.attributes = attributes.get();
        } else {
            this.attributes = new HashMap<>();
        }

        if (parameters.isPresent()) {
            this.parameters = parameters.get();
        } else {
            this.parameters = new HashMap<>();
        }

        if (session.isPresent()) {
            this.session = session.get();
        } else {
            this.session = new MockHttpSession(new MockServletContext());
        }

        this.cookies = cookies;
        this.method = method;
        this.pathInfo = pathInfo;
        this.contentType = contentType;
    }
    
    @Override
    public Object getAttribute(String arg0) { return attributes.get(arg0); }

    @Override
    public Enumeration<String> getAttributeNames() { return Collections.enumeration(attributes.keySet()); }

    @Override
    public void removeAttribute(String arg0) { attributes.remove(arg0); }

    @Override
    public void setAttribute(String arg0, Object arg1) { attributes.put(arg0, arg1); }

    @Override
    public String getParameter(String arg0) { return this.parameters.get(arg0); }

    @Override
    public String getContentType() { return contentType; }

    @Override
    public Cookie[] getCookies() { return cookies; }

    @Override
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException { }

    @Override
    public HttpSession getSession() { return session != null ? session : new MockHttpSession(new MockServletContext(new HashMap<String, Object>())); }

    @Override
    public HttpSession getSession(boolean arg0) { return session != null ? session : arg0 ? new MockHttpSession(new MockServletContext(new HashMap<String, Object>())) : null; }

    @Override
    public ServletContext getServletContext() { return session.getServletContext();  }

    @Override
    public String getPathInfo() { return pathInfo; }
    
    @Override
    public ServletInputStream getInputStream() throws IOException { return null; }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0) { 
        return new RequestDispatcher() {
          public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            MockHttpServletResponse mock = (MockHttpServletResponse) res;
            mock.encodedRedirectUrl = arg0;
          }
          public void include(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException { throw new UnsupportedOperationException("Unimplemented method 'include'");}
      }; 
    }

    @Override
    public String getMethod() { return method; }

    // ===== Do not need to implement =====
    public AsyncContext getAsyncContext() { return null; }
    public Enumeration<String> getParameterNames() { return null; }
    public String[] getParameterValues(String arg0) { return null; }
    public Map<String, String[]> getParameterMap() { return null; }
    public String getCharacterEncoding() { return null; }
    public int getContentLength() { return 0; }
    public long getContentLengthLong() { return 0; }
    public DispatcherType getDispatcherType() { return null; }
    public String getLocalAddr() { return null; }
    public String getLocalName() { return null; }
    public int getLocalPort() { return 0; }
    public Locale getLocale() { return null; }
    public Enumeration<Locale> getLocales() { return null; }
    public String getProtocol() { return null; }
    public BufferedReader getReader() throws IOException { return null; }
    public String getRealPath(String arg0) { return null; }
    public String getRemoteAddr() { return null; }
    public String getRemoteHost() { return null; }
    public int getRemotePort() { return 0; }
    public String getScheme() { return null; }
    public String getServerName() { return null; }
    public int getServerPort() { return 0; }
    public boolean isAsyncStarted() { return false; }
    public boolean isAsyncSupported() { return false; }
    public boolean isSecure() { return false; }
    public AsyncContext startAsync() throws IllegalStateException { return null; }
    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException { return null; }
    public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException { return false; }
    public String changeSessionId() { return null; }
    public String getAuthType() { return null; }
    public String getContextPath() { return null; }
    public long getDateHeader(String arg0) { return 0; }
    public String getHeader(String arg0) { return null; }
    public Enumeration<String> getHeaderNames() { return null; }
    public Enumeration<String> getHeaders(String arg0) { return null; }
    public int getIntHeader(String arg0) { return 0; }
    public Part getPart(String arg0) throws IOException, ServletException {return null; }
    public Collection<Part> getParts() throws IOException, ServletException { return null; }
    public String getPathTranslated() { return null; }
    public String getQueryString() { return null; }
    public String getRemoteUser() { return null; }
    public String getRequestURI() { return null; }
    public StringBuffer getRequestURL() { return null; }
    public String getRequestedSessionId() { return null; }
    public String getServletPath() { return null; }
    public Principal getUserPrincipal() { return null; }
    public boolean isRequestedSessionIdFromCookie() { return false; }
    public boolean isRequestedSessionIdFromURL() { return false; }
    public boolean isRequestedSessionIdFromUrl() { return false; }
    public boolean isRequestedSessionIdValid() { return false; }
    public boolean isUserInRole(String arg0) { return false; }
    public void login(String arg0, String arg1) throws ServletException { }
    public void logout() throws ServletException { }
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException { return null; }
}
