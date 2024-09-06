package org.mlk.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class MockServletOutputStream extends ServletOutputStream {

    WriteListener wr;
    public ByteArrayOutputStream real = new ByteArrayOutputStream();

    @Override
    public boolean isReady() { return true; }

    @Override
    public void setWriteListener(WriteListener arg0) { wr = arg0; }

    @Override
    public void write(int b) throws IOException {
        real.write(b);
    }
    
}
