package com.aya.third_trial;

import lombok.*;
import lombok.extern.java.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Log
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ReactorComponent extends Thread {

    private static final String EXCEPTION_MSG = "IO exception. Cause: %n%s";
    private List<ContentProvider> providers;

//    https://www.baeldung.com/java-thread-stop
    private final int LIMIT = 5;
    private int counter = 0;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public CompletableFuture<String> put(Path filePath) {
        try {
            if (!Thread.currentThread().isAlive()) {
                Thread.currentThread().start();
            }

            providers.add(new ContentProvider(new FileInputStream(filePath.toString())));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {

        while (counter <= LIMIT) {
            try {
                ContentProvider contentProvider = getAvailableToRead();
                if (contentProvider == null) {
                    try {
                        log.info("Reactor is waiting for available content...");
                        counter++;
                        Thread.sleep(1000);
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

    }

    private ContentProvider getAvailableToRead() {
        ContentProvider result = null;
        if (providers != null && !providers.isEmpty()) {
            do {
                for (Iterator<ContentProvider> i = providers.iterator(); i.hasNext(); ) {
                    ContentProvider item = i.next();
                    if (item.isOver()) {
                        i.remove();
                    } else if (item.getAvailableBytesToRead() > 0) {
                        result = item;
                        break;
                    }
                }
            } while (result == null);
        }
        return result;
    }
}

@Log
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
class ContentProvider {
    private static final String EXCEPTION_MSG = "IO exception. Cause: %n%s";
    private String name;
    private InputStream in;
    private ByteArrayOutputStream out;
    private boolean isOver;
    private CompletableFuture<String> content;

    public ContentProvider(InputStream in) {
        this.in = in;

    }

    public int getAvailableBytesToRead() {
        int availableBytesToRead = 0;
        try {
            availableBytesToRead = in.available();
        } catch (IOException e) {
            String msg = String.format(EXCEPTION_MSG, e.getMessage());
            log.warning(msg);
        }
        return availableBytesToRead;
    }

    public void storeContentData(byte[] readBytes) {
        try (ByteArrayOutputStream contentStarage = new ByteArrayOutputStream()) {
            contentStarage.write(out.toByteArray(), 0, out.size());
            contentStarage.write(readBytes, 0, readBytes.length);
            contentStarage.flush();
            out = contentStarage;
        } catch (IOException e) {
            String msg = String.format(EXCEPTION_MSG, e.getMessage());
            log.warning(msg);
        }
    }

    public CompletableFuture<String> getContent() {
        return content.complete(out.toByteArray());
    }
}
