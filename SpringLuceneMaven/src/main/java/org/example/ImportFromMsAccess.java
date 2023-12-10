package org.example;

import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


@Service
public class ImportFromMsAccess {
    public String getData(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            frame.setVisible(true);
            FileDialog fd = new FileDialog(frame, "Choose a bok file", FileDialog.LOAD);
            fd.setFile("*.bok");

            fd.setMultipleMode(true);
            // Show the dialog and get the selected file name
            fd.setVisible(true);
            //frame.setVisible(false);
            frame.dispose();
            //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            String message="";
            if(fd.getFiles()!=null && fd.getFiles().length!=0){
                for (var sf: fd.getFiles()) {
                    try {
                        var directory = sf.getPath();

                        // Create a ProcessBuilder object with the exe file name and the arguments
                        ProcessBuilder pb = new ProcessBuilder(MyServices.current_app_location+"\\ImportBok.exe", directory);

                        // Start the process and get a Process object
                        Process p = pb.start();
                        InputStream is=   p.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        String fline="";

                        while ((line = br.readLine()) != null) {
                            fline+=line;
                        }
                        p.waitFor();
                        if(fline.equals("ok")){
                            String filename=directory.replace(".bok",".mdb");
                            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

                            Connection conn=DriverManager.getConnection(
                                    "jdbc:ucanaccess://"+filename);

                            Statement s = conn.createStatement();
                            ArrayList<String> TablesNames =new ArrayList<>();
                            try (ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null)) {
                                while (rsMD.next()) {
                                    String tblName = rsMD.getString("TABLE_NAME");
                                    TablesNames.add(tblName);

                                }
                            }
                            String tit = "";
                            String bok = "";
                            for (String _tabelName:
                                    TablesNames) {
                                if (_tabelName.startsWith("b")) {
                                    bok=_tabelName;
                                }
                                if (_tabelName.startsWith("t")) {
                                    tit=_tabelName;
                                }
                            }
                            String bookId =  MyServices.getMaxBookIdFromSqlite();
                            ResultSet rs = s.executeQuery("SELECT * FROM [Main]");

                            rs.next();


                            Book book = new Book(rs,bookId);

                            rs.close();
                            ResultSet rsPages = s.executeQuery("SELECT * FROM ["+bok+"]");
                            ArrayList<Page> pageList = new ArrayList<>();
                            while (rsPages.next()) {
                                Page page = new Page(rsPages,bookId);
                                pageList.add(page);
                            }
                            ResultSet rsTitles = s.executeQuery("SELECT * FROM ["+tit+"]");
                            ArrayList<Title> titleList = new ArrayList<>();

                            while (rsTitles.next()) {
                                Title title = new Title(rsTitles,bookId);
                                titleList.add(title);

                            }
                            Class.forName("org.sqlite.JDBC");
                            Connection connLite = DriverManager.getConnection(MyServices.sql_Connetion_Url+"");
                            Statement stmt = connLite.createStatement();
                            MyServices.insertBook(bookId, book,stmt);
                            MyServices.insertPage(bookId, pageList, stmt);
                            MyServices.insertTitle(bookId, titleList);
                            conn.close();
                            stmt.close();
                            Path path = Paths.get(filename);
                            boolean result = Files.deleteIfExists(path);
                            message+="تم اضافة: "+book.name+"\n";
                        }
                    }catch (Exception e){
                        message+="يوجد خطأ: "+sf.getName();
                    }


                }


                return "Success "+message;
            }

//            JFileChooser fileChooser = new JFileChooser(".");
//            fileChooser.setName("choose files in bok extention");
//
//            fileChooser.setMultiSelectionEnabled(false);
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            fileChooser.setMultiSelectionEnabled(true);
//            int option = fileChooser.showOpenDialog(null);
//            if (option == JFileChooser.APPROVE_OPTION) {
//                for (var sf: fileChooser.getSelectedFiles()) {
//                    var directory = sf.getPath();
//
//                    // Create a ProcessBuilder object with the exe file name and the arguments
//                    ProcessBuilder pb = new ProcessBuilder(MyServices.current_app_location+"\\ImportBok.exe", directory);
//
//                    // Start the process and get a Process object
//                    Process p = pb.start();
//                    InputStream is=   p.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(is);
//                    BufferedReader br = new BufferedReader(isr);
//                    String line;
//                    String fline="";
//
//                    while ((line = br.readLine()) != null) {
//                        fline+=line;
//                    }
//                    p.waitFor();
//                    if(fline.equals("ok")){
//                     String filename=directory.replace(".bok",".mdb");
//                    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
//
//                    Connection conn=DriverManager.getConnection(
//                            "jdbc:ucanaccess://"+filename);
//
//                    Statement s = conn.createStatement();
//                    ArrayList<String> TablesNames =new ArrayList<>();
//                    try (ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null)) {
//                        while (rsMD.next()) {
//                            String tblName = rsMD.getString("TABLE_NAME");
//                            TablesNames.add(tblName);
//
//                        }
//                    }
//                    String tit = "";
//                    String bok = "";
//                    for (String _tabelName:
//                            TablesNames) {
//                        if (_tabelName.startsWith("b")) {
//                            bok=_tabelName;
//                        }
//                        if (_tabelName.startsWith("t")) {
//                            tit=_tabelName;
//                        }
//                    }
//                    String bookId =  MyServices.getMaxBookIdFromSqlite();
//                    ResultSet rs = s.executeQuery("SELECT * FROM [Main]");
//
//                    rs.next();
//
//
//                    Book book = new Book(rs,bookId);
//
//                    rs.close();
//                    ResultSet rsPages = s.executeQuery("SELECT * FROM ["+bok+"]");
//                    ArrayList<Page> pageList = new ArrayList<>();
//                    while (rsPages.next()) {
//                        Page page = new Page(rsPages,bookId);
//                        pageList.add(page);
//                    }
//                    ResultSet rsTitles = s.executeQuery("SELECT * FROM ["+tit+"]");
//                    ArrayList<Title> titleList = new ArrayList<>();
//
//                    while (rsTitles.next()) {
//                        Title title = new Title(rsTitles,bookId);
//                        titleList.add(title);
//
//                    }
//                    Class.forName("org.sqlite.JDBC");
//                    Connection connLite = DriverManager.getConnection(MyServices.sql_Connetion_Url+"");
//                    Statement stmt = connLite.createStatement();
//                    MyServices.insertBook(bookId, book,stmt);
//                    MyServices.insertPage(bookId, pageList, stmt);
//                    MyServices.insertTitle(bookId, titleList);
//                    conn.close();
//                    stmt.close();
//                    Path path = Paths.get(filename);
//                    boolean result = Files.deleteIfExists(path);
//                    System.out.println(book.name);
//
//                    }
//
//                }
//
//
//                return "ok";
//            }

