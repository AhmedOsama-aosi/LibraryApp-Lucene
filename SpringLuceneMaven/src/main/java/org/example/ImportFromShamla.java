package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Service
public class ImportFromShamla {
    public String database_location = "";
    public List<Book> booksList = new ArrayList<>();
    public List<String> foundedbooklist = new ArrayList<>();
    public List<String> unfoundedbooklist = new ArrayList<>();
    public  String get_book_foldar(int _book_id) {
        String foldar = "";
        if (_book_id < 10 && _book_id > 0) {
            foldar = "00" + _book_id;
        } else if (_book_id > 9 && _book_id < 100) {
            foldar = "0" + _book_id;
        } else if (_book_id > 99) {

            foldar = "" + _book_id;
            //  foldar = foldar.substring(foldar.length() - 3, foldar.length());
            foldar = foldar.substring(foldar.length() - 3);
        }

        return foldar;


    }

    public  List<Title> get_titles(int book_id) {
        List<Title> titleList = new ArrayList<>();
        List<Title> sqltitleList = new ArrayList<>();
        try {

//            Directory dir = FSDirectory.open(Paths.get("" + database_location + "\\store\\title"));
//            DirectoryReader reader = DirectoryReader.open(dir);

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get("" + database_location + "\\store\\title"))));

            TopDocs topDocs = searcher.search(new TermQuery(new Term("book_key",""+book_id)), Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document doc = searcher.storedFields().document(docId);
                Title title = new Title();
                title.bookId = book_id+"";
                var _id = doc.get("id").split("-");
                title.lvl = _id[1];
                title.title = doc.get("body");

                titleList.add(title);

            }

