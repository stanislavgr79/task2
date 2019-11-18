package com.exercises2;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
class FileReader {

    private StringBuilder stringBuilder = new StringBuilder();
    private List<String> searchWords = new ArrayList<>();

    private ClassLoader classLoader = getClass().getClassLoader();
    private File fileText = new File(Objects.requireNonNull(classLoader.getResource("test_text.txt")).getFile());
    private File fileSearchWords = new File(Objects.requireNonNull(classLoader.getResource("search_words.txt")).getFile());

    void fromInputToStringBuilder(File file) throws IOException {

        try (Stream linesStream = Files.lines(file.toPath())) {
            linesStream.forEach(line -> stringBuilder.append(line).append("\n"));
        }
    }

    void fromInputToListString(File file) throws IOException {

        Files.lines(file.toPath()).forEach(o-> searchWords.add(o) );
    }
}

