package org.mlk.kjm.inmates;

import static org.mlk.kjm.ServletUtils.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.ApplicationPropertiesImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InmateServlet extends HttpServlet {

    private static final String listName = "/ListInmates";
    private static final String singleName = "/SingleInmate";

    private static final String directory = "inmates/";

    private static final String listFile = directory + listName + ".html";
    private static final String singleFile = directory + singleName + ".html";

    public static final String contextPath = "/inmates";
    public static final String firstNameId = "first-name";
    public static final String lastNameId = "last-name";
    public static final String orderById = "order-by";
    public static final String orderByIsAscId = "order-by-is-asc";
    public static final String countyId = "county";
    public static final String isMaleId = "is-male";
    public static final String infoTextId = "info-text";
    public static final String viewId = "view";
    public static final String noResultId = "no-result";
    public static final String pagesId = "pages";
    public static final String pageActionsId = "page-actions";

    private final InmateRepository inmates;
    private final ApplicationProperties props;

    public InmateServlet() {
        this(new ApplicationPropertiesImpl(), new InmateRepositoryImpl(new ApplicationPropertiesImpl()));
    }

    public InmateServlet(ApplicationProperties appProps, InmateRepository inmates) {
        this.props = appProps;
        this.inmates = inmates;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null) {
                return;
            }

            if (pathInfo.startsWith(listName)) {
                Document resultListDocument = getInmateListDocument(req);
                String html = resultListDocument.html();
                resp.getWriter().append(html).flush();
                return;
            }

            if (singleName.equals(pathInfo)) {
                Document resultListDocument = getInmateSingleDocument(req);
                String html = resultListDocument.html();
                resp.getWriter().append(html).flush();
                return;
            }
        } catch (SQLException se) {
            if (props.isProduction()) {
                String failMessage = "<p>Error! Please try again later</p>";
                resp.getWriter().append(failMessage).flush();
                return;
            }

            se.printStackTrace(resp.getWriter());
        }
    }

    private Document getInmateListDocument(HttpServletRequest req) throws IOException, SQLException {
        Optional<String> queryFirstName = getOptionalParameter(req, firstNameId);
        Optional<String> queryLastName = getOptionalParameter(req, lastNameId);
        Optional<String> queryCounty = getOptionalParameter(req, countyId);
        Optional<String> queryIsMale = getOptionalParameter(req, isMaleId);
        Optional<Boolean> isMale = getIsMale(queryIsMale);
        Optional<String> orderBy = getOptionalParameter(req, orderById);
        boolean queryOrderByIsAscIsPresent = isParameterPresent(req, orderByIsAscId);
        Optional<Boolean> orderByIsAsc = Optional.of(queryOrderByIsAscIsPresent);
        
        int page = getPage(req);

        List<Inmate> inmates = this.inmates.getInmates(queryFirstName, queryLastName, queryCounty, Optional.empty(), isMale, page,
                defaultPageLength, orderBy, orderByIsAsc);

        Document inmatesDocument = getHtmlDocument(listFile);
        inmatesDocument.getElementById(firstNameId).val(queryFirstName.orElse(emptyString));
        inmatesDocument.getElementById(lastNameId).val(queryLastName.orElse(emptyString));
        inmatesDocument.getElementById(countyId).val(queryCounty.orElse(emptyString));
        if (queryCounty.isPresent()) {
            inmatesDocument.getElementById(countyId).selectFirst("option[value='" + queryCounty.get()  + "']").attr("selected", "true");
        }
        
        if (isMale.isPresent()) {
            inmatesDocument.getElementById(isMaleId).selectFirst("option[value='" + queryIsMale.get()  + "']").attr("selected", "true");
        }

        if (queryOrderByIsAscIsPresent) {
            inmatesDocument.getElementById(orderByIsAscId).attr("checked", "true");
        }

        int totalResults = this.inmates.getCount(queryFirstName, queryLastName, queryCounty, Optional.empty(), isMale);
        int pageCount = (int) Math.ceil((double) totalResults / (double) defaultPageLength);

        Element pageActionsElement = inmatesDocument.getElementById(pageActionsId);
        for (int i = 0; i < pageCount; i++) {
            int displayIndex = i + 1;
            String classes = i == page ? "btn btn-secondary" : "btn btn-link";
            String btn = "<input type='submit' class='" + classes + " btn-small' value='" + displayIndex + "' formaction='/inmates/ListInmates?" + toPageParam + "=" + i + "'/>";
            pageActionsElement.append(btn);
        }

        if (page <= 0) {
            inmatesDocument.getElementById("previous-btn").attr("disabled", "true");
        }
        
        if (page >= (pageCount - 1)) {
            inmatesDocument.getElementById("next-btn").attr("disabled", "true");
        }

        inmatesDocument.getElementById(pageId).val(String.valueOf(page));
        if (inmates.size() == 0) {
            inmatesDocument.selectFirst(tableTag).remove();
            inmatesDocument.getElementById(pagesId).remove();
        } else {
            inmatesDocument.getElementById(noResultId).remove();
            Element tbody = inmatesDocument.selectFirst(tbodyTag);
            for (Inmate inmate : inmates) {
                Element tr = tbody.selectFirst(trTag).clone();

                Element firstNameElement = tr.getElementById(firstNameId);
                firstNameElement.text(inmate.getFirstName());
                firstNameElement.removeAttr(idAttr);

                Element lastNameElement = tr.getElementById(lastNameId);
                lastNameElement.text(inmate.getLastName());
                lastNameElement.removeAttr(idAttr);

                Element countyElement = tr.getElementById(countyId);
                countyElement.text(inmate.getCounty());
                countyElement.removeAttr(idAttr);

                Map<String, String> params = new HashMap<String, String>();
                params.put(firstNameId, inmate.getFirstName());
                params.put(lastNameId, inmate.getLastName());
                params.put(countyId, inmate.getCounty());

                String hxGetValue = createLink(contextPath + singleName, params);
                tr.getElementById(viewId).attr(hxGetAttr, hxGetValue);

                tbody.appendChild(tr);
            }

            // The existing tr was there as a template.
            // Now, it has to be removed.
            int first = 0;
            tbody.children().remove(first);
        }

        return inmatesDocument;
    }

    private Document getInmateSingleDocument(HttpServletRequest req) throws IOException, SQLException {
        String queryInmateFirstName = getRequiredParameter(req, firstNameId);
        String queryInmateLastName = getRequiredParameter(req, lastNameId);
        String queryCounty = getRequiredParameter(req, countyId);

        Optional<Inmate> inmate = this.inmates.getInmate(queryInmateFirstName, queryInmateLastName, queryCounty);
        if (inmate.isEmpty()) {
            String html = "<p>Inmate not found!</p>";
            Document empty = Jsoup.parse(html);
            return empty;
        }

        Document inmateDocument = getHtmlDocument(singleFile);
        inmateDocument.getElementById(firstNameId).text(inmate.get().getFirstName());
        inmateDocument.getElementById(lastNameId).text(inmate.get().getLastName());
        inmateDocument.getElementById(countyId).text(inmate.get().getCounty());
        inmateDocument.getElementById(infoTextId).text(inmate.get().getInfo().orElse(emptyString));
        return inmateDocument;
    }

    private Optional<Boolean> getIsMale(Optional<String> input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }

        String yes = "Yes";
        if (input.get().equals(yes)) {
            return Optional.of(true);
        }

        String no = "No";
        if (input.get().equals(no)) {
            return Optional.of(false);
        }

        return Optional.empty();
    }
}
