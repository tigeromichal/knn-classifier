package com.ksr.knnclassifier.repository;

import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.TreeSet;

public class FileMatrixDao implements Dao<Table> {

    private final Logger logger = LoggerFactory.getLogger(FileMatrixDao.class);

    @Override
    public Table read(String path) {
        throw new UnsupportedOperationException("Read method not implemented");
    }

    @Override
    public void write(Table obj, String path) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            writer.write("\\begin{table}[]\n" + "\\centering\n" + "\\caption{My caption}\n" + "\\label{my-label}\n"
                    + "\\begin{tabular}{lllll}\n");
            int i = 0, j = 0;
            for (String columnLabel : new TreeSet<String>(obj.columnKeySet())) {
                writer.write(" & " + columnLabel);
            }
            writer.write(" \\" + "\n");
            for (String rowLabel : new TreeSet<String>(obj.rowKeySet())) {
                j = 0;
                writer.write(rowLabel);
                for (String columnLabel : new TreeSet<String>(obj.columnKeySet())) {
                    writer.write(" & " + Integer.toString(obj.contains(rowLabel, columnLabel) ?
                            (int) obj.get(rowLabel, columnLabel) : 0));
                    j++;
                }
                writer.write(" \\" + "\n");
                i++;
            }
            writer.write("\\end{tabular}\n" + "\\end{table}");
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

    }

}
