package org.mlk.kjm.inmates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.shared.ApplicationPropertiesImpl;

import static org.mlk.kjm.shared.ServletUtils.*;

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
    private static final String editName = "/EditInmate";

    private static final String directory = "inmates/";

    private static final String listFile = directory + listName + ".html";
    private static final String singleFile = directory + singleName + ".html";
    private static final String editFile = directory + editName + ".html";

    public static final String contextPath = "/inmates";
    public static final String firstNameId = "first-name";
    public static final String lastNameId = "last-name";
    public static final String orderById = "order-by";
    public static final String orderByIsAscId = "order-by-is-asc";
    public static final String countyId = "county";
    public static final String isMaleId = "is-male";
    public static final String infoTextId = "info-text";
    public static final String viewId = "view";
    public static final String editId = "edit";
    public static final String noResultId = "no-result";
    public static final String pagesId = "pages";
    public static final String pageActionsId = "page-actions";

    public static final String newFirstNameId = "new-" + firstNameId;
    public static final String newLastNameId = "new-" + lastNameId;
    public static final String newCountyId = "new-" + countyId;
    public static final String newIsMaleId = "new-" + isMaleId;
    public static final String newInfoTextId = "new-" + infoTextId;

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

            if (editName.equals(pathInfo)) {
                Document resultListDocument = getInmateEditDocument(req);
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

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Optional<String>> postBody = getPostBodyMap(req);
            String firstName = getRequiredFromPostBody(postBody, firstNameId);
            String newFirstName = getRequiredFromPostBody(postBody, newFirstNameId);
            
            String lastName = getRequiredFromPostBody(postBody, lastNameId);
            String newLastName = getRequiredFromPostBody(postBody, newLastNameId);
            
            String county = getRequiredFromPostBody(postBody, countyId);
            String newCounty = getRequiredFromPostBody(postBody, newCountyId);
            
            Optional<String> newIsMaleString = getOptionalFromPostBody(postBody, newIsMaleId);
            Optional<Boolean> newIsMale = getIsMale(newIsMaleString);
            Optional<String> newInfoText = getOptionalFromPostBody(postBody, newInfoTextId);

            Optional<Inmate> currentInmate = this.inmates.getInmate(firstName, lastName, county);
            if (currentInmate.isEmpty()) {
                String reason = "This inmate does not exist and cannot be updated";
                throw new SQLException(reason);
            }

            Inmate newInmate = new Inmate(newFirstName, newLastName, newCounty, Optional.empty(), newIsMale, newInfoText);
            this.inmates.updateInmate(currentInmate.get(), newInmate);

            String successMessage = "<p>Success!</p>";
            resp.getWriter().append(successMessage).flush();
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
            inmatesDocument.getElementById(countyId).selectFirst("option[value='" + queryCounty.get()  + "']").attr(selectedAttr, trueVal);
        }
        
        if (isMale.isPresent()) {
            inmatesDocument.getElementById(isMaleId).selectFirst("option[value='" + queryIsMale.get()  + "']").attr(selectedAttr, trueVal);
        }

        if (queryOrderByIsAscIsPresent) {
            inmatesDocument.getElementById(orderByIsAscId).attr(checkedAttr, trueVal);
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
            inmatesDocument.getElementById(previousBtnId).attr(disabledAttr, trueVal);
        }
        
        if (page >= (pageCount - 1)) {
            inmatesDocument.getElementById(nextBtnId).attr(disabledAttr, trueVal);
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

                String hxGetViewValue = createLink(contextPath + singleName, params);
                tr.getElementById(viewId).attr(hxGetAttr, hxGetViewValue);

                String hxGetEditValue = createLink(contextPath + editName, params);
                tr.getElementById(editId).attr(hxGetAttr, hxGetEditValue);

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

        if (inmate.get().isMale().isPresent()) {
            String isMaleView = inmate.get().isMale().get() ? "Yes" : "No";
            inmateDocument.getElementById(isMaleId).text(isMaleView);
        }
        return inmateDocument;
    }

    private Document getInmateEditDocument(HttpServletRequest req) throws IOException, SQLException {
        String queryInmateFirstName = getRequiredParameter(req, firstNameId);
        String queryInmateLastName = getRequiredParameter(req, lastNameId);
        String queryCounty = getRequiredParameter(req, countyId);

        Optional<Inmate> inmate = this.inmates.getInmate(queryInmateFirstName, queryInmateLastName, queryCounty);
        if (inmate.isEmpty()) {
            String html = "<p>Inmate not found!</p>";
            Document empty = Jsoup.parse(html);
            return empty;
        }

        Document editDocument = getHtmlDocument(editFile);

        Map<String, String> params = new HashMap<String, String>();
        String hxPutEditValue = createLink(contextPath + singleName, params);
        editDocument.selectFirst(inputSubmit).attr(hxPutAttr, hxPutEditValue);

        editDocument.getElementById(firstNameId).val(inmate.get().getFirstName());
        editDocument.getElementById(newFirstNameId).val(inmate.get().getFirstName());

        editDocument.getElementById(lastNameId).val(inmate.get().getLastName());
        editDocument.getElementById(newLastNameId).val(inmate.get().getLastName());
        
        editDocument.getElementById(countyId).val(inmate.get().getCounty());
        editDocument.getElementById(newCountyId).selectFirst("option[value='" + inmate.get().getCounty()  + "']").attr(selectedAttr, trueVal);
        
        String isMaleValue = inmate.get().isMale().isPresent() 
            ? inmate.get().isMale().get().toString()
            : emptyString;

        String isMaleView = inmate.get().isMale().isPresent() 
            ? inmate.get().isMale().get() ? "Yes" : "No"
            : emptyString;
        editDocument.getElementById(isMaleId).val(isMaleView);
        editDocument.getElementById(newIsMaleId).selectFirst("option[value='" + isMaleValue  + "']").attr(selectedAttr, trueVal);
        
        String infoTxt = inmate.get().getInfo().isPresent() ? inmate.get().getInfo().get() : emptyString;
        editDocument.getElementById(infoTextId).val(infoTxt);
        editDocument.getElementById(newInfoTextId).val(infoTxt);
        return editDocument;
    }

    private Optional<Boolean> getIsMale(Optional<String> input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }

        String yes = "true";
        if (input.get().equals(yes)) {
            return Optional.of(true);
        }

        String no = "false";
        if (input.get().equals(no)) {
            return Optional.of(false);
        }

        return Optional.empty();
    }
}
