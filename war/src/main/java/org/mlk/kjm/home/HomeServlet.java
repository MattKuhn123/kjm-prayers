package org.mlk.kjm.home;

import static org.mlk.kjm.shared.ServletUtils.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jsoup.nodes.Document;
import org.mlk.kjm.auth.AuthUtils;
import org.mlk.kjm.auth.AuthCodeSender;
import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.shared.ApplicationPropertiesImpl;
import org.mlk.kjm.users.AuthToken;
import org.mlk.kjm.users.UserRepository;
import org.mlk.kjm.users.UserRepositoryImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HomeServlet extends HttpServlet {
    public static int daysToExpires = 7;
    private static final String directory = "home/";

    private static final String homeName = "/Home";
    private static final String loginName = "/Login";

    private static final String homeFile = directory + homeName + ".html";
    private static final String loginFile = directory + loginName + ".html";

    public static final String emailId = "email";
    public static final String codeId = "code";

    public static final String loginMessageId = "login-msg";

    private static final String newCode = "/NewCode";
    private static final String useCode = "/UseCode";

    private final UserRepository users;
    private final AuthCodeSender authCodeSender;
    private final ApplicationProperties props;
    public HomeServlet() {
        this(new UserRepositoryImpl(new ApplicationPropertiesImpl()), 
            new AuthCodeSender(new ApplicationPropertiesImpl()), 
            new ApplicationPropertiesImpl());
    }

    public HomeServlet(UserRepository users, AuthCodeSender authCodeSender, ApplicationProperties props) {
        this.users = users;
        this.authCodeSender = authCodeSender;
        this.props = props;
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<AuthToken> authToken = getAuthToken(req);
        boolean isAuthTokenValid;
        try {
            isAuthTokenValid = AuthUtils.isAuthTokenValid(authToken, users);
        } catch (SQLException e) {
            if (props.isProduction()) {
                String msg = "Something went wrong!";
                resp.getWriter().append(msg);
            } else {
                e.printStackTrace();
                e.printStackTrace(resp.getWriter());
            }

            return;
        }

        if (isAuthTokenValid) {
            Document homeDocument = getHomeDocument();
            String html = homeDocument.html();
            resp.getWriter().append(html).flush();
        } else {
            String loginMessage = "Welcome! Please generate a token to continue.";
            Document loginDocument = getLoginDocument(loginMessage);
            String html = loginDocument.html();
            resp.getWriter().append(html).flush();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (newCode.equals(pathInfo)) {
            Map<String, Optional<String>> postBody = getPostBodyMap(req);
            String queryEmail = getRequiredFromPostBody(postBody, emailId);
            UUID code = UUID.randomUUID();
            LocalDate expires = LocalDate.now().plusDays(daysToExpires);

            AuthToken authToken = new AuthToken(expires, code, queryEmail);

            try {
                users.setAuthToken(authToken);
            } catch (SQLException e) {
                if (props.isProduction()) {
                    String msg = "Something went wrong!";
                    resp.getWriter().append(msg).flush();
                } else {
                    e.printStackTrace();
                    e.printStackTrace(resp.getWriter());
                }

                return;
            }

            authCodeSender.sendAuthCode(authToken);
            String msg = "Email sent! (Check your junk folder)";
            resp.getWriter().append(msg).flush();
        } else if (useCode.equals(pathInfo)){
            Map<String, Optional<String>> postBody = getPostBodyMap(req);
            String email = getRequiredFromPostBody(postBody, emailId);
            String code = getRequiredFromPostBody(postBody, codeId);
            Optional<AuthToken> validAuthToken = Optional.empty();
            try {
                validAuthToken = AuthUtils.isAuthTokenValid(email, code, users);
            } catch (SQLException e) {
                if (props.isProduction()) {
                    String loginMessage = "Something is wrong! Try again.";
                    Document loginDocument = getLoginDocument(loginMessage);
                    String html = loginDocument.html();
                    resp.getWriter().append(html).flush();
                } else {
                    e.printStackTrace();
                    e.printStackTrace(resp.getWriter());
                }

                return;
            }

            if (validAuthToken.isPresent()) {
                setAuthCookie(resp, validAuthToken.get());
                
                Document homeDocument = getHomeDocument();
                String html = homeDocument.html();
                resp.getWriter().append(html).flush();
            } else {
                String loginMessage = "Something is wrong! Try again.";
                Document loginDocument = getLoginDocument(loginMessage);
                String html = loginDocument.html();
                resp.getWriter().append(html).flush();
            }
        }
    }

    private Document getHomeDocument() throws IOException {
        Document homeDocument = getHtmlDocument(homeFile);
        return homeDocument;
    }

    private Document getLoginDocument(String loginMessage) throws IOException {
        Document loginDocument = getHtmlDocument(loginFile);
        loginDocument.getElementById(loginMessageId).text(loginMessage);
        return loginDocument;
    }
}
