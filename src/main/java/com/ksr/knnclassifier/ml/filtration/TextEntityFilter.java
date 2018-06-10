package com.ksr.knnclassifier.ml.filtration;

import com.ksr.knnclassifier.model.Entity;
import com.ksr.knnclassifier.model.TextEntity;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TextEntityFilter {

    public List<Entity> filterText(final List<? extends Entity> entities) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            String text = entity.getContent().toString().replaceAll("\n", " ");
            text = text.replaceAll("/", " ");
            text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
            text = text.replaceAll("\\b[\\w']{1,2}\\b", "");
            text = text.replaceAll("\\s{2,}", " ");
            result.add(new TextEntity(text, entity.getLabel()));
        }
        return result;
    }

    public List<Entity> filterStopWords(final List<? extends Entity> entities) {
        List<Entity> result = new ArrayList<>();
        CharArraySet stopWords = CharArraySet.copy(EnglishAnalyzer.getDefaultStopSet());
        for (Entity entity : entities) {
            StandardTokenizer tokenizer = new StandardTokenizer();
            tokenizer.setReader(new StringReader((String) entity.getContent()));
            TokenStream tokenStream = new StopFilter(tokenizer, stopWords);
            CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                tokenizer.reset();
                while (tokenStream.incrementToken()) {
                    stringBuilder.append(attr.toString() + ' ');
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while removing stop words", e);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            result.add(new TextEntity(stringBuilder.toString(), entity.getLabel()));
        }
        return result;
    }

    public List<Entity> extractStems(final List<? extends Entity> entities) {
        List<Entity> result = new ArrayList<>();
        InputStream dictionaryStream = getClass().getResourceAsStream("/hunspell/" + "en_US" + ".dic");
        InputStream affixStream = getClass().getResourceAsStream("/hunspell/" + "en_US" + ".aff");
        Dictionary dictionary = null;
        try {
            dictionary = new Dictionary(new RAMDirectory(), "tmp", affixStream, dictionaryStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException("Error while constructing new Dictionary");
        }
        for (Entity entity : entities) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String token : ((String) entity.getContent()).split(" ")) {
                StandardTokenizer tokenizer = new StandardTokenizer();
                tokenizer.setReader(new StringReader(token));
                CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
                TokenStream tokenStream = new HunspellStemFilter(tokenizer, (dictionary), true);
                List<String> resultTokens = new ArrayList<>();
                try {
                    tokenizer.reset();
                    while (tokenStream.incrementToken()) {
                        resultTokens.add(attr.toString());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error while extracting stems", e);
                }
                stringBuilder.append(resultTokens.get(resultTokens.size() - 1) + " ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            TextEntity newEntity = new TextEntity(stringBuilder.toString(), entity.getLabel());
            result.add(newEntity);
        }
        return result;
    }

}
