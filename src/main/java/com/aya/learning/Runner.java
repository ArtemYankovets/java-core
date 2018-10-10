package com.aya.learning;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
public class Runner {

    public static void main(String[] args) {
        Path srcDirPath = Paths.get("src/main/resources/books/");

        FileReaderInOneThread reader = new FileReaderInOneThread();

        try {
            List<Path> listOfFilePathes = reader.getListOfFilePathes(srcDirPath);
            List<IOStreamHelper> helpers = reader.compileHeplersByFilePathes(listOfFilePathes);
            while (reader.getAvailableForRead(helpers).isPresent()) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
