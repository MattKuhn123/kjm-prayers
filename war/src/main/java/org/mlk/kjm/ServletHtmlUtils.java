package org.mlk.kjm;


import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jakarta.servlet.http.HttpServletRequest;

public class ServletHtmlUtils {
    private static final String templatesDirectory = "/templates";
    private static final String contentTypeHtml = "text/html";

	public static Document getHtmlDocument(String file) throws IOException {
		try {
			String filePath = templatesDirectory + "/" + file;
			InputStream abc = ServletHtmlUtils.class.getResourceAsStream(filePath);
			byte[] bytes = abc.readAllBytes();
			String html = new String(bytes);
			Document htmlDocument = Jsoup.parseBodyFragment(html);
			return htmlDocument;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

	}

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
}
