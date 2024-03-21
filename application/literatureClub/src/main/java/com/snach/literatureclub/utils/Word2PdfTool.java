package com.snach.literatureclub.utils;

import com.aspose.words.Document;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveOptions;

import java.io.InputStream;
import java.io.OutputStream;

public class Word2PdfTool {
    public static void toPdf(InputStream word, OutputStream target) throws Exception {
        Document doc = new Document(word);
        PdfSaveOptions saveOp = new PdfSaveOptions();
        doc.save(target, saveOp);
    }
}
