package org.mlk.kjm.my;

import static org.mlk.kjm.ServletHtmlUtils.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
	private static final String greetingHtml = "MyServletGreeting.html";
	private static final String formHtml = "MyServletForm.html";

    private static final String greetingsToId = "greetingsTo";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Document greetingDoc = getGreetingDocument(Optional.empty());
		Document formDoc = getFormDocument(Optional.empty());
		String html = greetingDoc.html() + formDoc.html();
		resp.getWriter().append(html).flush();
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Optional<String>> postBody = getPostBody(req);
		
		Document greetingDoc = getGreetingDocument(postBody.get(greetingsToId));
		String html = greetingDoc.html();
		resp.getWriter().append(html).flush();
	}
    
    private Document getGreetingDocument(Optional<String> greetingsTo) throws IOException {
        Document html = getHtmlDocument(greetingHtml);
        if (greetingsTo.isPresent()) {
            Element greetingsToElement = html.getElementById(greetingsToId);
            greetingsToElement.text(greetingsTo.get());
        }

        return html;
    }

	private Document getFormDocument(Optional<String> greetingsTo) throws IOException {
        Document html = getHtmlDocument(formHtml);
        return html;
    }
}
