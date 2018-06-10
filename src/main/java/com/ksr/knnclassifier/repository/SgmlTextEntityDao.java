package com.ksr.knnclassifier.repository;

import com.ksr.knnclassifier.model.TextEntity;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SgmlTextEntityDao implements Dao<List<TextEntity>> {

    private final List<String> placeFilter = Arrays.asList("west-germany", "usa", "france", "uk", "canada", "japan");
    private final List<String> topicFilter = Arrays.asList("earn", "acq", "money-supply", "money-fix", "interest", "coffee", "gold");
    private final Logger logger = LoggerFactory.getLogger(SgmlTextEntityDao.class);
    private final String labelName;

    public SgmlTextEntityDao(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public List<TextEntity> read(final String path) {
        File SGMLFile = new File(path);
        List<TextEntity> entities = new ArrayList<>();
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new InputStreamReader(new FileInputStream(SGMLFile), "UTF-8"));
            Element rootElement = document.getRootElement();
            entities = filterArticlesFromReuters(rootElement);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return entities;
    }

    @Override
    public void write(List<TextEntity> obj, final String path) {
        throw new UnsupportedOperationException("Write method not implemented");
    }

    private List<TextEntity> filterArticlesFromReuters(Element rootElement) {
        List<TextEntity> entities = new ArrayList<>();
        for (Element reuter : rootElement.getChildren()) {
            String label = null;
            switch (labelName) {
                case "place":
                    Element placesMarkup = reuter.getChild("PLACES");
                    if (placesMarkup.getChildren().size() != 1) {
                        continue;
                    }
                    String place = placesMarkup.getChildren().get(0).getText();
                    if (!placeFilter.contains(place)) {
                        continue;
                    }
                    label = place;
                    break;
                case "topic":
                    Element topicsMarkup = reuter.getChild("TOPICS");
                    if (topicsMarkup.getChildren().size() != 1) {
                        continue;
                    }
                    String topic = topicsMarkup.getChildren().get(0).getText();
                    if (!topicFilter.contains(topic)) {
                        continue;
                    }
                    label = topic;
                    break;
            }
            Element textMarkup = reuter.getChild("TEXT");
            Element bodyMarkup = textMarkup.getChild("BODY");
            if (bodyMarkup == null) {
                continue;
            }
            String body = bodyMarkup.getText();
            entities.add(new TextEntity(body, label));
        }
        return entities;
    }
}
