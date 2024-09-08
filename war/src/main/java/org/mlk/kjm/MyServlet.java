package org.mlk.kjm;

import static org.mlk.kjm.ServletHtmlUtils.*;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
	public static final String htmlResource = "MyServlet.html";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!isRequestingHtml(req)) {
			throw getContentTypeError();
		}

		Document doc = getHtmlDocument(htmlResource);
		String html = doc.html();
		resp.getWriter().append(html).flush();
	}
}
