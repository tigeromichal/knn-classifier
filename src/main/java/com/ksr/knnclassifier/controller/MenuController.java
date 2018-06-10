package com.ksr.knnclassifier.controller;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.ksr.knnclassifier.ml.classification.Classifier;
import com.ksr.knnclassifier.ml.classification.KNN;
import com.ksr.knnclassifier.ml.extraction.*;
import com.ksr.knnclassifier.ml.filtration.TextEntityFilter;
import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.ml.similarity.frequencymatrix.CosineSimilarity;
import com.ksr.knnclassifier.ml.similarity.frequencymatrix.NGramSimilarity;
import com.ksr.knnclassifier.ml.similarity.vector.ChebyshevMetric;
import com.ksr.knnclassifier.ml.similarity.frequencymatrix.CustomSimilarity;
import com.ksr.knnclassifier.ml.similarity.vector.EuclideanMetric;
import com.ksr.knnclassifier.ml.similarity.vector.TaxicabMetric;
import com.ksr.knnclassifier.model.Entity;
import com.ksr.knnclassifier.repository.Dao;
import com.ksr.knnclassifier.repository.FileMatrixDao;
import com.ksr.knnclassifier.repository.SgmlTextEntityDao;
import com.ksr.knnclassifier.repository.XmlTextEntityDao;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

