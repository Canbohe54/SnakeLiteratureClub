package com.snach.literatureclub.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class TextExtractionTools {

    /**
     * 从DOCX文件中提取文本。
     *
     * @param filePath DOCX文件的路径。
     * @return 提取的文本，每个段落和表格单元格的内容以换行符分隔。
     * @throws IOException 如果读取文件发生错误。
     */
    public static String extractTextFromDocx(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            XWPFDocument document = new XWPFDocument(fis);

            // 遍历文档中的所有段落，将段落文本添加到StringBuilder
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                sb.append(paragraph.getText()).append("\n");
            }

            // 遍历文档中的所有表格，将表格中所有单元格的文本添加到StringBuilder
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph para : cell.getParagraphs()) {
                            sb.append(para.getText()).append("\n");
                        }
                    }
                }
            }

        }

        return sb.toString().trim(); // 返回处理后的字符串，移除首尾可能的空白字符
    }
}
