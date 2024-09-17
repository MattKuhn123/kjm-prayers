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
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.inmates.InmateRepository;
import org.mlk.kjm.inmates.InmateRepositoryImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PrayerServlet extends HttpServlet {
    public static final String contextPath = "/prayers";
    public static final String firstNameId = "first-name";
    public static final String lastNameId = "last-name";
    public static final String orderById = "order-by";
    public static final String orderByIsAscId = "order-by-is-asc";
    public static final String countyId = "county";
    public static final String dateId = "date";
    public static final String viewId = "view";
    public static final String prayerTextId = "prayer-text";
    public static final String noResultId = "no-result";

    private static final String directory = "prayers/";

    private static final String createName = "/CreatePrayer";
    private static final String listName = "/ListPrayers";
    private static final String singleName = "/SinglePrayer";

    private static final String createFile = directory + createName + ".html";
    private static final String listFile = directory + listName + ".html";
    private static final String singleFile = directory + singleName + ".html";

    private final ApplicationProperties props;
    private final PrayerRepository prayers;
    private final InmateRepository inmates;

    public PrayerServlet() {
        this(new ApplicationPropertiesImpl(), new PrayerRepositoryImpl(new ApplicationPropertiesImpl()), new InmateRepositoryImpl(new ApplicationPropertiesImpl()));
    }

    public PrayerServlet(ApplicationProperties props, PrayerRepository prayers, InmateRepository inmates) {
        this.props = props;
        this.prayers = prayers;
        this.inmates = inmates;
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
            String firstName = getRequiredFromPostBody(postBody, firstNameId);
            String lastName = getRequiredFromPostBody(postBody, lastNameId);
            String county = getRequiredFromPostBody(postBody, countyId);
            String dateString = getRequiredFromPostBody(postBody, dateId);
            LocalDate date = stringToDate(dateString);
            String prayerText = getRequiredFromPostBody(postBody, prayerTextId);

            Optional<Inmate> inmate = this.inmates.getInmate(firstName, lastName, county);
            if (inmate.isEmpty()) {
                Inmate newInmate = new Inmate(firstName, lastName, county, Optional.empty(), Optional.empty(), Optional.empty());
                this.inmates.createInmate(newInmate);
            }

            Prayer prayer = new Prayer(firstName, lastName, county, date, prayerText);
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
        Optional<String> queryFirstName = getOptionalParameter(req, firstNameId);
        Optional<String> queryLastName = getOptionalParameter(req, lastNameId);
        Optional<String> queryCounty = getOptionalParameter(req, countyId);
        Optional<String> queryDateString = getOptionalParameter(req, dateId);
        Optional<String> orderBy = getOptionalParameter(req, orderById);
        boolean queryOrderByIsAscIsPresent = isParameterPresent(req, orderByIsAscId);
        Optional<Boolean> orderByIsAsc = Optional.of(queryOrderByIsAscIsPresent);
        
        int page = getPage(req);
        Optional<LocalDate> queryDate = queryDateString.isPresent()
                ? Optional.of(stringToDate(queryDateString.get()))
                : Optional.empty();

        List<Prayer> prayers = this.prayers.getPrayers(queryFirstName, queryLastName, queryCounty, queryDate, page,
                defaultPageLength, orderBy, orderByIsAsc);

        Document prayersDocument = getHtmlDocument(listFile);
        prayersDocument.getElementById(firstNameId).val(queryFirstName.orElse(emptyString));
        prayersDocument.getElementById(lastNameId).val(queryLastName.orElse(emptyString));
        prayersDocument.getElementById(countyId).val(queryCounty.orElse(emptyString));
        if (queryCounty.isPresent()) {
            prayersDocument.getElementById(countyId).selectFirst("option[value='" + queryCounty.get()  + "']").attr(selectedAttr, trueVal);
        }

        prayersDocument.getElementById(dateId).val(queryDateString.orElse(emptyString));
        if (orderBy.isPresent()) {
            prayersDocument.getElementById(orderById).selectFirst("option[value='" + orderBy.get()  + "']").attr(selectedAttr, trueVal);
        }

        if (queryOrderByIsAscIsPresent) {
            prayersDocument.getElementById(orderByIsAscId).attr(checkedAttr, trueVal);
        }

        int totalResults = this.prayers.getCount(queryFirstName, queryLastName, queryCounty, queryDate);
        int pageCount = (int) Math.ceil((double) totalResults / (double) defaultPageLength);

        Element pageActionsElement = prayersDocument.getElementById(pageActionsId);
        for (int i = 0; i < pageCount; i++) {
            int displayIndex = i + 1;
            String classes = i == page ? "btn btn-secondary" : "btn btn-link";
            String btn = "<input type='submit' class='" + classes + " btn-small' value='" + displayIndex + "' formaction='/prayers/ListPrayers?" + toPageParam + "=" + i + "'/>";
            pageActionsElement.append(btn);
        }

        if (page <= 0) {
            prayersDocument.getElementById(previousBtnId).attr(disabledAttr, trueVal);
        }
        
        if (page >= (pageCount - 1)) {
            prayersDocument.getElementById(nextBtnId).attr(disabledAttr, trueVal);
        }

        prayersDocument.getElementById(pageId).val(String.valueOf(page));
        if (prayers.size() == 0) {
            prayersDocument.selectFirst(tableTag).remove();
            prayersDocument.getElementById(pagesId).remove();
        } else {
            prayersDocument.getElementById(noResultId).remove();
            Element tbody = prayersDocument.selectFirst(tbodyTag);
            for (Prayer prayer : prayers) {
                Element tr = tbody.selectFirst(trTag).clone();

                Element firstNameElement = tr.getElementById(firstNameId);
                firstNameElement.text(prayer.getFirstName());
                firstNameElement.removeAttr(idAttr);

                Element lastNameElement = tr.getElementById(lastNameId);
                lastNameElement.text(prayer.getLastName());
                lastNameElement.removeAttr(idAttr);

                Element countyElement = tr.getElementById(countyId);
                countyElement.text(prayer.getCounty());
                countyElement.removeAttr(idAttr);

                Element dateElement = tr.getElementById(dateId);
                dateElement.text(ServletUtils.dateToString(prayer.getDate()));
                dateElement.removeAttr(idAttr);

                Map<String, String> params = new HashMap<String, String>();
                params.put(firstNameId, prayer.getFirstName());
                params.put(lastNameId, prayer.getLastName());
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

        return prayersDocument;
    }

    private Document getPrayerSingleDocument(HttpServletRequest req) throws IOException, SQLException {
        String queryInmateFirstName = getRequiredParameter(req, firstNameId);
        String queryInmateLastName = getRequiredParameter(req, lastNameId);
        String queryDateString = getRequiredParameter(req, dateId);
        LocalDate queryDate = stringToDate(queryDateString);

        Optional<Prayer> prayer = this.prayers.getPrayer(queryInmateFirstName, queryInmateLastName, queryDate);
        if (prayer.isEmpty()) {
            String html = "<p>Prayer not found!</p>";
            Document empty = Jsoup.parse(html);
            return empty;
        }

        Document prayerDocument = getHtmlDocument(singleFile);
        prayerDocument.getElementById(firstNameId).text(prayer.get().getFirstName());
        prayerDocument.getElementById(lastNameId).text(prayer.get().getLastName());
        prayerDocument.getElementById(dateId).text(dateToString(prayer.get().getDate()));
        prayerDocument.getElementById(prayerTextId).text(prayer.get().getPrayer());
        return prayerDocument;
    }
}
