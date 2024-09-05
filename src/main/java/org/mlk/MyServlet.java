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

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw getRequestMethodError();
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw getRequestMethodError();
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw getRequestMethodError();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!respondWithHtml(req)) {
			throw getContentTypeError();
		}

		String html = getHTMLResponseDocument();
		resp.getWriter().append(html).flush();
	}

	private static String getHTMLResponseDocument() {
		Document doc = createDocument();
		doc = addTitle(doc, title);
		doc = addAnchor(doc, anchorText, anchorHref);
		return doc.html();
	}

	private static Document addTitle(Document doc, String title) {
		Element element = doc.selectFirst(headTag);
		element.appendElement(titleTag).text(title);
		return doc;
	}

	private static Document addAnchor(Document doc, String text, String href) {
		Element element = doc.selectFirst(bodyTag);
		element.appendElement(anchorTag).text(text).attr(hrefAttr, href);
		return doc;
	}
}
