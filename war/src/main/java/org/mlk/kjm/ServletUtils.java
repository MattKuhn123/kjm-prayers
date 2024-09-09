package org.mlk.kjm;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {
    private static final String templatesDirectory = "/templates";

	public static Document getHtmlDocument(String file) throws IOException {
		String filePath = templatesDirectory + "/" + file;
		InputStream inputStream = ServletUtils.class.getResourceAsStream(filePath);
		byte[] bytes = inputStream.readAllBytes();
		String html = new String(bytes);
		Document htmlDocument = Jsoup.parseBodyFragment(html);
		return htmlDocument;
	}

	private static Map<String, Optional<String>> getPostBodyMap(HttpServletRequest req) throws IOException {
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

	// TODO : Can we enforce having a constructor with a Map parameter?
	public static <T> T getPostBody(HttpServletRequest req, Class<T> clazz) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, Optional<String>> postBody = getPostBodyMap(req);
		
		@SuppressWarnings("rawtypes")
		Class[] cArg = new Class[] { Map.class };
		T result = clazz.getDeclaredConstructor(cArg).newInstance(postBody);
		return result;
	}
}
