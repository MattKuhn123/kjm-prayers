package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.greeting.Greeting;
import org.mlk.kjm.greeting.GreetingRequest;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PrayerServlet extends HttpServlet {
    private static final String directory = "prayers/";
	private static final String createPrayerHtml = directory + "CreatePrayer.html";

    private final PrayerRepository prayers;

    public PrayerServlet() {
        this(new PrayerRepositoryImpl());
    }

    public PrayerServlet(PrayerRepository prayers) {
        this.prayers = prayers;
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Document greetingDoc = getPrayerRequestDocument();
		String html = greetingDoc.html();
		resp.getWriter().append(html).flush();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            createPrayer(req);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | IOException e) {
            e.printStackTrace(resp.getWriter());
        }
    }

    private  Document getPrayerRequestDocument() throws IOException {
        Document doc = getHtmlDocument(createPrayerHtml);
        return doc;
    }

    private void createPrayer(HttpServletRequest req) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
        PrayerRequest postBody = getPostBody(req, PrayerRequest.class);
        Jail jail = new Jail(postBody.getCounty());
        Inmate inmate = new Inmate(postBody.getInmateFirstName(), postBody.getInmateLastName(), jail);
        Prayer prayer = new Prayer(inmate, postBody.getDate(), postBody.getPrayer());
        this.prayers.createPrayer(prayer);
	}
}
