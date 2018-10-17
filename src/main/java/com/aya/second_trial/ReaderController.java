package com.aya.second_trial;

import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Log
public class ReaderController {
    public static void main(String[] args) {
        try {
            List<ContentProvider> providers = creatingListOfProviders();
            while (true) {
                ContentProvider availableProvider = ReaderController.getAvailableProvider(providers);


                byte[] contentInBytes = readContentFromProvider(availableProvider);
                if (contentInBytes.length > 0) {
                    availableProvider.accumulateContent(contentInBytes);
                }

                if (availableProvider != null) {
                    String separator =
                            String.format("%n--------------------------------------------------------------------%n");

                    String msg = String.format(
                            "%sCONTENT OF %s is %n\t%s%s",
                            separator,
                            Thread.currentThread().getName(),
                            availableProvider.getStringContent(),
                            separator
                    );
                    log.info(msg);
                }
            }
        } catch (MalformedURLException | InterruptedException e) {
            String msg = String.format("%s has problem with reading data. Cause:%n%s",
                    Thread.currentThread().getName(), e.getMessage());
            log.warning(msg);
        }
    }

    private static ContentProvider getAvailableProvider(List<ContentProvider> providers) {
        return providers.stream()
                .filter(Thread::isAlive)
                .filter(cp -> cp.getAvailableBytesFoReading() > 0)
                .findFirst()
                .orElse(null);
    }

    private static List<ContentProvider> creatingListOfProviders() throws MalformedURLException {
        List<ContentProvider> providerList = new ArrayList<>();

        String urlApple = "https://www.apple.com/";
        ContentProvider providerApple = new ContentProvider(new URL(urlApple));

        String urlImbd = "https://www.imdb.com/";
        ContentProvider providerImbd = new ContentProvider(new URL(urlImbd));

        providerList.add(providerApple);
        providerList.add(providerImbd);

        providerApple.start();
        providerImbd.start();

        return providerList;
    }

    private static byte[] readContentFromProvider(ContentProvider availableProvider) throws InterruptedException {

        byte[] buffer = new byte[0];

        if (availableProvider != null) {
            try {
                int availableBytesFoReading = availableProvider.getAvailableBytesFoReading();
                InputStream in = availableProvider.getIn();

                buffer = new byte[availableBytesFoReading];
                int bytesRead = in.read(buffer, 0, availableBytesFoReading);

                String msg = String.format("Was read from %s %s bytes",
                        availableProvider.getName(), bytesRead);
                log.info(msg);
            } catch (IOException e) {
                String msg = String.format("%s has problem with reading data. Cause:%n%s",
                        Thread.currentThread().getName(), e.getMessage());
                log.warning(msg);
            }
        } else {
            Thread.sleep(5000);
        }

        return buffer;
    }
}
