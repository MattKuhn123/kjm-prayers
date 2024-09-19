package org.mlk.kjm.shared;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthFilter implements Filter {

    private final String cookieName = "TEST_COOKIE";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            doFilter((HttpServletRequest) req, (HttpServletResponse) resp, chain);
        }
    }

    private void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        Optional<String> getCookie = getCookie(req.getCookies(), cookieName);

        if (getCookie.isPresent()) {
            System.out.println("Cookie is present!");
            System.out.println(getCookie.get());
        } else {
            System.out.println("Cookie not present!");
        }

        Cookie cookie = createCookie();
        resp.addCookie(cookie);
        chain.doFilter(req, resp);
    }

    private Cookie createCookie() {
        Long value = Instant.now().toEpochMilli();
        Cookie result = new Cookie(cookieName, String.valueOf(value));
        boolean isHttpOnly = true;
        result.setHttpOnly(isHttpOnly);

        boolean isSecure = true;
        result.setSecure(isSecure);
        return result;
    }

    private Optional<String> getCookie(Cookie[] cookies, final String searchFor) {
        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (searchFor.equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}
