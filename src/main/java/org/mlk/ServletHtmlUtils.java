package org.mlk;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.http.HttpServletRequest;

public class ServletHtmlUtils {
    public static final String htmlTag = "html";
	public static final String hrefAttr = "href";
	public static final String langAttr = "lang";
	public static final String headTag = "head";
	public static final String bodyTag = "body";
	public static final String titleTag = "title";
	public static final String anchorTag = "a";
    public static final String langAttrVal = "en";

    private static final String contentTypeHtml = "text/html";

	public static UnsupportedOperationException getContentTypeError() {
		final String message = "Content type is not supported";
		return new UnsupportedOperationException(message);
	}

	public static UnsupportedOperationException getRequestMethodError() {
		final String message = "Request method is not supported";
		return new UnsupportedOperationException(message);
	}
    
    public static boolean respondWithHtml(HttpServletRequest req) {
		if (req.getContentType() == null) {
			return true;
		}

		if (req.getContentType().indexOf(contentTypeHtml) > -1) {
			return true;
		}

		return false;
	}

    public static Document createDocument() {
		String html = "<!DOCTYPE html>"
			+ "<" + htmlTag + " " + langAttr + "='" + langAttrVal + "'>"
			+ "<" + headTag + "></" + headTag + ">"
			+ "<" + bodyTag + "></" + bodyTag + ">"
			+ "</" + htmlTag + ">";
		Document doc = Jsoup.parse(html);
		return doc;
	}

	public static Element getHead(Document doc) {
		return doc.selectFirst(headTag);
	}

	public static Element getBody(Document doc) {
		return doc.selectFirst(bodyTag);
	}
}
