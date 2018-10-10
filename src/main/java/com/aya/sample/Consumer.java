package com.aya.sample;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import java.util.concurrent.BlockingQueue;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 10.10.18
 */
public class Consumer implements Runnable{
    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> q){
        queue = q;
    }

    public void run(){
        while(true){
            String line = queue.poll();

            if(line == null && !Controller.isProducerAlive())
                return;

            if(line != null){
                System.out.println(Thread.currentThread().getName()+" processing line: "+line);
                //Do something with the line here like see if it contains a string
            }

        }
    }
}