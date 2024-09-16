package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.ApplicationPropertiesImpl;
import org.mlk.kjm.ServletUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PrayerServlet extends HttpServlet {
    public static final String contextPath = "/prayers";
    public static final String inmateFirstNameId = "inmateFirstName";
    public static final String inmateLastNameId = "inmateLastName";
    public static final String orderById = "orderBy";
    public static final String orderByIsAscId = "orderByIsAsc";
    public static final String pageId = "page";
    public static final String countyId = "county";
    public static final String dateId = "date";
    public static final String viewId = "view";
    public static final String prayerTextId = "prayer-text";
    public static final String noResultId = "no-result";
    public static final String pagesId = "pages";
    public static final String pageActionsId = "page-actions";

    public static final String toPageParam = "toPage";

    public static final String defaultPage = "0";
    public static final int defaultPageLength = 5;

    private static final String directory = "prayers/";

    private static final String createName = "/CreatePrayer";
    private static final String listName = "/ListPrayers";
    private static final String listResetName = "/ListPrayers/Reset";
    private static final String listPreviousName = "/ListPrayers/Previous";
    private static final String listNextName = "/ListPrayers/Next";
    private static final String singleName = "/SinglePrayer";

    private static final String createFile = directory + createName + ".html";
    private static final String listFile = directory + listName + ".html";
    private static final String singleFile = directory + singleName + ".html";

    private static final String tableTag = "table";
    private static final String tbodyTag = "tbody";
    private static final String trTag = "tr";

    private static final String hxGetAttr = "hx-get";
    private static final String idAttr = "id";

    private static final String emptyString = "";

    private final ApplicationProperties props;
    private final PrayerRepository prayers;

    public PrayerServlet() {
        this(new ApplicationPropertiesImpl(), new PrayerRepositoryImpl(new ApplicationPropertiesImpl()));
    }

    public PrayerServlet(ApplicationProperties props, PrayerRepository prayers) {
        this.props = props;
        this.prayers = prayers;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null) {
                return;
            }

            if (pathInfo.startsWith(listName)) {
                Document resultListDocument = getPrayerListDocument(req);
                String html = resultListDocument.html();
                resp.getWriter().append(html).flush();
                return;
            }

            if (singleName.equals(pathInfo)) {
                Document resultListDocument = getPrayerSingleDocument(req);
                String html = resultListDocument.html();
                resp.getWriter().append(html).flush();
                return;
            }

            if (createName.equals(pathInfo)) {
                Document createPrayerDocument = getCreatePrayerDocument();
                String html = createPrayerDocument.html();
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

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Optional<String>> postBody = getPostBodyMap(req);
            String inmateFirstName = getRequiredFromPostBody(postBody, inmateFirstNameId);
            String inmateLastName = getRequiredFromPostBody(postBody, inmateLastNameId);
            String county = getRequiredFromPostBody(postBody, countyId);
            String dateString = getRequiredFromPostBody(postBody, dateId);
            LocalDate date = stringToDate(dateString);
            String prayerText = getRequiredFromPostBody(postBody, prayerTextId);

            Prayer prayer = new Prayer(inmateFirstName, inmateLastName, county, date, prayerText);
            this.prayers.createPrayer(prayer);

            String successMessage = "<p>Success!</p>";
            resp.getWriter().append(successMessage).flush();

        } catch (IllegalArgumentException e) {
            String failMessage = "<p>" + e.getMessage() + "</p>";
            resp.getWriter().append(failMessage).flush();
        } catch (SQLException se) {
            if (props.isProduction()) {
                String failMessage = "<p>Error! Please try again later</p>";
                resp.getWriter().append(failMessage).flush();
                return;
            }

            se.printStackTrace(resp.getWriter());
        }
    }

    private Document getCreatePrayerDocument() throws IOException {
        Document createPrayerDocument = getHtmlDocument(createFile);
        return createPrayerDocument;
    }

    private Document getPrayerListDocument(HttpServletRequest req) throws IOException, SQLException {
        Optional<String> queryFirstName = getOptionalParameter(req, inmateFirstNameId);
        Optional<String> queryLastName = getOptionalParameter(req, inmateLastNameId);
        Optional<String> queryCounty = getOptionalParameter(req, countyId);
        Optional<String> queryDateString = getOptionalParameter(req, dateId);
        Optional<String> orderBy = getOptionalParameter(req, orderById);
        boolean queryOrderByIsPresent = isParameterPresent(req, orderByIsAscId);
        Optional<Boolean> orderByIsAsc = Optional.of(queryOrderByIsPresent);
        
        int page = getPage(req);
        Optional<LocalDate> queryDate = queryDateString.isPresent()
                ? Optional.of(stringToDate(queryDateString.get()))
                : Optional.empty();

        List<Prayer> prayers = this.prayers.getPrayers(queryFirstName, queryLastName, queryCounty, queryDate, page,
                defaultPageLength, orderBy, orderByIsAsc);

        Document prayerDocument = getHtmlDocument(listFile);
        prayerDocument.getElementById(inmateFirstNameId).val(queryFirstName.orElse(emptyString));
        prayerDocument.getElementById(inmateLastNameId).val(queryLastName.orElse(emptyString));
        prayerDocument.getElementById(countyId).val(queryCounty.orElse(emptyString));
        if (queryCounty.isPresent()) {
            prayerDocument.getElementById(countyId).selectFirst("option[value='" + queryCounty.get()  + "']").attr("selected", "true");
        }

        prayerDocument.getElementById(dateId).val(queryDateString.orElse(emptyString));
        if (orderBy.isPresent()) {
            prayerDocument.getElementById(orderById).selectFirst("option[value='" + orderBy.get()  + "']").attr("selected", "true");
        }

        if (queryOrderByIsPresent) {
            prayerDocument.getElementById(orderByIsAscId).attr("checked", "true");
        }

        int totalResults = this.prayers.getCount(queryFirstName, queryLastName, queryCounty, queryDate);
        int pageCount = (int) Math.ceil((double) totalResults / (double) defaultPageLength);

        Element pageActionsElement = prayerDocument.getElementById(pageActionsId);
        for (int i = 0; i < pageCount; i++) {
            int displayIndex = i + 1;
            String classes = i == page ? "btn btn-secondary" : "btn btn-link";
            String btn = "<input type='submit' class='" + classes + "' value='" + displayIndex + "' formaction='/prayers/ListPrayers?" + toPageParam + "=" + i + "'/>";
            pageActionsElement.append(btn);
        }

        if (page <= 0) {
            prayerDocument.getElementById("previous-btn").attr("disabled", "true");
        }
        
        if (page >= (pageCount - 1)) {
            prayerDocument.getElementById("next-btn").attr("disabled", "true");
        }

        prayerDocument.getElementById(pageId).val(String.valueOf(page));
        if (prayers.size() == 0) {
            prayerDocument.selectFirst(tableTag).remove();
            prayerDocument.getElementById(pagesId).remove();
        } else {
            prayerDocument.getElementById(noResultId).remove();
            Element tbody = prayerDocument.selectFirst(tbodyTag);
            for (Prayer prayer : prayers) {
                Element tr = tbody.selectFirst(trTag).clone();

                Element inmateFirstNameElement = tr.getElementById(inmateFirstNameId);
                inmateFirstNameElement.text(prayer.getFirstName());
                inmateFirstNameElement.removeAttr(idAttr);

                Element inmateLastNameElement = tr.getElementById(inmateLastNameId);
                inmateLastNameElement.text(prayer.getLastName());
                inmateLastNameElement.removeAttr(idAttr);

                Element countyElement = tr.getElementById(countyId);
                countyElement.text(prayer.getCounty());
                countyElement.removeAttr(idAttr);

                Element dateElement = tr.getElementById(dateId);
                dateElement.text(ServletUtils.dateToString(prayer.getDate()));
                dateElement.removeAttr(idAttr);

                Map<String, String> params = new HashMap<String, String>();
                params.put(inmateFirstNameId, prayer.getFirstName());
                params.put(inmateLastNameId, prayer.getLastName());
                params.put(dateId, dateToString(prayer.getDate()));

                String hxGetValue = createLink(contextPath + singleName, params);
                tr.getElementById(viewId).attr(hxGetAttr, hxGetValue);

                tbody.appendChild(tr);
            }

            // The existing tr was there as a template.
            // Now, it has to be removed.
            int first = 0;
            tbody.children().remove(first);
        }

        return prayerDocument;
    }

    private Document getPrayerSingleDocument(HttpServletRequest req) throws IOException, SQLException {
        String queryInmateFirstName = getRequiredParameter(req, inmateFirstNameId);
        String queryInmateLastName = getRequiredParameter(req, inmateLastNameId);
        String queryDateString = getRequiredParameter(req, dateId);
        LocalDate queryDate = stringToDate(queryDateString);

        Optional<Prayer> prayer = this.prayers.getPrayer(queryInmateFirstName, queryInmateLastName, queryDate);
        if (prayer.isEmpty()) {
            String html = "<p>Prayer not found!</p>";
            Document empty = Jsoup.parse(html);
            return empty;
        }

        Document getPrayerDocument = getHtmlDocument(singleFile);
        getPrayerDocument.getElementById(inmateFirstNameId).text(prayer.get().getFirstName());
        getPrayerDocument.getElementById(inmateLastNameId).text(prayer.get().getLastName());
        getPrayerDocument.getElementById(dateId).text(dateToString(prayer.get().getDate()));
        getPrayerDocument.getElementById(prayerTextId).text(prayer.get().getPrayer());
        return getPrayerDocument;
    }

    private int getPage(HttpServletRequest req) {
        if (isToPage(req)) {
            int result = getToPage(req);
            return result;
        }

        if (isPageReset(req.getPathInfo())) {
            return 0;
        }

        try {
            String page = getOptionalParameter(req, pageId).orElse(defaultPage);
            int result = Integer.parseInt(page) + getPageDirection(req.getPathInfo());
            return result;
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean isPageReset(String pathInfo) {
        boolean result = listResetName.equals(pathInfo);
        return result;
    }

    private int getToPage(HttpServletRequest req) {
        try {
            Optional<String> toPage = getOptionalParameter(req, toPageParam);
            int result = Integer.parseInt(toPage.get());
            return result;
        } catch (UnsupportedEncodingException e) {
            return -1;
        }
    }

    private boolean isToPage(HttpServletRequest req) {
        try {
            Optional<String> toPage = getOptionalParameter(req, toPageParam);
            return toPage.isPresent();
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    private int getPageDirection(String pathInfo) {
        if (listPreviousName.equals(pathInfo)) {
            return -1;
        }

        if (listNextName.equals(pathInfo)) {
            return 1;
        }

        return 0;
    }
}
