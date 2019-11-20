package com.exercises2;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;


public class CheckCountWordInTextTest {


    @Test
    public void shouldReturnLinkedMapOrderReversedValueOfCountWordsInText_ByTextAndListCheckWords() throws IOException {
        LinkedHashMap<String, Long> expected = new LinkedHashMap<>();
        expected.put("слова",16L);
        expected.put("все",4L);
        expected.put("слов",2L);
        expected.put("текст",2L);
        expected.put("список",1L);
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