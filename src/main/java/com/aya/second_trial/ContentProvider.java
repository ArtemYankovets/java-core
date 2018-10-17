package com.aya.second_trial;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Log
public class ContentProvider extends Thread {

    private URL url;

    private int availableBytesFoReading = 0;

    private ByteArrayOutputStream resultByteArray = new ByteArrayOutputStream();

    private Charset encoding;

    @Getter
    private InputStream in;


    public ContentProvider(URL url) {
        this.url = url;

        try {
            URLConnection con = url.openConnection();
            in = con.getInputStream();
            encoding = getEncoding(con.getContentEncoding());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.currentThread().setName("provider for content " + url.toString());
    }

    @Override
    public void run() {
        while (true) {
            getAvailableBytesFoReading();
            if (availableBytesFoReading > 0) {
                String msg = String.format("%s - available bytes: %s",
                        Thread.currentThread().getName(), availableBytesFoReading);
                log.info(msg);
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    String msg = String.format("%s was interrupted. Cause:%n%s",
                            Thread.currentThread().getName(), e.getMessage());
                    log.warning(msg);
                }
            }
        }
    }

    public int getAvailableBytesFoReading() {
        try {
            availableBytesFoReading = in.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableBytesFoReading;
    }

    public void accumulateContent(byte[] buffer) {

        try (ByteArrayOutputStream contentAccumulator = new ByteArrayOutputStream()) {
            contentAccumulator.write(resultByteArray.toByteArray(), 0, resultByteArray.size());
            contentAccumulator.write(buffer, 0, buffer.length);
            contentAccumulator.flush();
            resultByteArray = contentAccumulator;
        } catch (IOException e) {
            String msg = String.format("%s has problem with accumulation data. Cause:%n%s",
                    Thread.currentThread().getName(), e.getMessage());
            log.warning(msg);
        }

        String msg = String.format("%s was accumulated: %s bytes",
                Thread.currentThread().getName(), buffer.length);
        log.info(msg);
    }

    public String getStringContent() {
        String msg = String.format("\t ==> %s result content has size %s bytes",
                Thread.currentThread().getName(), resultByteArray.size());
        log.info(msg);
        return new String(resultByteArray.toByteArray(), encoding);
    }


    private Charset getEncoding(String contentEncoding) {
        Charset defaultCharset = StandardCharsets.UTF_8;

        if (contentEncoding != null) {
            //todo add parsing contentEncoding
        }

        return defaultCharset;
    }

}
