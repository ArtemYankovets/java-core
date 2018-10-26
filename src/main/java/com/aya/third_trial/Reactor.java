package com.aya.third_trial;

import lombok.*;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Log
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Reactor {

    private ReactorComponent reactor;

    public CompletableFuture<String> put(Path path) {
        if (reactor == null || reactor.providers.isEmpty() || reactor.isInterrupted()) {
            reactor = startReactor();
        }
        return reactor.put(path);
    }

    private ReactorComponent startReactor() {
        ReactorComponent newReactor = new ReactorComponent();
        newReactor.setName(String.format("Reactor #%s", (new Random()).nextInt()));
        log.info("=====> Thread '" + newReactor.getName() + "' starting...");
        newReactor.start();
        return newReactor;
    }

    private class ReactorComponent extends Thread {
        private static final String EXCEPTION_MSG = "IO exception. Cause: %n%s";
        private List<ContentProvider> providers = new ArrayList<>();

        private final int ATTEMPTS_LIMIT = 3;
        private final AtomicInteger counter = new AtomicInteger(0);

        public CompletableFuture<String> put(Path filePath) {
            synchronized (this) {
                ContentProvider contentProvider = new ContentProvider(filePath);
                providers.add(contentProvider);
                return contentProvider.getContent();
            }
        }

        @Override
        public void run() {
            while ((counter.get() < ATTEMPTS_LIMIT) /*|| (!providers.isEmpty() && counter.get() > 0)*/) {
                try {
                    ContentProvider contentProvider = getAvailableToRead();
                    if (contentProvider == null) {
                        try {
                            log.info("Reactor is waiting for available content...");
                            counter.incrementAndGet();
                            log.info("Reactor sleeping...");
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            String msg = String.format("Thread was interrupted. Cause is %n%s", e.getMessage());
                            log.warning(msg);
                        }
                    } else {
                        int availableBytesToRead = contentProvider.getAvailableBytesToRead();
                        byte[] buffer = new byte[availableBytesToRead];

                        contentProvider.getIn().read(buffer, 0, availableBytesToRead);
                        contentProvider.storeContentData(buffer);
                    }

                } catch (IOException e) {
                    String msg = String.format(EXCEPTION_MSG, e.getMessage());
                    log.warning(msg);
                }
            }
            Thread.currentThread().interrupt();
            log.info("<=x=x= Thread " + Thread.currentThread().getName() + " was interrupted");
        }

        private ContentProvider getAvailableToRead() {
            synchronized (this) {
                ContentProvider availableToReadContentProvider = null;
                if (providers != null && !providers.isEmpty()) {
                    do {
                        for (Iterator<ContentProvider> i = providers.iterator(); i.hasNext(); ) {
                            ContentProvider item = i.next();
                            if (item.isOver()) {
                                item.readLoadedContent();
                                i.remove();
                            } else if (item.getAvailableBytesToRead() > 0) {
                                availableToReadContentProvider = item;
                                break;
                            }
                        }
                    } while (availableToReadContentProvider == null && !providers.isEmpty());
                }
                return availableToReadContentProvider;
            }
        }
    }
}


