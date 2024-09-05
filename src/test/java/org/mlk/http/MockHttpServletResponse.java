package org.mlk.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class MockHttpServletResponse implements HttpServletResponse {

    private final Map<String, String> headers;

    public String contentType;
    public String characterEncoding;
    public int contentLength;
    public String redirectUrl;
    public String encodedUrl;
    public String encodedRedirectUrl;
    public String sendRedirect;
    public int errorInt;
    public String errorString;

    public PrintWriter pw;
    public MockServletOutputStream os;

    public MockHttpServletResponse() {
        os = new MockServletOutputStream();
        pw = new PrintWriter(os);
        headers = new HashMap<String, String>();
    }

    public String getResAsString() {
        MockServletOutputStream os = null;
        try {
            os = getOutputStream();
            return new String(os.real.toByteArray());
        } catch (Exception e) {
            return null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public String getContentType() { return contentType; }
    
    @Override
    public MockServletOutputStream getOutputStream() throws IOException {
        return os;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return pw;
    }
    
    @Override
    public void addHeader(String arg0, String arg1) { headers.put(arg0, arg1); }
    @Override
    public boolean containsHeader(String arg0) { return headers.containsKey(arg0); }
    @Override
    public void setHeader(String arg0, String arg1) { headers.put(arg0, arg1); }
    @Override
    public void setContentLength(int arg0) { contentLength = arg0; }
    @Override
    public void setCharacterEncoding(String arg0) { characterEncoding = arg0; }
    @Override
    public void setContentType(String arg0) { contentType = arg0; }
    @Override
    public String getHeader(String arg0) { return headers.get(arg0); }
    @Override
    public String encodeRedirectURL(String arg0) {  return encodeRedirectUrl(arg0); }
    @Override
    public String encodeRedirectUrl(String arg0) { return encodedRedirectUrl = arg0; }
    @Override
    public String encodeURL(String arg0) { return encodeUrl(arg0); }
    @Override
    public String encodeUrl(String arg0) { return encodedUrl = arg0; }
    @Override
    public Collection<String> getHeaderNames() { return headers.keySet(); }
    @Override
    public Collection<String> getHeaders(String arg0) { return headers.values(); }
    @Override
    public void sendError(int arg0) throws IOException { errorInt = arg0; }
    @Override
    public void sendError(int arg0, String arg1) throws IOException { errorInt = arg0; errorString = arg1; }
    @Override
    public void sendRedirect(String arg0) throws IOException { sendRedirect = arg0; }

    // ===== Do not need to implement =====
    public void flushBuffer() throws IOException { }
    public int getBufferSize() { return 0; }
    public String getCharacterEncoding() { return null; }
    public Locale getLocale() { return null; }
    public boolean isCommitted() { return false; }
    public void reset() { }
    public void resetBuffer() { }
    public void setBufferSize(int arg0) { }
    public void setContentLengthLong(long arg0) { }
    public void setLocale(Locale arg0) { }
    public void addCookie(Cookie arg0) { }
    public void addDateHeader(String arg0, long arg1) { }
    public void addIntHeader(String arg0, int arg1) { }
    public void setDateHeader(String arg0, long arg1) { }
    public void setIntHeader(String arg0, int arg1) { }
    public void setStatus(int arg0) { }
    public void setStatus(int arg0, String arg1) { }
    public int getStatus() { return 0; }
    
}
