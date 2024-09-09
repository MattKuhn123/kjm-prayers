package org.mlk.kjm.ocr;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sourceforge.tess4j.TesseractException;

public class OcrServlet extends HttpServlet {
    public static final String filePartName = "file";
    private final OcrService ocrService;

    public OcrServlet() {
        this.ocrService = new OcrService();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream inputStream = req.getPart(filePartName).getInputStream();
        try {
            String textContent = this.ocrService.parseTextContentFromImage(inputStream);
            resp.getWriter().append(textContent).flush();
        } catch (TesseractException e) {
            e.printStackTrace(resp.getWriter());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
	}
}
