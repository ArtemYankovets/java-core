package com.aya.learning;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
@Log
public class Runner {

    public static void main(String[] args) {

        log.info("==== START ===");
        Path srcDirPath = Paths.get("src/main/resources/books/");

        FileReaderInOneThread reader = new FileReaderInOneThread();

// https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
//        URL[] strings = {
//                new URL("https://www.apple.com/"),
//                new URL("https://www.imdb.com/"),
//                new URL("http://www1.euro.dell.com"),
//                new URL("https://www.microsoft.com/")
//        };

//        List<URL> urlList = Arrays.asList(strings);

//        try {

/*            List<ContentURLReader> contentReaders = reader.compileHeplersByFilePathes(urlList);
            log.info("--------------------------------------------------------");

            ContentURLReader current = reader.getAvailableForRead(contentReaders);
            while ( current != null && current.isAvailable() ) {
                current.readContent();
                current = reader.getAvailableForRead(contentReaders);
            }
            log.info("--------------------------------------------------------");

            contentReaders.forEach(ContentURLReader::getContent);
            log.info("--------------------------------------------------------");*/

//            List<Path> listOfFilePaths = reader.getListOfFilePathes(srcDirPath);
            List<Path> listOfFilePaths = new ArrayList<>();

            listOfFilePaths.add(Paths.get("src/main/resources/books/2015 Gold FCE SB.pdf"));

            log.info("--------------------------------------------------------");

            List<ContentReader> contentReaders = reader.compileHeplersByFilePathes(listOfFilePaths);
            log.info("--------------------------------------------------------");

            ContentReader current = reader.getAvailableForRead(contentReaders);
            while ( current != null && current.isAvailable() ) {
                current.readContent();
                current = reader.getAvailableForRead(contentReaders);
            }
            log.info("--------------------------------------------------------");

            contentReaders.forEach(ContentReader::getContent);
            log.info("--------------------------------------------------------");

//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        log.info("=== FINISH ===");
    }

    private static void createAndStartProducers(){
        Path srcDirPath = Paths.get("src/main/resources/books/");

        List<Path> pathList = new ArrayList<>();

        try {
            pathList = Files.list(srcDirPath)
                    .filter(Files::isRegularFile)
                    .peek(System.out::println)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int num = 0;
        for (Path path : pathList) {
            ContentReader contentReader = ContentReader.create(path);
            contentReader.setName("contentReader #" + num++);
            Thread contentReaderThread = new Thread(contentReader,contentReader.getName() + path.toString());
            contentReaderThread.start();
        }
    }


//    public static void trial() {
//        Path srcDirPath = Paths.get("src/main/resources/books/");
//
//        FileReaderInOneThread reader = new FileReaderInOneThread();
//
//        try {
//            List<Path> listOfFilePathes = reader.getListOfFilePathes(srcDirPath);
//
//            Path path = listOfFilePathes.get(0);
//            FileInputStream in = new FileInputStream(path.toString());
//
//
//            byte[] buffer = new byte[50];
//
//            int offset = 0;
//            // First read
//            int bytesRead = in.read(buffer, offset, buffer.length);
//            System.out.println(new String(buffer, 0, bytesRead));
//            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//
//            arrayOutputStream.write(buffer, 0, bytesRead);
//            arrayOutputStream.flush();
//            arrayOutputStream.close();
//            System.out.println("---------------------------");
//
//            // Second read
//            bytesRead = in.read(buffer, 0, buffer.length);
//            System.out.println(new String(buffer, 0, bytesRead));
//
//            ByteArrayOutputStream arrayOutputStream2 = new ByteArrayOutputStream();
//
//            arrayOutputStream2.write(arrayOutputStream.toByteArray(), 0, arrayOutputStream.size());
//            arrayOutputStream2.write(buffer, 0, bytesRead);
//
//            arrayOutputStream2.flush();
//            arrayOutputStream2.close();
//            System.out.println("---------------------------");
//            System.out.println("---------------------------");
//
//            byte[] bytes = arrayOutputStream2.toByteArray();
//            String s = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println(s);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

}
