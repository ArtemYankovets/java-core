package com.aya.sample;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
public class Producer implements Runnable{

    private Path fileToRead;
    private BlockingQueue<String> queue;

    public Producer(Path filePath, BlockingQueue<String> q){
        fileToRead = filePath;
        queue = q;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = Files.newBufferedReader(fileToRead);
            String line;
            while((line = reader.readLine()) != null){
                try {
                    queue.put(line);
                    System.out.println(Thread.currentThread().getName() + " added \"" + line + "\" to queue, queue size: " + queue.size());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            System.out.println(Thread.currentThread().getName()+" finished");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
