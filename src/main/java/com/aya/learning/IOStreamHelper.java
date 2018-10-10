package com.aya.learning;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Log
public class IOStreamHelper {

    private Path filePath;

    private int availableBufferSize;

    private InputStream inputStream;

    private BufferedReader bufferedReader;

    private ContentHandler contentHandler;

    public boolean isAvailable() {
        try {
            availableBufferSize = inputStream.available();
        } catch (IOException e) {
            String msg = String.format("Can't read file \'%s\'. %nCause: %s", filePath, e.getMessage());
            log.warning(msg);
            availableBufferSize = 0;
        }
        return availableBufferSize > 0;
    }

    public static IOStreamHelper create(Path filePath) {

        IOStreamHelper helper = new IOStreamHelper();
        helper.filePath = filePath;

        try {

            InputStream inputStream = new FileInputStream(filePath.toString());
            int availableSize = inputStream.available();

            helper.inputStream = inputStream;
            helper.availableBufferSize = availableSize;
            helper.bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                    availableSize
            );
            helper.contentHandler = new ContentHandler();

        } catch (IOException e) {
            String msg = String.format("Can't read file \'%s\'. %nCause: %s", filePath, e.getMessage());
            log.warning(msg);
        }

        return helper;
    }
}
