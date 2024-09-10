package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;

import org.jsoup.nodes.Document;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetPrayersRequestServlet extends HttpServlet {
    private static final String directory = "prayers/";
	private static final String getPrayersRequestHtml = directory + "GetPrayersRequest.html";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Document doc = getHtmlDocument(getPrayersRequestHtml);
        String html = doc.html();
        resp.getWriter().append(html).flush();
    }
}
