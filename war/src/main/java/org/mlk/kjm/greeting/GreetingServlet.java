package org.mlk.kjm.greeting;

import static org.mlk.kjm.ServletUtils.*;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GreetingServlet extends HttpServlet {
	private static final String directory = "greeting/";
	private static final String greetingResponseHtml = directory + "AddGreetingResponse.html";
	private static final String greetingHtml = directory + "Greeting.html";
	private static final String greetingLiHtml = directory + "GreetingLi.html";

    private static final String greetingsListId = "greetingsList";
    private static final String greetingsToId = "greetingsTo";
    private static final String msgId = "msg";

	private final GreetingRepository greetings;
	public GreetingServlet() {
		this(new GreetingRepository());
	}

	public GreetingServlet(GreetingRepository greetings) {
		this.greetings = greetings;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Document greetingDoc = getGreetingDocument(greetings.getGreetings());
		String html = greetingDoc.html();
		resp.getWriter().append(html).flush();
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean addedGreeting = addGreeting(req);
		String msg = addedGreeting ? "greeted!" : "error!";
		Document greetingDoc = getAddGreetingResponseDocument(msg);
		String html = greetingDoc.body().html();
		resp.getWriter().append(html).flush();
	}

	private boolean addGreeting(HttpServletRequest req) {
		try {
			GreetingRequest postBody = getPostBody(req, GreetingRequest.class);
			Greeting newGreeting = new Greeting(postBody.getGreetingsTo());
			this.greetings.addGreeting(newGreeting);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
    
    private Document getGreetingDocument(List<Greeting> greetingsTo) throws IOException {
        Document doc = getHtmlDocument(greetingHtml);
		Element greetingsListElement = doc.getElementById(greetingsListId);
		for (Greeting greeting : greetingsTo) {
			Document li = getHtmlDocument(greetingLiHtml);
			Element greetingsToElement = li.getElementById(greetingsToId);
			if (greeting.getGreetingTo().isPresent()) {
				greetingsToElement.text(greeting.getGreetingTo().get());
			}

			greetingsListElement.appendChild(li.body());
		}

        return doc;
    }

	private Document getAddGreetingResponseDocument(String msg) throws IOException {
        Document doc = getHtmlDocument(greetingResponseHtml);
		doc.getElementById(msgId).text(msg);
        return doc;
    }
}
