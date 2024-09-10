package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.jsoup.nodes.Document;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CreatePrayerServlet extends HttpServlet {
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
        Document createPrayerDocument = getHtmlDocument(createPrayerHtml);
		String html = createPrayerDocument.html();
		resp.getWriter().append(html).flush();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            CreatePrayerRequest postBody = getPostBody(req, CreatePrayerRequest.class);
            Jail jail = new Jail(postBody.getCounty());
            Inmate inmate = new Inmate(postBody.getInmateFirstName(), postBody.getInmateLastName(), jail);
            Prayer prayer = new Prayer(inmate, postBody.getDate(), postBody.getPrayer());
            this.prayers.createPrayer(prayer);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | IOException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}
