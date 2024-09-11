package org.mlk.kjm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;
import static java.util.stream.Collectors.toList;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {
	private static final String pattern = "MM/dd/yyyy";
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	public static String dateToString(LocalDate date) {
		return date.format(formatter);
	}

	public static LocalDate stringToDate(String string) {
		return LocalDate.parse(string, formatter);
	}

    private static final String templatesDirectory = "/templates";

	public static Document getHtmlDocument(String file) throws IOException {
		String filePath = templatesDirectory + "/" + file;
		InputStream inputStream = ServletUtils.class.getResourceAsStream(filePath);
		byte[] bytes = inputStream.readAllBytes();
		String html = new String(bytes);
		Document htmlDocument = Jsoup.parseBodyFragment(html);
		return htmlDocument;
	}

	public static String getRequiredFromPostBody(Map<String, Optional<String>> postBody, String parameter) throws IOException {
		Optional<String> value = postBody.get(parameter);
		if (value.isEmpty()) {
			throw new IllegalArgumentException(parameter + " is required!");
		}

		return value.get();
	}

	public static Optional<String> getOptionalFromPostBody(Map<String, Optional<String>> postBody, String parameter) throws IOException {
		Optional<String> value = postBody.get(parameter);
		return value;
	}

	public static Map<String, Optional<String>> getPostBodyMap(HttpServletRequest req) throws IOException {
		String postBody = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
		String[] parameters = postBody.split("&");
		Map<String, Optional<String>> result = new HashMap<String, Optional<String>>();
		for (String parameter : parameters) {
			String[] keyValue = parameter.split("=");
			String key = keyValue[0];
			Optional<String> value = (keyValue.length == 2) 
				? Optional.of(decode(keyValue[1], StandardCharsets.UTF_8.name())) 
				: Optional.empty();
			result.put(key, value);
		}

		return result;
	}

	public static Optional<String> getOptionalParameter(HttpServletRequest req, String parameter) throws UnsupportedEncodingException {
		String result = req.getParameter(parameter);
		if (result == null || "".equals(result)) {
			return Optional.empty();
		}

		return Optional.of(decode(result, StandardCharsets.UTF_8.name()));
	}

	public static String getRequiredParameter(HttpServletRequest req, String parameter) throws UnsupportedEncodingException {
		String result = req.getParameter(parameter);
		if (result == null || "".equals(result)) {
			throw new IllegalArgumentException(parameter + " is required!");
		}

		return decode(result, StandardCharsets.UTF_8.name());
	}

	public static String createLink(String contextPath, Map<String, String> params) throws UnsupportedEncodingException {
		List<String> encodedParams = params.keySet().stream().map(k -> {
			try {
				String encodedKey = encode(k, StandardCharsets.UTF_8.name());
				String encodedValue = encode(params.get(k), StandardCharsets.UTF_8.name());
				return encodedKey + "=" + encodedValue;
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}).collect(toList());

		String delimiter = "&";
		String result = String.join(delimiter, encodedParams);

		String prefix = "/";
		if (!contextPath.startsWith(prefix)) {
			contextPath = "/" + contextPath;
		}

		return contextPath + "?" + result;
	}
}
