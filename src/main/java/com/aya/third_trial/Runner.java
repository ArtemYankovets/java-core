package com.aya.third_trial;

import lombok.extern.java.Log;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log
public class Runner {
    public static void main(String[] args) {

        try {
            Reactor reactor = new Reactor();

            CompletableFuture<String> content1 = reactor.put(Paths.get("src/main/resources/books/Alice's Adventures in Wonderland by Lewis Carroll.txt"));
            CompletableFuture<String> content2 = reactor.put(Paths.get("src/main/resources/books/King Stephen. It - royallib.ru.txt"));

            log.info("----- Thread sleep 10000 -----");
            Thread.sleep(10000);

            CompletableFuture<String> content3 = reactor.put(Paths.get("src/main/resources/books/The Tempest by William Shakespeare.txt"));

            content1.thenAccept(result -> log.info("\t Callback for content1 " + result.length()));
            content2.thenAccept(result -> log.info("\t Callback for content2 " + result.length()));
            content3.thenAccept(result -> log.info("\t Callback for content3 " + result.length()));

            String msg = String.format("\t\t Content 1 is %s", content1.get().length());
            log.info(msg);
            msg = String.format("\t\t Content 3 is %s", content3.get().length());
            log.info(msg);
            msg = String.format("\t\t Content 2 is %s", content2.get().length());
            log.info(msg);

            log.info("----- Thread sleep 10000 -----");
            Thread.sleep(10000);

            CompletableFuture<String> content4 =
                    reactor.put(Paths.get("src/main/resources/books/Alice's Adventures in Wonderland by Lewis Carroll.txt"));

            log.info("----- Thread sleep 7000 -----");
            Thread.sleep(7000);

            msg = String.format("Content 4 is %s", content4.get().length());
            log.info(msg);

        } catch (InterruptedException e) {
            String msg = String.format("Thread was interrupted. Cause is %n%s", e.getMessage());
            log.warning(msg);
        } catch (ExecutionException e) {
            String msg = String.format("Execution problem. Cause is %n%s", e.getMessage());
            log.warning(msg);
        }
    }
}
