package com.ksr.knnclassifier.ml.similarity.text;


import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.TextEntity;

public class LevenshteinMetric implements Metric<TextEntity> {

    @Override
    public double distance(final TextEntity content1, final TextEntity content2) {
        int deletionCost, insertionCost, substitutionCost;
        String[] words1 = content1.getContent().split(" ");
        String[] words2 = content2.getContent().split(" ");

        int text2Length = words2.length;
        int text1Length = words1.length;

        int[] cost = new int[text2Length + 1];
        int[] newCost = new int[text2Length + 1];

        for (int i = 0; i <= text2Length; i++) {
            cost[i] = i;
        }
        for (int i = 1; i <= text1Length; i++) {
            newCost[0] = i;
            for (int j = 1; j <= text2Length; j++) {
                int replaceCost = words1[i - 1].equals(words2[j - 1]) ? 0 : 1;
                deletionCost = newCost[j - 1] + 1;
                insertionCost = cost[j] + 1;
                substitutionCost = cost[j - 1] + replaceCost;

                newCost[j] = deletionCost < insertionCost ? deletionCost : insertionCost;
                if (substitutionCost < newCost[j])
                    newCost[j] = substitutionCost;
            }
            int[] temp = newCost;
            newCost = cost;
            cost = temp;
        }
        return cost[text2Length];
    }

}
