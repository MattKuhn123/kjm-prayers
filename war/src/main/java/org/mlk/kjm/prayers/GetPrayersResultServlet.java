package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mlk.kjm.ServletUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetPrayersResultServlet extends HttpServlet {
    private static final String directory = "prayers/";
	private static final String getPrayersHtml = directory + "GetPrayersResult.html";
	private static final String getPrayersResultEmptyHtml = directory + "GetPrayersResultEmpty.html";
    private static final String tbodyTag = "tbody";
    private static final String trTag = "tr";

    private static enum GetPrayersTr {
        inmateFirstName,
        inmateLastName,
        county,
        date,
        view
    }

    private final PrayerRepository prayers;

    public GetPrayersResultServlet() {
        this(PrayerRepositoryImpl.getInstance());
    }

    public GetPrayersResultServlet(PrayerRepository prayers) {
        this.prayers = prayers;
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO : Get query parameters

        List<Prayer> prayers = this.prayers.getPrayers();
        Document doc = (prayers.size() > 0) 
            ? getPrayersDocument(prayers) 
            : getPrayersEmptyDocument();

        String html = doc.html();
        resp.getWriter().append(html).flush();
    }

    private Document getPrayersDocument(List<Prayer> prayers) throws IOException {
        Document getPrayersDocument = getHtmlDocument(getPrayersHtml);
        Element tbody = getPrayersDocument.selectFirst(tbodyTag);
        for (Prayer prayer : prayers) {
            Element tr = tbody.selectFirst(trTag).clone();
            tr.getElementById(GetPrayersTr.inmateFirstName.toString()).text(prayer.getInmate().getFirstName());
            tr.getElementById(GetPrayersTr.inmateLastName.toString()).text(prayer.getInmate().getLastName());
            tr.getElementById(GetPrayersTr.county.toString()).text(prayer.getInmate().getJail().getCounty());
            tr.getElementById(GetPrayersTr.date.toString()).text(ServletUtils.dateToString(prayer.getDate()));
			
            // TODO : Link to detail view
            
            tbody.appendChild(tr);
        }

        // The existing tr was there as a template.
        // Now, it has to be removed.
        int first = 0;
        tbody.children().remove(first);

        return getPrayersDocument;
    }

    private Document getPrayersEmptyDocument() throws IOException {
        Document getPrayersEmptyDocument = getHtmlDocument(getPrayersResultEmptyHtml);
        return getPrayersEmptyDocument;
    }
}
