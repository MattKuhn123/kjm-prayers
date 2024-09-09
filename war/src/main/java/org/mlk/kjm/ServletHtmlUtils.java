package org.mlk.kjm;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jakarta.servlet.http.HttpServletRequest;

public class ServletHtmlUtils {
    private static final String templatesDirectory = "/templates";

	public static Document getHtmlDocument(String file) throws IOException {
		String filePath = templatesDirectory + "/" + file;
		InputStream inputStream = ServletHtmlUtils.class.getResourceAsStream(filePath);
		byte[] bytes = inputStream.readAllBytes();
		String html = new String(bytes);
		Document htmlDocument = Jsoup.parseBodyFragment(html);
		return htmlDocument;
	}

	public static Map<String, Optional<String>> getPostBody(HttpServletRequest req) throws IOException {
		String postBody = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
		String[] parameters = postBody.split("&");
		Map<String, Optional<String>> result = new HashMap<String, Optional<String>>();
		for (String parameter : parameters) {
			String[] keyValue = parameter.split("=");
			String key = keyValue[0];
			Optional<String> value = (keyValue.length == 2) 
				? Optional.of(keyValue[1]) 
				: Optional.empty();
			result.put(key, value);
		}

		return result;
	}
}
