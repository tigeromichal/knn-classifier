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
import java.util.List;

public class XmlTextEntityDao implements Dao<List<TextEntity>> {

    private final Logger logger = LoggerFactory.getLogger(XmlTextEntityDao.class);
    private final String labelName;

    public XmlTextEntityDao(final String labelName) {
        this.labelName = labelName;
    }

    @Override
    public List<TextEntity> read(String path) {
        File SGMLFile = new File(path);
        List<TextEntity> entities = new ArrayList<>();
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new InputStreamReader(new FileInputStream(SGMLFile), "UTF-8"));
            Element rootElement = document.getRootElement();
            for (Element reuter : rootElement.getChildren()) {
                Element labelMarkup = reuter.getChild(labelName);
                if (labelMarkup == null) {
                    continue;
                }
                String label = labelMarkup.getText();
                Element bodyMarkup = reuter.getChild("body");
                if (bodyMarkup == null) {
                    continue;
                }
                String body = bodyMarkup.getText();
                entities.add(new TextEntity(body, label));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return entities;
    }

    @Override
    public void write(List<TextEntity> obj, String path) {
        throw new UnsupportedOperationException("Write method not implemented");
    }
}
