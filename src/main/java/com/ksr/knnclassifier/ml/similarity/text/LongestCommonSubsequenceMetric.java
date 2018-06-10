package com.ksr.knnclassifier.ml.similarity.text;

import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.TextEntity;

public class LongestCommonSubsequenceMetric implements Metric<TextEntity> {

    @Override
    public double distance(final TextEntity content1, final TextEntity content2) {
        String text1 = content1.getContent();
        String text2 = content2.getContent();
        String[] words1 = text1.split(" ");
        String[] words2 = text2.split(" ");

        int m = words1.length;
        int n = words2.length;
        int L[][] = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0)
                    L[i][j] = 0;
                else if (words1[i - 1].equals(words2[j - 1]))
                    L[i][j] = L[i - 1][j - 1] + 1;
                else
                    L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
            }
        }
        return L[m][n];
    }

}
