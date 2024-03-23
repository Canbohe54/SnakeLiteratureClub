package com.snach.literatureclub.utils;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.*;

/**
 * @author yvioo。
 */

public class File2PdfTools {

    /**
     * 验证License 若不验证则转化出的pdf文档会有水印产生
     * @return
     */
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = File2PdfTools.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * word转pdf
     * inpath: 输入word的路径
     * outpath: 输出pdf的路径
     */
    public  void word2Pdf2(String inpath,String outpath) throws Exception {
        if (!getLicense()) {
            System.out.println("非法------------");
            return;
        }

        long old = System.currentTimeMillis();
        File file = new File(outpath);

        FileOutputStream os = new FileOutputStream(file);

        //解决乱码
        //如果是windows执行，不需要加这个
        //TODO 如果是linux执行，需要添加这个*****
        //FontSettings.setFontsFolder("/usr/share/fonts",true);

        Document doc = new Document(inpath);

        //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
        doc.save(os, SaveFormat.PDF);
        long now = System.currentTimeMillis();
        System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
    }


    /**
     * word转pdf
     * @param wordInput word输入流
     */
    public static byte[] File2Pdf(InputStream wordInput) throws IOException {
        if (!getLicense()) {
            System.out.println("非法的许可证");
            return null;
        }

        //新建一个空白pdf文档
        long old = System.currentTimeMillis();
        // 创建临时pdf文件
        File tempFile = File.createTempFile("tempFile", ".pdf");//创建临时文件
        String tempPath = tempFile.getCanonicalPath();
        FileOutputStream os = new FileOutputStream(tempFile);
        FileInputStream is = new FileInputStream(tempFile);

        //Address是将要被转化的word文档
        Document docx = null;
        try {
            docx = new Document(wordInput);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            docx.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回pdf文件的字节数组
        byte[] pdfBytes = is.readAllBytes();
        is.close();
        os.close();
        tempFile.delete();
        long now = System.currentTimeMillis();
        //转化用时
        System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        return pdfBytes;
    }
}
