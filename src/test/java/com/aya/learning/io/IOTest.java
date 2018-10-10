package com.aya.learning.io;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IOTest {

    @Test
    public void testOfReadingFromFile() throws IOException {

        Path dirPath = Paths.get("src/main/resources/books/");
        File file = new File(dirPath + "/1.txt");
        assertEquals("src\\main\\resources\\books\\1.txt", file.getPath());

        // Get list of file
        List<Path> listFilePaths = Files.list(dirPath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        listFilePaths.forEach(System.out::println);

        List<Reader> readers = new ArrayList<>();
        List<InputStream> inputStreams = new ArrayList<>();
        for (Path path : listFilePaths) {
            String strPath = path.toString();
            readers.add(new InputStreamReader(new FileInputStream(strPath)));
            inputStreams.add(new FileInputStream(strPath));
        }

        try (InputStream is = inputStreams.get(0)) {

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
        }




//        while (select(inputStreams).


//        try (InputStream is = new FileInputStream(fileName)) {
//
//            byte[] buffer = new byte[is.available()];
//            is.read(buffer);
//
//            int i = 0;
//
//            for (byte b: buffer) {
//
//                if (i % 10 == 0) {
//                    System.out.println();
//                }
//
//                System.out.printf("%02x ", b);
//
//                i++;
//            }
//        }

//        InputStream inputStream = new FileInputStream(file.getPath());
//        Reader inputStreamReader = new InputStreamReader(inputStream);
//        String line = null;
//        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
//            while((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//        }

//        ForkJoinPool ioPool = new ForkJoinPool(8);
//        ForkJoinTask<?> tasks = ioPool.submit(
//                () -> pathList.parallelStream().forEach();
//                        tasks.get();

//
//        List<Reader> readers = new ArrayList<>();
//
//        List<Reader> collect = Files.list(dirPath)
//                .filter(Files::isRegularFile)
//                .map(f -> Files.newBufferedReader(f.toAbsolutePath()))
//                .collect(Collectors.toList());
//
//
//        try() {
//
//
//
//            int data = inputStreamReader.read();
//            while(data != -1){
//                char theChar = (char) data;
//                data = inputStreamReader.read();
//            }
//
//            inputStreamReader.close();
//        }

    }

//    private boolean isAvailableSttream (List<InputStream> inputStreams) {
//        if (inputStreams.isEmpty()) {
//            return false;
//        }
//    }
//
//    @SneakyThrows
//    private Optional<InputStreamHelper> select(List<InputStream> inputStreams) {
//        return inputStreams.stream()
//                .map(inputStream -> new InputStreamHelper(inputStream.available(), inputStream))
//                .filter(InputStreamHelper::isAvailable)
//                .findFirst();
//    }

    @AllArgsConstructor
    @Getter
    class InputStreamHelper {
        private int numberOfBytes;
        private InputStream inputStream;

        public boolean isAvailable() {
            return numberOfBytes > 0;
        }
    }
}