            return "";
        }catch (Exception e){
            return e.getMessage();
        }
    }
//public String getData(String filename){
//    try {
////        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
////
////
////        JFileChooser fileChooser = new JFileChooser(".");
////
////        fileChooser.setMultiSelectionEnabled(false);
////        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
////        int option = fileChooser.showOpenDialog(null);
////        if (option == JFileChooser.APPROVE_OPTION) {
////            for (var sf: fileChooser.getSelectedFiles()) {
////                var directory = sf.getAbsolutePath();
////
////            }
////
////
//////            Connection conn=DriverManager.getConnection(
//////                    "jdbc:ucanaccess://"+directory);
////
////        }
////
//
//        //AccessConverter converter = new AccessConverter("","","",9,true,true);
//        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
//
//        Connection conn=DriverManager.getConnection(
//                "jdbc:ucanaccess://"+filename);
////        Connection conn=DriverManager.getConnection(
////                "jdbc:ucanaccess://D:\\المكتبة الشاملة\\db1.mdb;");
//        Statement s = conn.createStatement();
//        ArrayList<String> TablesNames =new ArrayList<>();
//        try (ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null)) {
//            while (rsMD.next()) {
//                String tblName = rsMD.getString("TABLE_NAME");
//                TablesNames.add(tblName);
//
//            }
//        }
//        String tit = "";
//        String bok = "";
//        for (String _tabelName:
//                TablesNames) {
//            if (_tabelName.startsWith("b")) {
//                bok=_tabelName;
//            }
//            if (_tabelName.startsWith("t")) {
//                tit=_tabelName;
//            }
//        }
//        String bookId =  MyServices.getMaxBookIdFromSqlite();
//        ResultSet rs = s.executeQuery("SELECT * FROM [Main]");
//
//        rs.next();
//
//
//        Book book = new Book(rs,bookId);
//
//        rs.close();
//        ResultSet rsPages = s.executeQuery("SELECT * FROM ["+bok+"]");
//        ArrayList<Page> pageList = new ArrayList<>();
//        while (rsPages.next()) {
//            Page page = new Page(rsPages,bookId);
//            pageList.add(page);
//        }
//        ResultSet rsTitles = s.executeQuery("SELECT * FROM ["+tit+"]");
//        ArrayList<Title> titleList = new ArrayList<>();
//
//        while (rsTitles.next()) {
//            Title title = new Title(rsTitles,bookId);
//            titleList.add(title);
//
//        }
//        Class.forName("org.sqlite.JDBC");
//        Connection connLite = DriverManager.getConnection(MyServices.sql_Connetion_Url+"");
//        Statement stmt = connLite.createStatement();
//        MyServices.insertBook(bookId, book,stmt);
//        MyServices.insertPage(bookId, pageList, stmt);
//        MyServices.insertTitle(bookId, titleList);
//        conn.close();
//        stmt.close();
//        Path path = Paths.get(filename);
//        boolean result = Files.deleteIfExists(path);
//        System.out.println(book.name);
//        return "ok";
//    }catch (Exception e){
//        return e.getMessage();
//    }
//}

}
//class BookFromAccess {
//
//    public BookFromAccess(ResultSet resultSet,String _bookId) throws Exception {
//        bookId = _bookId;
//        name = resultSet.getNString("Bk");
//        catId = "41";
//        betaka = resultSet.getNString("Betaka");
//
//    }
//
//    public String bookId;
//    public String name;
//    public String catId;
//    public String betaka;
//
//}

//class PageFromAccess {
//
//
//    public PageFromAccess(ResultSet resultSet,String _bookId) throws Exception {
//
//        bookId = _bookId;
//        id = resultSet.getString("id");
//        nass =resultSet.getNString("nass");
//        part = resultSet.getString("part");
//        page = resultSet.getString("page");
//        try {
//            sora = resultSet.getString("sora");
//            aya = resultSet.getString("aya");
//        }catch (Exception e){
//
//        }
//
//    }
//
//
//    public String bookId;
//    public String id;
//    public String nass;
//    public String part;
//    public String page;
//    public String sora;
//    public String aya;
//}

//class TitleFromAccess {
//
//
//    public TitleFromAccess(ResultSet resultSet,String _bookId) throws Exception{
//
//        bookId = _bookId;
//        title = resultSet.getNString("tit");
//        id = resultSet.getInt("id");
//        lvl = resultSet.getString("lvl");
//        sub = resultSet.getString("Sub");
//
//    }
//
//
//    public String title;
//    public String bookId;
//    public int id;
//    public String lvl;
//    public String sub;
//
//
//}