public class MenuController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    List<Entity> entities;
    @FXML
    private ChoiceBox metricsChoiceBox;
    @FXML
    private ChoiceBox extractorChoiceBox;
    @FXML
    private ChoiceBox dataSetChoiceBox;
    @FXML
    private TextField kParameterTextField;
    @FXML
    private TextField trainingDataPercentageTextField;
    @FXML
    private TextField categoryNameTextField;
    @FXML
    private TextField nGramTextField;
    @FXML
    private TextField topFrequentPercentageTextField;
    @FXML
    private Button loadDataButton;
    @FXML
    private Button classifyButton;
    @FXML
    private Button saveResultsButton;
    @FXML
    private ProgressBar classifyProgressBar;
    @FXML
    private GridPane confusionGridPane;
    @FXML
    private Label accuracyLabel;

    private Task classificationTask;
    private Table<String, String, Integer> confusionMatrix;
    private double accuracy;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList metricsChoiceBoxItems = FXCollections.observableArrayList("EuclideanMetric",
                "ChebyshevMetric", "TaxicabMetric", "CosineSimilarity", "NGramSimilarity", "CustomSimilarity");
        metricsChoiceBox.setItems(metricsChoiceBoxItems);
        ObservableList extractorChoiceBoxItems = FXCollections.observableArrayList("CountExtractor",
                "TfidfExtractor", "FrequencyMatrixExtractor", "NgramExtractor", "CustomExtractor");
        extractorChoiceBox.setItems(extractorChoiceBoxItems);
        extractorChoiceBox.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, str2) -> {
            String selectedExtractor = extractorChoiceBox.getSelectionModel().getSelectedItem().toString();
            switch (selectedExtractor) {
                case "NgramExtractor":
                    nGramTextField.setVisible(true);
                    topFrequentPercentageTextField.setVisible(false);
                    break;
                case "CustomExtractor":
                    nGramTextField.setVisible(true);
                    topFrequentPercentageTextField.setVisible(true);
                    break;
                default:
                    nGramTextField.setVisible(false);
                    topFrequentPercentageTextField.setVisible(false);
            }
        });
        ObservableList dataSetChoiceBoxItems = FXCollections.observableArrayList("reuters", "custom");
        dataSetChoiceBox.setItems(dataSetChoiceBoxItems);
        dataSetChoiceBox.getSelectionModel().selectFirst();
        loadDataButton.setOnAction(event -> {
            if (dataSetChoiceBox.getSelectionModel().getSelectedItem().toString().equals("reuters")) {
                loadReuters();
                logger.info("Loaded reuters");
            } else if (dataSetChoiceBox.getSelectionModel().getSelectedItem().toString().equals("custom")) {
                loadEntitiesFrom("sentences.xml");
                logger.info("Loaded custom sentences");
            }
            TextEntityFilter textEntityFilter = new TextEntityFilter();
            entities = textEntityFilter.filterText(entities);
            entities = textEntityFilter.filterStopWords(entities);
            entities = textEntityFilter.extractStems(entities);
        });
        classifyButton.setOnAction(event -> {
            if (entities != null) {
                classificationTask = createTask();
                classifyProgressBar.progressProperty().unbind();
                classifyProgressBar.progressProperty().bind(classificationTask.progressProperty());
                classificationTask.messageProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.equals("finished")) {
                        classifyProgressBar.progressProperty().unbind();
                        classifyProgressBar.setProgress(0);
                        confusionGridPane.getChildren().clear();
                        int i = 0, j;
                        for (String rowLabel : new TreeSet<>(confusionMatrix.rowKeySet())) {
                            j = 0;
                            Label label = new Label(rowLabel);
                            label.getStyleClass().add("confusionLabel");
                            confusionGridPane.add(label, j, i + 1);
                            for (String columnLabel : new TreeSet<>(confusionMatrix.columnKeySet())) {
                                if (i == 0) {
                                    label = new Label(columnLabel);
                                    label.getStyleClass().add("confusionLabel");
                                    confusionGridPane.add(label, j + 1, i);
                                }
                                label = new Label(Integer.toString(confusionMatrix.contains(rowLabel, columnLabel) ?
                                        confusionMatrix.get(rowLabel, columnLabel) : 0));
                                label.getStyleClass().add("confusionValue");
                                confusionGridPane.add(label, j + 1, i + 1);
                                j++;
                            }
                            i++;
                        }
                        accuracyLabel.setText("Accuracy:" + Double.valueOf(accuracy).toString());
                        Dao dao = new FileMatrixDao();
                        dao.write(confusionMatrix, "matrix.txt");
                    }
                });
                new Thread(classificationTask).start();
            } else {
                logger.error("Articles should not be null");
            }
        });
        saveResultsButton.setOnAction(event -> {
            try {
                saveResults();
            } catch (Exception e) {
                logger.error("Saving graph to file aborted");
            }
        });
    }

    private Task createTask() {
        return new Task() {
            @Override
            protected Object call() {
                final double trainingDataPercentage = Double.valueOf(trainingDataPercentageTextField.getText());
                final String labelName = categoryNameTextField.getText();
                final int k = Integer.valueOf(kParameterTextField.getText());

                final int trainingArticlesNumber = (int) (trainingDataPercentage * entities.size());
                List<Entity> trainEntities = entities.subList(0, trainingArticlesNumber);
                List<Entity> testArticles = entities.subList(trainingArticlesNumber, entities.size());

                final String metricsName = metricsChoiceBox.getSelectionModel().getSelectedItem().toString();
                Metric metric = null;
                switch (metricsName) {
                    case "EuclideanMetric":
                        metric = new EuclideanMetric();
                        break;
                    case "ChebyshevMetric":
                        metric = new ChebyshevMetric();
                        break;
                    case "TaxicabMetric":
                        metric = new TaxicabMetric();
                        break;
                    case "CosineSimilarity":
                        metric = new CosineSimilarity();
                        break;
                    case "NGramSimilarity":
                        metric = new NGramSimilarity();
                        break;
                    case "CustomSimilarity":
                        metric = new CustomSimilarity();
                        break;
                }

                if (Arrays.asList(new String[]{"EuclideanMetric", "ChebyshevMetric", "TaxicabMetric", "CosineSimilarity", "NGramSimilarity", "CustomSimilarity"}).contains(metricsName)) {
                    final String extractorName = extractorChoiceBox.getSelectionModel().getSelectedItem().toString();
                    FeatureExtractor extractor = null;
                    switch (extractorName) {
                        case "CountExtractor":
                            extractor = new CountExtractor();
                            break;
                        case "TfidfExtractor":
                            extractor = new TfidfExtractor();
                            break;
                        case "FrequencyMatrixExtractor":
                            extractor = new FrequencyMatrixExtractor();
                            break;
                        case "NgramExtractor":
                            extractor = new NgramExtractor(Integer.parseInt(nGramTextField.getText()));
                            break;
                        case "CustomExtractor":
                            extractor = new CustomExtractor(Integer.parseInt(nGramTextField.getText()), Double.valueOf(topFrequentPercentageTextField.getText()));
                            break;
                    }

                    extractor.buildDictionary(trainEntities);

                    List<Entity> extractedEntities = new ArrayList<>();
                    for (Entity entity : entities) {
                        extractedEntities.add(extractor.extract(entity));
                    }
                    entities = extractedEntities;
                    trainEntities = entities.subList(0, trainingArticlesNumber);
                    testArticles = entities.subList(trainingArticlesNumber, entities.size());
                }

                confusionMatrix = HashBasedTable.create();
                int n = testArticles.size();
                Classifier classifier = new KNN(metric, k);
                int correctCount = 0;

                double startTime = System.nanoTime();

                for (int i = 0; i < n; i++) {
                    Entity entity = testArticles.get(i);
                    String predictedLabel = classifier.classify(trainEntities, entity);
                    String label = entity.getLabel();
                    logger.info(predictedLabel + " " + label);
                    confusionMatrix.put(predictedLabel, label, confusionMatrix.contains(predictedLabel, label) ? confusionMatrix.get(predictedLabel, label) + 1 : 1);
                    if (predictedLabel.equals(label)) {
                        correctCount++;
                    }
                    if (i % 10 == 0) {
                        updateProgress(i, n);
                    }
                }

                double elapsedTime = (System.nanoTime() - startTime) / 1000000000.0;

                updateMessage("finished");
                accuracy = correctCount * 1.0 / (n * 1.0);
                logger.info("Correct: " + Double.toString(accuracy) + "%");
                logger.info("time : " + Double.toString(elapsedTime));
                return true;
            }
        };
    }

    private void loadReuters() {
        String labelName = categoryNameTextField.getText();
        SgmlTextEntityDao dao = new SgmlTextEntityDao(labelName);
        entities = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            String path = "data/reuters/reut2-0" + String.format("%02d", i) + ".sgm";
            entities.addAll(dao.read(path));
        }
    }

    private void loadEntitiesFrom(final String path) {
        String labelName = categoryNameTextField.getText();
        XmlTextEntityDao dao = new XmlTextEntityDao(labelName);
        entities = new ArrayList<>();
        entities.addAll(dao.read("data/own/" + path));
    }

    private void saveResults() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            throw new NullPointerException("File should not be null");
        }
        Dao dao = new FileMatrixDao();
        dao.write(confusionMatrix, file.getPath());
    }

}
