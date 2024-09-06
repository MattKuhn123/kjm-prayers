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

    public static boolean isRequestingHtml(HttpServletRequest req) {
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
			+ "</" + htmlTag + ">";
		Document doc = Jsoup.parse(html);
		return doc;
	}

	public static Element createTitle(String text) {
		Element titleEmpty = createEmptyElement(titleTag);
		Element titleResult = titleEmpty.text(text);
		return titleResult;
	}

	public static Element createAnchor(String text, String href) {
		Element anchorEmpty = createEmptyElement(anchorTag);
		Element anchorResult = anchorEmpty.text(text).attr(hrefAttr, href);
		return anchorResult;
	}

	private static String toOpenTag(String tag) {
		return "<" + tag + ">";
	}

	private static String toCloseTag(String tag) {
		return "</" + tag + ">";
	}

	private static Element createEmptyElement(String tag) {
		String html = toOpenTag(tag) + toCloseTag(tag);
		Document bodyFragment = Jsoup.parseBodyFragment(html); 
		Element body = bodyFragment.body();
		Element emptyElement = body.selectFirst(tag);
		return emptyElement;
	}
}
