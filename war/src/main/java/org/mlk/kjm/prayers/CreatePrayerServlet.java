package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CreatePrayerServlet extends HttpServlet {
    public static final String inmateFirstNameId = "inmateFirstName";
    public static final String inmateLastNameId = "inmateLastName";
    public static final String countyId = "county";
    public static final String dateId = "date";
    public static final String prayerId = "prayer";
    public static final String requestForDocument = "/document";
    
    private static final String directory = "prayers/";
	private static final String createPrayerHtml = directory + "CreatePrayer.html";

    private final PrayerRepository prayers;

    public CreatePrayerServlet() {
        this(PrayerRepositoryImpl.getInstance());
    }

    public CreatePrayerServlet(PrayerRepository prayers) {
        this.prayers = prayers;
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (requestForDocument.equals(pathInfo)) {
            Document createPrayerDocument = getHtmlDocument(createPrayerHtml);
            String html = createPrayerDocument.html();
            resp.getWriter().append(html).flush();
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
}
