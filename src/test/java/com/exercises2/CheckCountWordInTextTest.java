package com.exercises2;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

public class CheckCountWordInTextTest {


    @Test
    public void shouldReturnLinkedMapOrderReversedValueOfCountWordsInText_ByTextAndListCheckWords() throws IOException {
        LinkedHashMap<String, Long> expected = new LinkedHashMap<>();
        expected.put("слова",13L);
        expected.put("все",3L);
        expected.put("слов",2L);
        expected.put("список",1L);
        expected.put("текст",1L);
        expected.put("дом",0L);

        FileReader fileReader = new FileReader();
        fileReader.fromInputToStringBuilder(fileReader.getFileText());
        fileReader.fromInputToListString(fileReader.getFileSearchWords());
        String text = fileReader.getStringBuilder().toString();
        List<String> listTestWords = fileReader.getSearchWords();

        CheckCountWordInText checkCountWordInText = new CheckCountWordInText();
        LinkedHashMap<String, Long> actual = checkCountWordInText.runner(text, listTestWords);

        Assert.assertEquals(expected, actual);

    }
}