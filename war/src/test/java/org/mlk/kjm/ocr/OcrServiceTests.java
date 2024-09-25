package org.mlk.kjm.ocr;

import java.io.InputStream;
import java.util.Optional;
import org.junit.Test;
import org.mlk.kjm.helpers.ApplicationPropertiesTestingImpl;
import org.mlk.kjm.shared.InputStreamUtils;

import junit.framework.TestCase;

public class OcrServiceTests extends TestCase {
    
    @Test
    public void test_Something() throws Exception {
        OcrService ocr = new OcrService(new ApplicationPropertiesTestingImpl());

        String file = "ocr/image.png";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(file);
        byte[] bytes = InputStreamUtils.readAllBytes(inputStream);
        Optional<String> result = ocr.getTextFromImage(bytes);
        assertTrue(result.isPresent());
    }
}
