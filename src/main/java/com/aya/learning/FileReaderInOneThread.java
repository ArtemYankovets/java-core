package com.aya.learning;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
public class FileReaderInOneThread {


    public List<Path> getListOfFilePathes(Path srcDirPath) throws IOException {

        return Files.list(srcDirPath)
                .filter(Files::isRegularFile)
                .peek(System.out::println)
                .collect(Collectors.toList());
    }

    public List<IOStreamHelper> compileHeplersByFilePathes(List<Path> filePathslist) {
        return filePathslist.stream()
                .map(IOStreamHelper::create)
                .collect(Collectors.toList());
    }

    public Optional<IOStreamHelper> getAvailableForRead(List<IOStreamHelper> helpers) {
        return helpers.stream()
                .filter(IOStreamHelper::isAvailable)
                .findFirst();
    }
}
