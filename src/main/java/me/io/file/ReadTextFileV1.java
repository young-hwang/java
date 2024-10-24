package me.io.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ReadTextFileV1 {
    public static String PATH = "temp/hello2.txt";

    public static void main(String[] args) throws IOException {
        String writeString = "abc\n가나다";
        System.out.println("=== Write String ===");
        System.out.println(writeString);

        Path path = Path.of(PATH);

        // 파일에서 쓰기
        Files.writeString(path, writeString, UTF_8);

        // 파일에서 읽기
        String readString = Files.readString(path, UTF_8);

        System.out.println("=== Read String ===");
        System.out.println(readString);

    }
}