//            reader.close();
//            dir.close();
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
        }
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + database_location + "\\book\\" + get_book_foldar(book_id) + "\\" + book_id + ".db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * From title");

            while (rs.next()) {
                Title title = new Title();
                title.id = rs.getInt("page");
                title.lvl = rs.getString("id");
                title.sub = rs.getString("parent");

                sqltitleList.add(title);

            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);

        }
        try {
            if (titleList.size() == sqltitleList.size()) {
                titleList.sort(Comparator.comparing(s -> s.lvl));
                sqltitleList.sort(Comparator.comparing(s -> s.lvl));
                for (int i = 0; i < titleList.size(); i++) {
                    titleList.get(i).id = sqltitleList.get(i).id;
                    titleList.get(i).sub = sqltitleList.get(i).sub;
                }
                sqltitleList.clear();
            } else {
                System.out.println("titles lists not match : " + book_id);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
        }

        return titleList;

    }

    public  List<Page> get_pages(int book_id) {
        List<Page> pageList = new ArrayList<>();
        List<Page> sqlpageList = new ArrayList<>();
        try {


//            Directory dir = FSDirectory.open(Paths.get("" + database_location + "\\store\\page"));
//            DirectoryReader reader = DirectoryReader.open(dir);
//            IndexSearcher searcher = new IndexSearcher(reader);
//            Analyzer analyzer = new StandardAnalyzer();
//            QueryParser parser = new QueryParser("book_key", analyzer);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get("" + database_location + "\\store\\page"))));

            TopDocs topDocs = searcher.search(new TermQuery(new Term("book_key",""+book_id)), Integer.MAX_VALUE);
//            Query query = parser.parse(String.valueOf(book_id));
//
//            TopDocs topDocs = searcher.search(query, reader.maxDoc());
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document doc = searcher.storedFields().document(docId);
                Page page = new Page();
                page.bookId = book_id+"";
                var _id = doc.get("id").split("-");
                page.id = _id[1];
                if (doc.get("body") != null) {
                    page.nass = doc.get("body");
                    if (doc.get("foot") != null) {
                        page.nass += "\r_________\r" + doc.get("foot");

                    }
                } else {
                    if (doc.get("foot") != null) {
                        page.nass = doc.get("foot");
                    }
                }


                pageList.add(page);

            }
//            reader.close();
//            dir.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
        }
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + database_location + "\\book\\" + get_book_foldar(book_id) + "\\" + book_id + ".db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * From page");

            while (rs.next()) {
                Page page = new Page();
                page.id = rs.getString("id");
                page.page = rs.getString("page")==null?"1":rs.getString("page");
                page.part = rs.getString("part")==null?"1":rs.getString("part");

                sqlpageList.add(page);

            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            if (pageList.size() == sqlpageList.size()) {
                pageList.sort(Comparator.comparing(s -> s.id));
                sqlpageList.sort(Comparator.comparing(s -> s.id));
                for (int i = 0; i < pageList.size(); i++) {
                    pageList.get(i).page = sqlpageList.get(i).page;
                    pageList.get(i).part = sqlpageList.get(i).part;
                }
                sqlpageList.clear();
            } else {
                System.out.println("pages lists not match : " + book_id);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
        }

        return pageList;
    }
    public  void get_selected_books(ArrayList<String>BookNames) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + database_location + "\\master.db");
            Statement stmt = conn.createStatement();
            String sqlcommand = "";
            foundedbooklist=new ArrayList<>();
            unfoundedbooklist=new ArrayList<>();
            booksList = new ArrayList<>();
            if (BookNames.size() > 0) {
                for (int i = 0; i < BookNames.size(); i++) {
                    sqlcommand = "'" + BookNames.get(i) + "'";
                    ResultSet rs = stmt.executeQuery("select * From book where book_name = " + sqlcommand + ";");
                    if (rs.next()) {
                        foundedbooklist.add(BookNames.get(i));
                    } else {
                        unfoundedbooklist.add(BookNames.get(i));
                    }
                }

            }

            if (foundedbooklist.size() > 0) {
                sqlcommand = "'" + foundedbooklist.get(0) + "'";
            }
            if (foundedbooklist.size() > 1) {
                for (int i = 1; i < foundedbooklist.size(); i++) {
                    sqlcommand += "," + "'" + foundedbooklist.get(i) + "'";
                }
            }
            if (foundedbooklist.size() > 0) {
                ResultSet rs = stmt.executeQuery("select * From book where book_name in (" + sqlcommand + ")");

                while (rs.next()) {
                    Book book = new Book();
                    book.bookId = rs.getString("book_id");
                    book.name = rs.getString("book_name");
                    book.catId = 40+"";
                    booksList.add(book);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
        }
        try {


            //Directory dir = FSDirectory.open(Paths.get("" + database_location + "\\store\\book"));

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get("" + database_location + "\\store\\book"))));



            for (Book _book :
                    booksList) {

                TopDocs topDocs = searcher.search(new TermQuery(new Term("id",""+_book.bookId)), Integer.MAX_VALUE);
                ScoreDoc[] hits = topDocs.scoreDocs;
                for (int i = 0; i < hits.length; i++) {
                    int docId = hits[i].doc;
                    Document doc = searcher.storedFields().document(docId);
                    _book.betaka = doc.get("body_store");

                }
            }



        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
        }

    }

    public String getBooksFromShamela(ArrayList<String>BookNames){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            frame.setVisible(true);

            // Show the dialog and get the selected file name
            //frame.setVisible(false);

            //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

            JFileChooser fileChooser = new JFileChooser(".");

            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setDialogTitle("Select Shamela Directory");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(frame);
            frame.dispose();
            if (option == JFileChooser.APPROVE_OPTION) {
                var directory = fileChooser.getSelectedFile();

                database_location = directory.toString()+"\\database";


                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection(MyServices.sql_Connetion_Url+"");
                Statement stmt = conn.createStatement();


                get_selected_books(BookNames);
                for (int b = 0; b < booksList.size(); b++) {

                    String _bookId =  MyServices.getMaxBookIdFromSqlite();

                    int currnetBookId =Integer.parseInt(booksList.get(b).bookId);
                    List<Title> titles= get_titles(currnetBookId);
                    List<Page> pages= get_pages(currnetBookId);



                    MyServices.insertBook(_bookId, booksList.get(b),stmt);

                    MyServices.insertTitle(_bookId, titles);

                    MyServices.insertPage( _bookId, pages, stmt);

                }

                conn.close();
                stmt.close();
                String message_content = "";
                if (unfoundedbooklist.size() > 0) {
                    String nonfoundbooks = "";
                    for (String _book :
                            unfoundedbooklist) {
                        nonfoundbooks +="- "+ _book + "<br/>";
                    }

                    message_content = "تم اضافة : " + booksList.size() + " <br/> <span style=\" color:red \"> لم يتم ايجاد : </span> <br/>" + nonfoundbooks;

                } else {
                    message_content = "عدد الكتب التي تم اضافتها : "+ booksList.size();
                }

                String messsage = "<html><body><div style=\"text-align:right;width:300px;font-size:10px;\" >" + message_content + "</div></body></html>";
                JLabel messagelabel= new JLabel(messsage);
               // JOptionPane.showMessageDialog(null, messagelabel, "Done", JOptionPane.PLAIN_MESSAGE);
                return "Success "+ message_content;
            }
            return "Success "+"operation Canceld";
        } catch (Exception e) {
            // JOptionPane.showMessageDialog(null, e.getMessage(), "خطأ", JOptionPane.PLAIN_MESSAGE);
            return e.getMessage();
        }
    }

}
