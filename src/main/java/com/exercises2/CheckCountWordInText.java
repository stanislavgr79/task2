package com.exercises2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toMap;


public class CheckCountWordInText {

    private final Logger LOG = LoggerFactory.getLogger(CheckCountWordInText.class);

    private Pattern paragraph_pattern = Pattern.compile("[\\r\\n\\t]+");
    private Pattern sentence_pattern = Pattern.compile("(?<=[.?!])\\s+(?=[^.!?])");
    private Pattern word_pattern = Pattern.compile("[\\\\D\\s+_*#$%&'@`~<>()\\[\\],.?:;!/\\\\]+?");

    private Map<String, Long> resultMap = new TreeMap<>();

    public LinkedHashMap<String, Long> runner(String text, List<String> wordsCheckList){
        LOG.info("Get text.length=" + text.length() + ", wordsCheckList.size=" + wordsCheckList.size());
        fillResultMap(wordsCheckList);
        textToParagraph(text, wordsCheckList);
        return returnSortedByValueReversedMap(this.resultMap);
    }

    private void fillResultMap(List<String> listTestWords){
        listTestWords.forEach(obj->resultMap.put(obj, 0L));
    }

    private LinkedHashMap<String, Long> returnSortedByValueReversedMap(Map<String, Long> resultMap){
        return resultMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void textToParagraph(String text, List<String> wordsCheckList){
        List<String> paragraphList = Arrays.stream(paragraph_pattern.split(text))
                .map(String::trim).collect(Collectors.toList());
        LOG.info("Get paragraphList.size=" + paragraphList.size());
        paragraphToSentenceList(paragraphList, wordsCheckList);
    }

    private void paragraphToSentenceList(List<String> paragraphList, List<String> wordsCheckList){
        for (String paragraph : paragraphList) {
            Arrays.stream(sentence_pattern.split(paragraph))
                    .forEach(obj -> sentenceToWordList(obj, wordsCheckList));
        }
    }

    private void sentenceToWordList(String sentence, List<String> wordsCheckList){
        countWords(Arrays.stream(word_pattern.split(sentence)).collect(Collectors.toList()), wordsCheckList);
    }

    private void countWords(List<String> wordList, List<String> wordsCheckList)  {
        TreeMap<String, Long> searchMap = new TreeMap<>();
        for(int x=0; x<wordsCheckList.size(); x++){
            int finalX = x;
            wordList.stream()
                    .filter(o -> o.equals(wordsCheckList.get(finalX)))
                    .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, counting()))
                    .forEach(searchMap::put);
        }
        searchMap.forEach((k, v) -> resultMap.merge(k, v, Long::sum));
    }

}


