package org.mlk;

import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
	public static final String htmlTag = "html";
	public static final String hrefAttr = "href";
	public static final String langAttr = "lang";
	public static final String headTag = "head";
	public static final String bodyTag = "body";
	public static final String titleTag = "title";
	public static final String anchorTag = "a";
	
	public static final String title = "MLK Demo";
	public static final String langAttrVal = "en";
	public static final String anchorText = "Home";
	public static final String anchorHref = "/";

	public static UnsupportedOperationException getContentTypeError() {
		final String message = "Request type is not supported";
		return new UnsupportedOperationException(message);
	}

	private static final String contentTypeHtml = "text/html";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { }
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { }
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { }

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		if (respondWithHtml(req)) {
			writeHTMLResponseDocument(resp.getWriter());
		} else {
			throw getContentTypeError();
		}
	}

	private boolean respondWithHtml(HttpServletRequest req) {
		boolean useHtml = req.getContentType() == null || req.getContentType().indexOf(contentTypeHtml) > -1;
		return useHtml;
	}

	private void writeHTMLResponseDocument(PrintWriter writer) {
		Document doc = createDocument();
		doc = addTitle(doc, title);
		doc = addAnchor(doc, anchorText, anchorHref);
		writer.append(doc.html());
		writer.flush();
	}

	private static Document createDocument() {
		String html = "<!DOCTYPE html>"
			+ "<" + htmlTag + " " + langAttr + "='" + langAttrVal + "'>"
			+ "<" + headTag + "></" + headTag + ">"
			+ "<" + bodyTag + "></" + bodyTag + ">"
			+ "</" + htmlTag + ">";
		Document doc = Jsoup.parse(html);
		return doc;
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
