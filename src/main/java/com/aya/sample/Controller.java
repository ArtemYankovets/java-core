package com.aya.sample;

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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
public class Controller {
    private static final int NUMBER_OF_CONSUMERS = 3;
    private static final int NUMBER_OF_PRODUCERS = 3;
    private static final int QUEUE_SIZE = 2;
    private static BlockingQueue<String> queue;
    private static Collection<Thread> producerThreadCollection, allThreadCollection;

    public static void main(String[] args) {
        producerThreadCollection = new ArrayList<Thread>();
        allThreadCollection = new ArrayList<Thread>();
        queue = new LinkedBlockingDeque<String>(QUEUE_SIZE);

        createAndStartProducers();
        createAndStartConsumers();

        for(Thread t: allThreadCollection){
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("Controller finished");
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

        pathList.forEach(path -> {
            Producer producer = new Producer(path, queue);
            Thread producerThread = new Thread(producer,"producer-" + path.toString());
            producerThreadCollection.add(producerThread);
            producerThread.start();
        });

        allThreadCollection.addAll(producerThreadCollection);
    }

    private static void createAndStartConsumers(){
        for(int i = 0; i < NUMBER_OF_CONSUMERS; i++){
            Thread consumerThread = new Thread(new Consumer(queue), "consumer-" + i);
            allThreadCollection.add(consumerThread);
            consumerThread.start();
        }
    }

    public static boolean isProducerAlive(){
        for(Thread t: producerThreadCollection){
            if(t.isAlive())
                return true;
        }
        return false;
    }
}
