package org.mlk;

import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mlk.ServletHtmlUtils.*;

public class MyServlet extends HttpServlet {
	public static final String title = "MLK Demo";
	public static final String anchorText = "Home";
	public static final String anchorHref = "/";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!isRequestingHtml(req)) {
			throw getContentTypeError();
		}

		Document doc = getHtmlResponseDocument();
		String html = doc.html();
		resp.getWriter().append(html).flush();
	}

	private static Document getHtmlResponseDocument() {
		Document doc = createDocument();
		doc = addTitle(doc, title);
		doc = addAnchor(doc, anchorText, anchorHref);
		return doc;
	}

	private static Document addTitle(Document doc, String text) {
		Element title = createTitle(text);
		doc.head().appendChild(title);
		return doc;
	}

	private static Document addAnchor(Document doc, String text, String href) {
		Element anchor = createAnchor(text, href);
		doc.body().appendChild(anchor);
		return doc;
	}
}
