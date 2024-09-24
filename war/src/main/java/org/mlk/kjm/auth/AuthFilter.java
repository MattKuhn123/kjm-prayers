package org.mlk.kjm.auth;

import static org.mlk.kjm.shared.ServletUtils.getAuthToken;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.mlk.kjm.shared.ApplicationPropertiesImpl;
import org.mlk.kjm.users.AuthToken;
import org.mlk.kjm.users.UserRepository;
import org.mlk.kjm.users.UserRepositoryImpl;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthFilter implements Filter {

    private final UserRepository users;
    public AuthFilter() {
        this(new UserRepositoryImpl(new ApplicationPropertiesImpl()));
    }

    public AuthFilter(UserRepository users) {
        this.users = users;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            doFilter((HttpServletRequest) req, (HttpServletResponse) resp, chain);
        }
    }

    private void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        Optional<AuthToken> authToken = getAuthToken(req);
        boolean isAuthTokenValid = false;
        try {
            isAuthTokenValid = AuthUtils.isAuthTokenValid(authToken, users);
        } catch (SQLException e) {
        }
        
        if (isAuthTokenValid) {
            chain.doFilter(req, resp);
        }
    }

}
