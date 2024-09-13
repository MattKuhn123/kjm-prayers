package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.ApplicationPropertiesImpl;
import org.mlk.kjm.ServletUtils;
import org.mlk.kjm.prayers.PrayerRepository.OrderBy;

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
    public static final String countyId = "county";
    public static final String dateId = "date";
    public static final String viewId = "view";
    public static final String prayerId = "prayer";
    public static final String noResultId = "noResult";

    private static final String directory = "prayers/";

    public static final String createName = "/CreatePrayer";
    public static final String listName = "/ListPrayers";
    public static final String singleName = "/SinglePrayer";

    private static final String createFile = directory + createName + ".html";
    private static final String listFile = directory + listName + ".html";
    private static final String singleFile = directory + singleName + ".html";

    private static final String tableTag = "tale";
    private static final String tbodyTag = "tbody";
    private static final String trTag = "tr";

    private final ApplicationProperties props;
    private final PrayerRepository prayers;

    public PrayerServlet() {
        this(ApplicationPropertiesImpl.getInstance(),
                PrayerRepositoryImpl.getInstance(ApplicationPropertiesImpl.getInstance()));
    }

    public PrayerServlet(ApplicationProperties props, PrayerRepository prayers) {
        this.props = props;
        this.prayers = prayers;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (listName.equals(pathInfo)) {
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
            String prayerString = getRequiredFromPostBody(postBody, prayerId);

            Prayer prayer = new Prayer(inmateFirstName, inmateLastName, county, date, prayerString);
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
        Optional<String> queryOrderByString = getOptionalParameter(req, orderById);
        Optional<String> queryOrderByStringIsAsc = getOptionalParameter(req, orderByIsAscId);
        Optional<OrderBy> orderBy = getOrderBy(queryOrderByString);
        Optional<Boolean> orderByIsAsc = getOrderByIsAsc(queryOrderByStringIsAsc);
        int page = 0;
        int pageLength = 100;
        Optional<LocalDate> queryDate = queryDateString.isPresent()
                ? Optional.of(stringToDate(queryDateString.get()))
                : Optional.empty();

        List<Prayer> prayers = this.prayers.getPrayers(queryFirstName, queryLastName, queryCounty, queryDate, page,
                pageLength, orderBy, orderByIsAsc);

        Document prayerDocument = getHtmlDocument(listFile);
        prayerDocument.getElementById(inmateFirstNameId).val(queryFirstName.orElse(""));
        prayerDocument.getElementById(inmateLastNameId).val(queryLastName.orElse(""));
        prayerDocument.getElementById(countyId).text(queryCounty.orElse(""));
        prayerDocument.getElementById(dateId).text(queryDateString.orElse(""));

        
        if (prayers.size() == 0) {
            prayerDocument.selectFirst(tableTag).remove();
        } else {
            prayerDocument.select(noResultId).remove();
            Element tbody = prayerDocument.selectFirst(tbodyTag);
            for (Prayer prayer : prayers) {
                Element tr = tbody.selectFirst(trTag).clone();
                tr.getElementById(inmateFirstNameId).text(prayer.getFirstName());
                tr.getElementById(inmateLastNameId).text(prayer.getLastName());
                tr.getElementById(countyId).text(prayer.getCounty());
                tr.getElementById(dateId).text(ServletUtils.dateToString(prayer.getDate()));
    
                String attrKey = "hx-get";
                Map<String, String> params = new HashMap<String, String>();
                params.put(inmateFirstNameId, prayer.getFirstName());
                params.put(inmateLastNameId, prayer.getLastName());
                params.put(dateId, dateToString(prayer.getDate()));
    
                String attrValue = createLink(contextPath + singleName, params);
                tr.getElementById(viewId).attr(attrKey, attrValue);
    
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
        getPrayerDocument.getElementById(prayerId).text(prayer.get().getPrayer());
        return getPrayerDocument;
    }

    private Optional<OrderBy> getOrderBy(Optional<String> orderByString) {
        if (orderByString.isEmpty()) {
            return Optional.empty();
        }

        OrderBy orderBy = OrderBy.valueOf(orderByString.get());
        return Optional.of(orderBy);
    }

    private Optional<Boolean> getOrderByIsAsc(Optional<String> orderByIsAscString) {
        if (orderByIsAscString.isEmpty()) {
            boolean defaultValue = false;
            return Optional.of(defaultValue);
        }

        boolean orderByIsAsc = "on".equals(orderByIsAscString.get());
        return Optional.of(orderByIsAsc);
    }
}
