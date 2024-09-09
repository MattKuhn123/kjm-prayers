package org.mlk.kjm.ocr;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.mlk.kjm.ServletUtils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OcrService {
    private final ITesseract instance;

    public OcrService() {
        instance =  new Tesseract();
        String dataPath = ServletUtils.class.getResource("/tessdata").getFile().substring(1);
        instance.setDatapath(dataPath);
    }

    public String parseTextContentFromImage(InputStream is) throws IOException, TesseractException {
		BufferedImage bi = ImageIO.read(is);
		String result = instance.doOCR(bi);  
		return result;
	}
}
