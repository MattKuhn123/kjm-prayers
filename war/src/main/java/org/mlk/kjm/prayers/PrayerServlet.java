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
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PrayerServlet extends HttpServlet {
    public static final String contextPath = "/prayers";
    public static final String inmateFirstNameId = "inmateFirstName";
    public static final String inmateLastNameId = "inmateLastName";
    public static final String countyId = "county";
    public static final String dateId = "date";
    public static final String viewId = "view";
    public static final String prayerId = "prayer";
    
    private static final String directory = "prayers/";
	
    public static final String createName = "/CreatePrayer";
    public static final String listName = "/ListPrayers";
    public static final String queryName = "/QueryPrayers";
    public static final String singleName = "/SinglePrayer";

    private static final String createFile = directory + createName + ".html";
    private static final String listFile = directory + listName + ".html";
    private static final String queryFile = directory + queryName + ".html";
    private static final String singleFile = directory + singleName + ".html";
    
    private static final String tbodyTag = "tbody";
    private static final String trTag = "tr";

    private final PrayerRepository prayers;

    public PrayerServlet() {
        this(PrayerRepositoryImpl.getInstance());
    }

    public PrayerServlet(PrayerRepository prayers) {
        this.prayers = prayers;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (queryName.equals(pathInfo)) {
            Document queryPrayersDocument = getQueryPrayersDocument();
            String html = queryPrayersDocument.html();
            resp.getWriter().append(html).flush();
            return;
        }
        
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

        throw new IllegalArgumentException("Invalid request!");
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

            Jail jail = new Jail(county);
            Inmate inmate = new Inmate(inmateFirstName, inmateLastName, jail);
            Prayer prayer = new Prayer(inmate, date, prayerString);
            this.prayers.createPrayer(prayer);

            String successMessage = "<p>Success!</p>";
            resp.getWriter().append(successMessage).flush();

        } catch (IllegalArgumentException e) {
            String failMessage = "<p>" + e.getMessage() + "</p>";
            resp.getWriter().append(failMessage).flush();
        }
    }

    private Document getQueryPrayersDocument() throws IOException {
        Document queryPrayersDocument = getHtmlDocument(queryFile);
        return queryPrayersDocument;
    }

    private Document getCreatePrayerDocument() throws IOException {
        Document createPrayerDocument = getHtmlDocument(createFile);
        return createPrayerDocument;
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

        Document getPrayersDocument = getHtmlDocument(listFile);
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

            String attrValue = createLink(contextPath + singleName, params);
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

        Document getPrayerDocument = getHtmlDocument(singleFile);
        getPrayerDocument.getElementById(inmateFirstNameId).text(prayer.get().getInmate().getFirstName());
        getPrayerDocument.getElementById(inmateLastNameId).text(prayer.get().getInmate().getLastName());
        getPrayerDocument.getElementById(dateId).text(dateToString(prayer.get().getDate()));
        getPrayerDocument.getElementById(prayerId).text(prayer.get().getPrayer());
        return getPrayerDocument;
    }
}
