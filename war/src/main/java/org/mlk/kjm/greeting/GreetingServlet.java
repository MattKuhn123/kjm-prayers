package org.mlk.kjm.greeting;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GreetingServlet extends HttpServlet {
	private static final String greetingHtml = "Greeting.html";
	private static final String formHtml = "GreetingForm.html";

    private static final String greetingsToId = "greetingsTo";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Document greetingDoc = getGreetingDocument(Optional.empty());
		Document formDoc = getFormDocument();
		String html = greetingDoc.html() + formDoc.html();
		resp.getWriter().append(html).flush();
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			GreetingRequest postBody = getPostBody(req, GreetingRequest.class);
			Document greetingDoc = getGreetingDocument(postBody.getGreetingsTo());
			String html = greetingDoc.html();
			resp.getWriter().append(html).flush();
		} catch (Exception e) {
			// TODO : Filter stack traces in non-dev
			e.printStackTrace(resp.getWriter());
		}
	}
    
    private Document getGreetingDocument(Optional<String> greetingsTo) throws IOException {
        Document html = getHtmlDocument(greetingHtml);
        if (greetingsTo.isPresent()) {
            Element greetingsToElement = html.getElementById(greetingsToId);
            greetingsToElement.text(greetingsTo.get());
        }

        return html;
    }

	private Document getFormDocument() throws IOException {
        Document html = getHtmlDocument(formHtml);
        return html;
    }
}
