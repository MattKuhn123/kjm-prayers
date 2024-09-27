package org.mlk.kjm.shared;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.mlk.kjm.home.HomeServlet;
import org.mlk.kjm.users.AuthToken;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletUtils {

	public static final String selectedAttr = "selected";
	public static final String checkedAttr = "checked";
	public static final String falseVal = "false";
	public static final String trueVal = "true";
	public static final String disabledAttr = "disabled";
    public static final String inputSubmit = "input[type='submit']";
    public static final String formTag = "form";
    public static final String tableTag = "table";
    public static final String tbodyTag = "tbody";
    public static final String trTag = "tr";
    public static final String hxGetAttr = "hx-get";
    public static final String hxPutAttr = "hx-put";
    public static final String idAttr = "id";
    public static final String emptyString = "";

	// DATES
	private static final String pattern = "MM/dd/yyyy";
	private static final String htmlPattern = "yyyy-MM-dd";
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	private static DateTimeFormatter htmlFormatter = DateTimeFormatter.ofPattern(htmlPattern);
	public static String dateToString(LocalDate date) {
		try {
			return date.format(formatter);
		} catch (Exception  e) {
			return date.format(htmlFormatter);
		}
	}

	public static LocalDate stringToDate(String string) {
		try {
			return LocalDate.parse(string, formatter);
		} catch (Exception e) {
			return LocalDate.parse(string, htmlFormatter);
		}
	}

	// HTML
    private static final String templatesDirectory = "/templates";

	public static Document getHtmlDocument(String file) throws IOException {
		String filePath = templatesDirectory + "/" + file;
		InputStream inputStream = ServletUtils.class.getResourceAsStream(filePath);
		byte[] bytes = InputStreamUtils.readAllBytes(inputStream);
		String html = new String(bytes);
		Document htmlDocument = Jsoup.parseBodyFragment(html);
		return htmlDocument;
	}

	// REQUEST PARAMETER HELPERS
	public static String getRequiredFromPostBody(Map<String, Optional<String>> postBody, String parameter) throws IllegalArgumentException {
		Optional<String> value = postBody.get(parameter);
		if (!value.isPresent()) {
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

	public static boolean isParameterPresent(HttpServletRequest req, String parameter) {
		String value = req.getParameter(parameter);
		boolean result = value != null;
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

	// RANDOM
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
	
	// PAGING
	public static final String previousBtnId = "previous-btn";
	public static final String nextBtnId = "next-btn";
	public static final String pageId = "page";
	public static final String pagesId = "pages";
	public static final String defaultPage = "0";
    public static final int defaultPageLength = 15;
    public static final String pageActionsId = "page-actions";
	public static final String toPageParam = "toPage";
    public static final String listResetName = "/Reset";
    public static final String listPreviousName = "/Previous";
    public static final String listNextName = "/Next";

    public static boolean isPageReset(String pathInfo) {
        boolean result = pathInfo.indexOf(listResetName) > -1;
        return result;
    }

    public static int getToPage(HttpServletRequest req) {
        try {
            Optional<String> toPage = getOptionalParameter(req, toPageParam);
            int result = Integer.parseInt(toPage.get());
            return result;
        } catch (UnsupportedEncodingException e) {
            return -1;
        }
    }

    public static boolean isToPage(HttpServletRequest req) {
        try {
            Optional<String> toPage = getOptionalParameter(req, toPageParam);
            return toPage.isPresent();
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static int getPageDirection(String pathInfo) {
        if (pathInfo.indexOf(listPreviousName) > -1) {
            return -1;
        }

        if (pathInfo.indexOf(listNextName) > -1) {
            return 1;
        }

        return 0;
    }

	public static int getPage(HttpServletRequest req) {
        if (isToPage(req)) {
            int result = getToPage(req);
            return result;
        }

        if (isPageReset(req.getPathInfo())) {
            return 0;
        }

        try {
            String page = getOptionalParameter(req, pageId).orElse(defaultPage);
            int result = Integer.parseInt(page) + getPageDirection(req.getPathInfo());
            return result;
        } catch (Exception e) {
            return -1;
        }
    }


	// COOKIES
	public static void setAuthCookie(HttpServletResponse resp, AuthToken authToken) {
		int maxAge = HomeServlet.daysToExpires * 24 * 60 * 60;
		setCookie(resp, authCookieName, authToken.toString(), maxAge);
	}

	public static void setCookie(HttpServletResponse resp, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        
        boolean isHttpOnly = true;
        cookie.setHttpOnly(isHttpOnly);

        boolean isSecure = true;
        cookie.setSecure(isSecure);

        cookie.setMaxAge(maxAge);

		cookie.setPath("/");

        resp.addCookie(cookie);;
    }

    public static Optional<String> getCookie(HttpServletRequest req, final String searchFor) {
		Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (searchFor.equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

	public static String authCookieName = "z";

	public static Optional<AuthToken> getAuthToken(HttpServletRequest req) {
		Optional<String> authCookie = getAuthCookie(req);
		if (!authCookie.isPresent()) {
			return Optional.empty();
		}

		Optional<AuthToken> authToken = AuthToken.fromString(authCookie.get());
		return authToken;
	}

	public static Optional<String> getAuthCookie(HttpServletRequest req) {
		return getCookie(req, authCookieName);
	}
}
