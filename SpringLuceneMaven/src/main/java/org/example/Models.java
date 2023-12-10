package org.example;

import org.apache.lucene.document.Document;

import java.sql.ResultSet;
import java.util.ArrayList;

class Category {

    public Category() {
    }

    public Category(Document docx) {
        catId = docx.get("catId");
        name = docx.get("name");

    }

    public String catId;
    public String name;

}

class Book {
    public Book() {
    }
    public Book(ResultSet resultSet, String _bookId) throws Exception {
        bookId = _bookId;
        name = resultSet.getNString("Bk");
        catId = "41";
        betaka = resultSet.getNString("Betaka")==null?" ":resultSet.getNString("Betaka");

    }
    public Book(Document docx) {
        bookId = docx.get("bookId");
        name = docx.get("name");
        catId = docx.get("catId");
        betaka = docx.get("betaka");

    }

    public String bookId;
    public String name;
    public String catId;
    public String betaka;

}

class Page {

    public Page() {

    }
    public Page(ResultSet resultSet,String _bookId) throws Exception {

        bookId = _bookId;
        id = resultSet.getString("id");
        nass =resultSet.getNString("nass");
        part = resultSet.getString("part")==null?"1":resultSet.getString("part");
        page = resultSet.getString("page")==null?"1":resultSet.getString("page");
        try {

        }catch (Exception e){
            sora = resultSet.getString("sora")==null?"0":resultSet.getString("sora");
            aya = resultSet.getString("aya")==null?"0":resultSet.getString("aya");
        }


    }
    public Page(String _bookId, String _id, String _nass, String _part, String _page, String _sora, String _aya) {
        bookId = _bookId;
        id = _id;
        nass = _nass;
        part = _part;
        page = _page;
        sora = _sora;
        aya = _aya;
    }

    public Page(String _docId,Document docx) {
        docId = _docId;
        bookId = docx.get("bookId");
        id = docx.get("id");
        nass = docx.get("nass");
        part = docx.get("part");
        page = docx.get("page");
        sora = docx.get("sora");
        aya = docx.get("aya");
    }

    public String docId;
    public String bookId;
    public String id;
    public String nass;
    public String part;
    public String page;
    public String sora;
    public String aya;
}

class Title {
    public Title() {
    }
    public Title(ResultSet resultSet,String _bookId) throws Exception{
        bookId = _bookId;
        title = resultSet.getNString("tit");
        id = resultSet.getInt("id");
        lvl = resultSet.getString("lvl")==null?"0":resultSet.getString("lvl");
        sub =  resultSet.getString("Sub")==null?"0":resultSet.getString("Sub");


    }
    public Title(int _docId,Document docx) {
        docId = _docId;
        bookId = docx.get("bookId");
        title = docx.get("title");
        id = Integer.parseInt(docx.get("id"));
        lvl = docx.get("lvl");
        sub = docx.get("sub");

    }

    public int docId;
    public String title;
    public String bookId;
    public int id;
    public String lvl;
    public String sub;


}

class BookPage {

    public BookPage(String _nass, String _docid, String _id, String _part, String _page, String _aya, String _sora, String _bookId, String _bestFregmant) {

        nass = _nass;
        docid = _docid;
        id = _id;
        part = _part;
        page = _page;
        aya = _aya;
        sora = _sora;
        bookId = _bookId;
        bestFregmant = _bestFregmant;
    }

    public String nass;
    public String id;
    public String docid;
    public String part;
    public String page;
    public String aya;
    public String sora;
    public String bookId;
    public String bestFregmant;
}

class PagesResult {

    public int docum;
    public int pagenum;
    public int pageid;
}

class BookPartContent {
    public ArrayList<Title> titlesList = new ArrayList<>();
    public ArrayList<PagesResult> pagesList = new ArrayList<>();
}
class QuranContent {
    public ArrayList<Title> titlesList = new ArrayList<>();
    public ArrayList<Page> pagesList = new ArrayList<>();
}
class BookMark {
    public String docId;
    public String bookId;
    public String pageId;
    public String categoryName;
    public String bookName;
    public String part;
    public String pageNum;
}
