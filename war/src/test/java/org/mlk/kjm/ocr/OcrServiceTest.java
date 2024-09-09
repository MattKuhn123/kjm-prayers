package org.mlk.kjm.ocr;

import java.io.InputStream;

import org.junit.Test;
import org.mlk.kjm.ServletUtils;

import junit.framework.TestCase;

public class OcrServiceTest extends TestCase {
    
    @Test
    public void test_Something() throws Exception {
        String filePath = "/prayer.jpg";
		InputStream inputStream = ServletUtils.class.getResourceAsStream(filePath);

        OcrService ocrService = new OcrService();

        String textContent = ocrService.parseTextContentFromImage(inputStream);
        assertNotNull(textContent);
    }
}
