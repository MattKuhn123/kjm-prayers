package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.ServletUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetPrayersServlet extends HttpServlet {
    public static final String contextPath = "/getPrayers";
    public static final String inmateFirstNameId = "inmateFirstName";
    public static final String inmateLastNameId = "inmateLastName";
    public static final String countyId = "county";
    public static final String dateId = "date";
    public static final String viewId = "view";
    public static final String prayerId = "prayer";
    
    private static final String directory = "prayers/";
	private static final String getPrayersQueryHtml = directory + "GetPrayersQuery.html";
    private static final String getPrayersResultTableHtml = directory + "GetPrayersResultTable.html";
    private static final String getPrayersResultCardHtml = directory + "GetPrayersResultCard.html";
    
    private static final String tbodyTag = "tbody";
    private static final String trTag = "tr";

    public static final String requestForDocument = "/document";
    public static final String requestForList = "/list";
    public static final String requestForSingle = "/single";

    private final PrayerRepository prayers;

    public GetPrayersServlet() {
        this(PrayerRepositoryImpl.getInstance());
    }

    public GetPrayersServlet(PrayerRepository prayers) {
        this.prayers = prayers;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (requestForDocument.equals(pathInfo)) {
            Document queryDocument = getQueryDocument();
            String html = queryDocument.html();
            resp.getWriter().append(html).flush();
            return;
        } else if (requestForList.equals(pathInfo)) {
            Document resultListDocument = getPrayerListDocument(req);
            String html = resultListDocument.html();
            resp.getWriter().append(html).flush();
            return;
        } else if (requestForSingle.equals(pathInfo)) {
            Document resultListDocument = getPrayerSingleDocument(req);
            String html = resultListDocument.html();
            resp.getWriter().append(html).flush();
            return;
        }

        throw new IllegalArgumentException("Invalid request!");
    }

    private Document getQueryDocument() throws IOException {
        Document doc = getHtmlDocument(getPrayersQueryHtml);
        return doc;
    }

    private Document getPrayerListDocument(HttpServletRequest req) throws IOException {
        Optional<String> queryFirstName = getOptionalParameter(req, inmateFirstNameId);
        Optional<String> queryLastName = getOptionalParameter(req, inmateLastNameId);
        Optional<String> queryCounty = getOptionalParameter(req, countyId);
        Optional<String> queryDateString = getOptionalParameter(req, dateId);
        Optional<LocalDate> queryDate = queryDateString.isPresent() 
            ? Optional.of(stringToDate(queryDateString.get())) 
            : Optional.empty();

        List<Prayer> prayers = this.prayers.getPrayers(queryFirstName, queryLastName, queryCounty, queryDate);
        if (prayers.size() == 0) {
            String html = "<p>No prayers found!</p>";
            Document empty = Jsoup.parse(html);
            return empty;
        }

        Document getPrayersDocument = getHtmlDocument(getPrayersResultTableHtml);
        Element tbody = getPrayersDocument.selectFirst(tbodyTag);
        for (Prayer prayer : prayers) {
            Element tr = tbody.selectFirst(trTag).clone();
            tr.getElementById(inmateFirstNameId).text(prayer.getInmate().getFirstName());
            tr.getElementById(inmateLastNameId).text(prayer.getInmate().getLastName());
            tr.getElementById(countyId).text(prayer.getInmate().getJail().getCounty());
            tr.getElementById(dateId).text(ServletUtils.dateToString(prayer.getDate()));
            
            String attrKey = "hx-get";
            Map<String, String> params = new HashMap<String, String>();
            params.put(inmateFirstNameId, prayer.getInmate().getFirstName());
            params.put(inmateLastNameId, prayer.getInmate().getLastName());
            params.put(dateId, dateToString(prayer.getDate()));

            String attrValue = createLink(contextPath + requestForSingle, params);
            tr.getElementById(viewId).attr(attrKey, attrValue);
            
            tbody.appendChild(tr);
        }

        // The existing tr was there as a template.
        // Now, it has to be removed.
        int first = 0;
        tbody.children().remove(first);

        return getPrayersDocument;
    }

    private Document getPrayerSingleDocument(HttpServletRequest req) throws IOException {
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

        Document getPrayerDocument = getHtmlDocument(getPrayersResultCardHtml);
        getPrayerDocument.getElementById(inmateFirstNameId).text(prayer.get().getInmate().getFirstName());
        getPrayerDocument.getElementById(inmateLastNameId).text(prayer.get().getInmate().getLastName());
        getPrayerDocument.getElementById(dateId).text(dateToString(prayer.get().getDate()));
        getPrayerDocument.getElementById(prayerId).text(prayer.get().getPrayer());
        return getPrayerDocument;
    }
}
