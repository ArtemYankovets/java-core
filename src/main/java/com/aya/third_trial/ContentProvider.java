package com.aya.third_trial;

import lombok.*;
import lombok.extern.java.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

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

    public ContentProvider(Path path) {
        String filePath = path.toString();
        try {
            init(filePath);
        } catch (FileNotFoundException e) {
            String msg = String.format("File '%s' was not found. Cause %n%s", filePath, e.getMessage());
            log.warning(msg);
        }
    }

    private void init(String filePath) throws FileNotFoundException {
        name = filePath;
        in = new FileInputStream(filePath);
        out = new ByteArrayOutputStream();
        isOver = false;
        content = new CompletableFuture<>();
    }

    public int getAvailableBytesToRead() {
        int availableBytesToRead = 0;
        try {
            availableBytesToRead = in.available();
            if (availableBytesToRead == 0) {
                isOver = true;
            }
        } catch (IOException e) {
            String msg = String.format(EXCEPTION_MSG, e.getMessage());
            log.warning(msg);
        }
        return availableBytesToRead;
    }

    public void storeContentData(byte[] readBytes) {
        try (ByteArrayOutputStream contentStorage = new ByteArrayOutputStream()) {
            contentStorage.write(out.toByteArray(), 0, out.size());
            contentStorage.write(readBytes, 0, readBytes.length);
            contentStorage.flush();
            out = contentStorage;
        } catch (IOException e) {
            String msg = String.format(EXCEPTION_MSG, e.getMessage());
            log.warning(msg);
        }
    }

    public void readLoadedContent() {
        String resultContent = null;
        try {
            resultContent = out.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        content.complete(resultContent);
    }
}
