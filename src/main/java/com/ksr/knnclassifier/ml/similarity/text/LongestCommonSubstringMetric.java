package com.ksr.knnclassifier.ml.similarity.text;

import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.TextEntity;

public class LongestCommonSubstringMetric implements Metric<TextEntity> {

    @Override
    public double distance(final TextEntity content1, final TextEntity content2) {
        String text1 = content1.getContent();
        String text2 = content2.getContent();
        String[] words1 = text1.split(" ");

        String[] words2 = text2.split(" ");
        int m = words1.length;
        int n = words2.length;

        int max = 0;

        int[][] dp = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (words1[i].equals(words2[j])) {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = dp[i - 1][j - 1] + 1;
                    }

                    if (max < dp[i][j])
                        max = dp[i][j];
                }

            }
        }
        return max;
    }

}
