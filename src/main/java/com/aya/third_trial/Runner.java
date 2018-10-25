package com.aya.third_trial;

import lombok.extern.java.Log;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log
public class Runner {
    public static void main(String[] args) {

        try {
            ReactorComponent reactorComponent = new ReactorComponent();
            reactorComponent.start();

            CompletableFuture<String> content1 =
                    reactorComponent.put(Paths.get("src/main/resources/books/2015 Gold FCE SB.pdf"));

            String msg = String.format("Content is %s", content1.get());
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
