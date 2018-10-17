package com.aya.learning;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import lombok.*;
import lombok.extern.java.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Log
public class ContentURLReader implements Runnable {

    @Setter
    private String name;

    private String content;

    private long contentSize;

    private long totalReadBytes;

    private boolean isContentNotOver;

    private URL contentUrl;

    private int availableBufferSize;

    private InputStream srcContentInputStream;

    private ByteArrayOutputStream resultByteArray;

    private BufferedReader bufferedReader;

    private ContentHandler contentHandler;

    public boolean isContentNotOver() {
        if (isContentNotOver && (contentSize - totalReadBytes) == 0) {
            isContentNotOver = false;
        }

        return isContentNotOver;
    }

    public boolean isAvailable() {
        boolean result = false;
        try {
            availableBufferSize = srcContentInputStream.available();
            if (availableBufferSize == 0) {
                srcContentInputStream.close();
                isContentNotOver = false;
            } else {
                result = availableBufferSize > 0;
            }
        } catch (IOException e) {
            String msg = String.format("Can't read file \'%s\'. %nCause: %s", contentUrl.toString(), e.getMessage());
            log.warning(msg);
        }
        return result;
    }

    public static ContentURLReader create(URL url) {

        ContentURLReader helper = new ContentURLReader();
        helper.contentUrl = url;
        helper.isContentNotOver = true;
        helper.totalReadBytes = 0;

        helper.resultByteArray = new ByteArrayOutputStream();

        try {
            URLConnection conn = helper.contentUrl.openConnection();
            InputStream inputStream = conn.getInputStream();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            helper.contentSize = conn.getContentLength();

            double mbSize = (double) helper.contentSize / 1024;
            String msg = String.format("%n\t file name: \t %s%n \t\tsize: \t %4.3f kB", helper.contentUrl.toString(), mbSize);
            log.info(msg);

            int availableSize = inputStream.available();
            helper.srcContentInputStream = inputStream;
            helper.availableBufferSize = availableSize;
            helper.bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                    availableSize
            );
            helper.contentHandler = new ContentHandler();

        } catch (IOException e) {
            String msg = String.format("Can't read file \'%s\'. %nCause: %s",
                    helper.contentUrl.toString(), e.getMessage());
            log.warning(msg);
        }

        return helper;
    }

    public void readContent() {
        try {
            byte[] buffer = new byte[availableBufferSize];
            log.info("Available buffer size " + availableBufferSize + " bytes");
            totalReadBytes -= availableBufferSize;

            // reading content
            int bytesRead = srcContentInputStream.read(buffer, 0, buffer.length);
            log.info(Thread.currentThread().getName() + " read available " + bytesRead + " bytes from " + contentUrl.toString());

            // content accumulation
            ByteArrayOutputStream contentAccumulator = new ByteArrayOutputStream();
            contentAccumulator.write(resultByteArray.toByteArray(), 0, resultByteArray.size());
            contentAccumulator.write(buffer, 0, bytesRead);
            contentAccumulator.flush();
            contentAccumulator.close();
            resultByteArray = contentAccumulator;
            log.info(Thread.currentThread().getName() + " read bytes was accumulated");

        } catch (IOException e) {
            String msg = Thread.currentThread().getName() +
                    ". Something wrong with reading file: " + contentUrl.toString() +
                    " Cause: " + e.getMessage();
            log.warning(msg);
        }
    }

    @Override
    public void run() {
        readContent();
    }

    public String getContent() {

        if (isContentNotOver) {
            log.warning("isContentNotOver: " + isContentNotOver());
            throw new IllegalArgumentException("Content is not over");
        }

        content = new String(resultByteArray.toByteArray(), StandardCharsets.UTF_8);
        String separator = "--------------------------------------------------------";
        String msg = String.format("%s%n result content %n " +
                        "\t file name: %s%n " +
                        "%s%n " +
                        "%s%n " +
                        "%s%n",
                separator, contentUrl.toString(), separator, content, separator);
        log.info(msg);
        return content;
    }

}
