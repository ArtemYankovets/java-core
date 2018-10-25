package com.aya.third_trial;

import lombok.*;
import lombok.extern.java.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ReactorComponent extends Thread {

    private List<ContentProvider> providers;

    public CompletableFuture<String> put(Path filePath) {
        try {
            providers.add(
                    new ContentProvider(filePath.toString(),
                    new FileInputStream(filePath.toString()),
                    new ByteArrayOutputStream())
            );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {

    }
}

@AllArgsConstructor
@Getter
@Setter
class ContentProvider {
    private String name;
    private InputStream in;
    private ByteArrayOutputStream out;

}
