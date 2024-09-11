package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.jsoup.nodes.Document;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetPrayerServlet extends HttpServlet {
    private static final String directory = "prayers/";
	private static final String getPrayerHtml = directory + "GetPrayer.html";

    public static final String contextPath = "/getPrayer";
    public static final String inmateFirstNameParam = "inmateFirstName";
    public static final String inmateLastNameParam = "inmateLastName";
    public static final String dateParam = "date";

    public static final String inmateFirstNameId = "inmateFirstName";
    public static final String inmateLastNameId = "inmateLastName";
    public static final String dateId = "date";
    public static final String prayerId = "prayer";

    private final PrayerRepository prayers;

    public GetPrayerServlet() {
        this(PrayerRepositoryImpl.getInstance());
    }

    public GetPrayerServlet(PrayerRepository prayers) {
        this.prayers = prayers;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryInmateFirstName = getRequiredParameter(req, inmateFirstNameParam);
        String queryInmateLastName = getRequiredParameter(req, inmateLastNameParam);
        String queryDateString = getRequiredParameter(req, dateParam);
        LocalDate queryDate = stringToDate(queryDateString);

        Optional<Prayer> prayer = this.prayers.getPrayer(queryInmateFirstName, queryInmateLastName, queryDate);
        Document doc = prayer.isPresent()
            ? getPrayerDocument(prayer.get())
            : getPrayerNotFoundDocument(); // TODO
        
        String html = doc.html();
        resp.getWriter().append(html).flush();
    }

    private Document getPrayerDocument(Prayer prayer) throws IOException {
        Document getPrayerDocument = getHtmlDocument(getPrayerHtml);

        getPrayerDocument.getElementById(inmateFirstNameId).text(prayer.getInmate().getFirstName());
        getPrayerDocument.getElementById(inmateLastNameId).text(prayer.getInmate().getLastName());
        getPrayerDocument.getElementById(dateId).text(dateToString(prayer.getDate()));
        getPrayerDocument.getElementById(prayerId).text(prayer.getPrayer());

        return getPrayerDocument;
    }

    private Document getPrayerNotFoundDocument() {
        // TODO
        return null;
    }
}
