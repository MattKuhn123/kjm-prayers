package org.mlk.kjm.ocr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.shared.ApplicationPropertiesImpl;
import org.mlk.kjm.shared.InputStreamUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class OcrServlet extends HttpServlet {
    private static final String fileId = "file";

    private final OcrService ocr;

    public OcrServlet() {
        this(new ApplicationPropertiesImpl());
    }

    public OcrServlet(ApplicationProperties props) {
        this.ocr = new OcrService(props);
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart(fileId);
        InputStream fileContent = filePart.getInputStream();

        byte[] bytes = InputStreamUtils.readAllBytes(fileContent);
        Optional<String> result = ocr.getTextFromImage(bytes);

        if (result.isPresent()) {
            resp.getWriter().append(result.get()).flush();
        } else {
            final String defaultText = "Could not get text from image.";
            resp.getWriter().append(defaultText).flush();
        }
    }
}
