package org.example;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportFromWord {


public  String importFromDoc(){
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame();
        frame.setVisible(true);
        FileDialog fd = new FileDialog(frame, "Choose a doc file", FileDialog.LOAD);
       // fd.setFile("*.doc");
        fd.setMultipleMode(true);
        // Show the dialog and get the selected file name
        fd.setVisible(true);
        //frame.setVisible(false);
        frame.dispose();
        String selectedFileName="";
        String message="";
        if(fd.getFiles()!=null && fd.getFiles().length!=0){
            for (var sf: fd.getFiles()) {
                selectedFileName= sf.getName();
                try {
                    if (selectedFileName.contains(".docx")){

                        FileInputStream fis = new FileInputStream(sf);
                        XWPFDocument doc = new XWPFDocument(fis);
                        // Extract the text of the document
                        XWPFWordExtractor extractor = new XWPFWordExtractor(doc);

                        var dfd= extractor.getText().split("\n");
                        int length=0;
                        List<List<String>> docxPages = new ArrayList();
                        List<String> docxPage = new ArrayList();

                        for (String paragraph:
                                dfd) {


                            docxPage.add(paragraph);
                            docxPage.add("\n");
                            length += paragraph.length();
                            if (length>1500) {
                                docxPage.removeIf(p->p== null);
                                docxPages.add(docxPage);
                                length = 0;
                                docxPage= new ArrayList();
                            }

                        }
                        //}
                        if (docxPage.size()!=0){
                            docxPages.add(docxPage);
                        }

                        //  Close the document and file input stream
                        doc.close();
                        fis.close();
                        addBookDataBase(selectedFileName,docxPages,".docx");
                        message+= "تم اضافة: " + selectedFileName+"\n";

                    }else if (selectedFileName.contains(".doc")) {
                        POIFSFileSystem fs = null;
                        fs = new POIFSFileSystem (new FileInputStream (sf.getPath()));
                        HWPFDocument doc = new HWPFDocument (fs);
                        WordExtractor we = new WordExtractor (doc);
                        String[] paragraphs = we.getParagraphText();
                        int length=0;
                        List<List<String>> pages = new ArrayList();
                        ArrayList<String> page = new ArrayList();
                        for (String paragraph:
                                paragraphs) {
                            //paragraph.replaceAll("[\u0001\u000b\u0007]","\r\n");
                            page.add(paragraph);
                            length += paragraph.length();
                            if (length>1500) {
                                page.removeIf(p->p == null);
                                pages.add(page);
                                length = 0;
                                page= new ArrayList();
                            }

                        }
                        if (page.size()!=0){
                            pages.add(page);
                        }
                        doc.close();
                        fs.close();
                        addBookDataBase(selectedFileName,pages,".doc");

                        message+= "تم اضافة: " + selectedFileName+"\n";
                    }
                    else {
                        message+=  "صيغة غير مدعومة: " + selectedFileName+"\n";
                    }

                }catch (Exception e){
                    message+= "حدث خطأ: " + selectedFileName+"\n";
                }


            }
            return "Success "+message;
        }else {
            return "";
        }

    }catch (Exception e){
        return e.getMessage();
    }
  ////  POIFSFileSystem fs = null;
 ////   String fileName="D:\\المكتبة الشاملة\\آداب الصحبة لأبي عبد الرحمن السلمي ط الصحابة\\Word\\آداب الصحبة لأبي عبد الرحمن السلمي ط الصحابة.doc";
 ////   fs = new POIFSFileSystem (new FileInputStream (fileName));

  ////  HWPFDocument doc = new HWPFDocument (fs);

}

public void addBookDataBase(String selectedFileName,List<List<String>> pages,String extention) throws Exception{
    String bookId = MyServices.getMaxBookIdFromSqlite();
    Book newBook = new Book();
    newBook.bookId = bookId;
    newBook.name = selectedFileName.replace(extention,"");

    newBook.catId="42";
    newBook.betaka="";
    List<Page>listPages= new ArrayList<>();
    for (int i =0 ; i<pages.size();i++ ) {
        Page newPage = new Page();
        newPage.nass="";
        for (String text:
                pages.get(i)) {
            newPage.nass+=text;
        }
        var par=newPage.nass.replaceAll("[\u0001\u000b\u0007]","\n");
        par=par.replaceAll("(\r\n{1,})","\n");
        par=par.replaceAll("(\r{2,})","\r");
        par=par.replaceAll("(\n{2,})","\n");
        par=par.replaceAll("(\r\n{1,})","\n");
        newPage.nass=par;
        newPage.id =(i+1)+"";
        newPage.page =(i+1)+"";
        newPage.part ="1";
        newPage.bookId =bookId;
        newPage.sora="0";
        newPage.aya="0";
        listPages.add(newPage);
    }

    Class.forName("org.sqlite.JDBC");
    Connection connLite = DriverManager.getConnection(MyServices.sql_Connetion_Url+"");
    Statement stmt = connLite.createStatement();
    MyServices.insertBook(bookId, newBook,stmt);
    MyServices.insertPage(bookId, listPages, stmt);
    stmt.close();
}
}
