package org.example;

import com.github.msarhan.lucene.ArabicRootExtractorAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.poi.hslf.record.Environment;
import org.springframework.stereotype.Service;
import ws.shamela.CustomField;
import ws.shamela.MorphologyAnalyzer;
//import ws.shamela.CustomField;
//import ws.shamela.MorphologyAnalyzer;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;

class Search_Result {

    public String results_count;
    public ArrayList<BookPage> result_BookPage;

}

@Service
public class MyServices {

    static  String current_app_location="E:\\الصوفية";
  //  static String current_app_location=Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();
    static  String lucene_database_path = current_app_location+"\\database\\store";
    static  String path_Category= lucene_database_path+"\\categories";
    static String path_Book=lucene_database_path+"\\books";
    static  String path_Page=lucene_database_path+"\\pages";
    static  String path_Title=lucene_database_path+"\\titles";
    
   public static String sql_Connetion_Url ="jdbc:sqlite:"+ current_app_location+"\\database\\Library.db";
    //static final String databaseName = "D:\\data\\LibraryProject\\database\\libraryDB.mdf";
  //  static final String url="jdbc:sqlserver://localhost:1433;databaseName=" + databaseName + ";encrypt=false;user=sa;password=1234";
    public ArrayList<Category> getCategories() {
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Category));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new MatchAllDocsQuery();
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE, Sort.RELEVANCE);
            ScoreDoc[] hits = topDocs.scoreDocs;

            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document docx = searcher.storedFields().document(docId);
                Category category = new Category();
                category.catId = docx.get("catId");
                category.name = docx.get("name");
                categoryArrayList.add(category);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return categoryArrayList;
    }

    public Category getCategoryById(int catId) {
        Category theCategory = new Category();

        try {
            Directory dir = FSDirectory.open(Paths.get(path_Category));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new QueryParser("catId", new StandardAnalyzer()).parse("" + catId);
            TopDocs topDocs = searcher.search(query, 1);
            ScoreDoc[] hits = topDocs.scoreDocs;
            Document docx = searcher.storedFields().document(topDocs.scoreDocs[0].doc);
            Category category = new Category(docx);
            theCategory = category;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return theCategory;
    }
    public ArrayList<Book> getCategoryBooks(int CategoryId) {
        ArrayList<Book> bookArrayList = new ArrayList<>();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Book));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("catId", "" + CategoryId));

            TopDocs topDocs = searcher.search(query, 1000000, Sort.INDEXORDER);
            ScoreDoc[] hits = topDocs.scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Book book = new Book();
                book.catId = docx.get("catId");
                book.bookId = docx.get("bookId");
                book.name = docx.get("name");
                book.betaka = docx.get("betaka");
                bookArrayList.add(book);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bookArrayList;
    }

    public ArrayList<Book> getSearchBooks(String Search_Words) {


        ArrayList<Book> bookArrayList = new ArrayList<>();
        try {

            Directory dir = FSDirectory.open(Paths.get(path_Book));

            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            // searcher.storedFields();
            String query_text = "\"" + Search_Words + "\"";

            Query query = new QueryParser("name", new StandardAnalyzer()).parse(query_text);
            // Query query = new TermQuery(new Term("nass",query_text));

            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE, Sort.RELEVANCE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Book book = new Book();
                book.catId = docx.get("catId");
                book.bookId = docx.get("bookId");
                book.name = docx.get("name");
                book.betaka = docx.get("betaka");
                bookArrayList.add(book);
            }


            reader.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return bookArrayList;

    }

    public ArrayList<Page> getPagesByBookId(int BookId)  {
        ArrayList<Page> pagesArrayList = new ArrayList<>();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);


            //Query query = parser.parse("bookId:"+BookId+" AND part:1");
            Query query = new TermQuery(new Term("bookId", "" + BookId));
            TopDocs topDocs = searcher.search(query, 10000000);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Page page = new Page(docId+"",docx);

                pagesArrayList.add(page);
            }
            pagesArrayList.sort((x,y)->Integer.parseInt(x.id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pagesArrayList;
    }
    ///*************************************************************
    public Stream<String> getSearchDocs(String Search_Words, int skip) throws Exception {

            Directory dir = FSDirectory.open(Paths.get(path_Page));


            Map<String, Analyzer> analyzerMap = new HashMap<>();
            analyzerMap.put("m_nass", new StandardAnalyzer());
            //analyzerMap.put("m_nass", new MorphologyAnalyzer());
            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            String query_text = "\"" + Search_Words + "\"";

            Query query = new QueryParser("m_nass", wrapper).parse(query_text);
        //    TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE, Sort.RELEVANCE);
        //    TopDocs topDocs = searcher.search(query, 10);
        //    System.out.println(topDocs.totalHits);
        //    topDocs= searcher.searchAfter(topDocs.scoreDocs[topDocs.scoreDocs.length-1],query,10);
        //    var hits = Arrays.stream(topDocs.scoreDocs).map(p-> p.doc+"");

        TopScoreDocCollector collector = TopScoreDocCollector.create (10+skip,Integer.MAX_VALUE);
        searcher.search (query, collector);


    // get the total number of hits and print it
        ScoreDoc [] hitss = collector.topDocs(skip,10).scoreDocs;
        int numTotalHits = collector.getTotalHits();
        System.out.println (numTotalHits + " total matching documents");
        var hits = Arrays.stream(hitss).map(p-> p.doc+"");





        return hits;

    }
    public Stream<String> getSearchDocsInselectedBooks(String Search_Words,ArrayList<String>books) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(path_Page));


        Map<String, Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put("m_nass", new StandardAnalyzer());
        //analyzerMap.put("m_nass", new MorphologyAnalyzer());
        PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        String query_text = "\"" + Search_Words + "\"";

        ArrayList<BytesRef> terms = new ArrayList<>();
        for (String bookId : books) {
            terms.add(new BytesRef(bookId));

        }

        TermInSetQuery termsInSetQuery = new TermInSetQuery("bookId", terms);
        Query query = new QueryParser("m_nass", wrapper).parse(query_text);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(query, BooleanClause.Occur.MUST);
        booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST);


        BooleanQuery bq = booleanQuery.build();


      //  TopDocs topDocs = searcher.search(bq, Integer.MAX_VALUE, Sort.RELEVANCE);
        TopDocs topDocs = searcher.search(bq, 2000000000, Sort.RELEVANCE);

        var hits = Arrays.stream(topDocs.scoreDocs).map(p-> p.doc+"");





        return hits;

    }
    public Search_Result getSearchedPages(String Search_Words, ArrayList<String> docs) {
        Search_Result searchResult = new Search_Result();
        searchResult.result_BookPage = new ArrayList<>();
       // ArrayList<BookPage> results = new ArrayList<>();
        try {



            Map<String, Analyzer> analyzerMap = new HashMap<>();
            analyzerMap.put("m_nass", new StandardAnalyzer());
            //analyzerMap.put("m_nass", new MorphologyAnalyzer());
            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);



            String query_text = "\"" + Search_Words + "\"";

            Query query = new QueryParser("m_nass", wrapper).parse(query_text);

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"font-weight:bold;color:red;\">", "</span>");
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);



            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Page))));

            IndexSearcher searcher2 = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Book))));

            IndexSearcher searcher3 = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Category))));


            for (int i = 0; i < docs.size(); i++) {
                int docId = Integer.parseInt(docs.get(i));

                Document docx = searcher.storedFields().document(docId);

                TopDocs topDocs2 = searcher2.search(new TermQuery(new Term("bookId", docx.get("bookId"))), 1);
                Book book = new Book(searcher2.storedFields().document(topDocs2.scoreDocs[0].doc));
                TopDocs topDocs3 = searcher3.search(new TermQuery(new Term("catId", book.catId)), 1);
                Category category = new Category(searcher3.storedFields().document(topDocs3.scoreDocs[0].doc));
                String nass = docx.get("nass");
                String removentachkil = docx.get("nass").replaceAll("([^\\u0621-\\u064A\\s])", "");
                String BestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "m_nass", removentachkil);

                BookPage bookPage = new BookPage("" + nass, "" + docId, "" + category.name, "" + docx.get("part"), "" + docx.get("page"), "" + docx.get("aya"), "" + docx.get("sora"), "" + book.name, "" + BestFragment);
              //  results.add(bookPage);
                searchResult.result_BookPage.add(bookPage);
            }
        //    searchResult.result_BookPage = results;


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return searchResult;

    }
    ///*************************************************************
    public Search_Result getSearchPages(String Search_Words, int skip) {
        Search_Result searchResult = new Search_Result();
        ArrayList<BookPage> results = new ArrayList<>();
        try {


            Map<String, Analyzer> analyzerMap = new HashMap<>();
            analyzerMap.put("m_nass", new StandardAnalyzer());
            //analyzerMap.put("m_nass", new MorphologyAnalyzer());
            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);

            String query_text = "\"" + Search_Words + "\"";

            Query query = new QueryParser("m_nass", wrapper).parse(query_text);

            TopScoreDocCollector collector = TopScoreDocCollector.create (10+skip,Integer.MAX_VALUE);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Page))));
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(query, BooleanClause.Occur.MUST);
            BooleanQuery bq = booleanQuery.build();
            searcher.search (bq, collector);



            ScoreDoc [] hits = collector.topDocs(skip,10).scoreDocs;
            int numTotalHits = collector.getTotalHits();

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"font-weight:bold;color:red;\">", "</span>");
            QueryScorer scorer = new QueryScorer(bq);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            searchResult.results_count = numTotalHits + "";



            IndexSearcher searcher2 = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Book))));

            IndexSearcher searcher3 = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Category))));
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;

                Document docx = searcher.storedFields().document(docId);

                TopDocs topDocs2 = searcher2.search(new TermQuery(new Term("bookId", docx.get("bookId"))), 1);
                Book book = new Book(searcher2.storedFields().document(topDocs2.scoreDocs[0].doc));
                TopDocs topDocs3 = searcher3.search(new TermQuery(new Term("catId", book.catId)), 1);
                Category category = new Category(searcher3.storedFields().document(topDocs3.scoreDocs[0].doc));
                String nass = docx.get("nass");
                String removentachkil = docx.get("nass").replaceAll("([^\\u0621-\\u064A\\s])", "");
                String BestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "m_nass", removentachkil);

                BookPage bookPage = new BookPage("" + nass, "" + docId, "" + category.name, "" + docx.get("part"), "" + docx.get("page"), "" + docx.get("aya"), "" + docx.get("sora"), "" + book.name, "" + BestFragment);
                results.add(bookPage);

            }
            searchResult.result_BookPage = results;
//            reader.close();
//            reader2.close();
//            dir2.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return searchResult;

    }
    public Search_Result getSearchPagesInselectedBooks(String Search_Words, int skip,ArrayList<String>books) {
        Search_Result searchResult = new Search_Result();
        ArrayList<BookPage> results = new ArrayList<>();
        try {

          //  Directory dir = FSDirectory.open(Paths.get(path_Page));
            //  DirectoryReader reader = DirectoryReader.open(dir);
            //    IndexSearcher searcher = new IndexSearcher(reader);

            Map<String, Analyzer> analyzerMap = new HashMap<>();
            analyzerMap.put("m_nass", new StandardAnalyzer());
            //analyzerMap.put("m_nass", new MorphologyAnalyzer());

            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);

            String query_text = "\"" + Search_Words + "\"";

            ArrayList<BytesRef> terms = new ArrayList<>();
            for (String bookId : books) {
                terms.add(new BytesRef(bookId));

            }

            TermInSetQuery termsInSetQuery = new TermInSetQuery("bookId", terms);
            Query query = new QueryParser("m_nass", wrapper).parse(query_text);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(query, BooleanClause.Occur.MUST);
            booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST);


            BooleanQuery bq = booleanQuery.build();


            TopScoreDocCollector collector = TopScoreDocCollector.create (10+skip,Integer.MAX_VALUE);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Page))));
            searcher.search (bq, collector);


//            TopDocs topDocs = searcher.search(bq, Integer.MAX_VALUE, Sort.RELEVANCE);
//            ScoreDoc[] hits = topDocs.scoreDocs;
            ScoreDoc [] hits = collector.topDocs(skip,10).scoreDocs;
            int numTotalHits = collector.getTotalHits();

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"font-weight:bold;color:red;\">", "</span>");
            QueryScorer scorer = new QueryScorer(bq);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            searchResult.results_count = numTotalHits + "";


//
//            Directory dir2 = FSDirectory.open(Paths.get(path_Book));
//            DirectoryReader reader2 = DirectoryReader.open(dir2);
//            IndexSearcher searcher2 = new IndexSearcher(reader2);
//            Directory dir3 = FSDirectory.open(Paths.get(path_Category));
//            DirectoryReader reader3 = DirectoryReader.open(dir3);
//            IndexSearcher searcher3 = new IndexSearcher(reader3);

            IndexSearcher searcher2 = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Book))));

            IndexSearcher searcher3 = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Category))));

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;

                Document docx = searcher.storedFields().document(docId);

                TopDocs topDocs2 = searcher2.search(new TermQuery(new Term("bookId", docx.get("bookId"))), 1);
                Book book = new Book(searcher2.storedFields().document(topDocs2.scoreDocs[0].doc));
                TopDocs topDocs3 = searcher3.search(new TermQuery(new Term("catId", book.catId)), 1);
                Category category = new Category(searcher3.storedFields().document(topDocs3.scoreDocs[0].doc));
                String nass = docx.get("nass");
                String removentachkil = docx.get("nass").replaceAll("([^\\u0621-\\u064A\\s])", "");
                String BestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "m_nass", removentachkil);

                BookPage bookPage = new BookPage("" + nass, "" + docId, "" + category.name, "" + docx.get("part"), "" + docx.get("page"), "" + docx.get("aya"), "" + docx.get("sora"), "" + book.name, "" + BestFragment);
                results.add(bookPage);

            }
            searchResult.result_BookPage = results;
//            reader.close();
//            reader2.close();
//            dir2.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return searchResult;

    }
    public Search_Result getSearchInBookPages(String Search_Words, int BookId, int skip) {
        Search_Result searchResult = new Search_Result();
        ArrayList<BookPage> results = new ArrayList<>();
        try {

            Directory dir = FSDirectory.open(Paths.get(path_Page));


            Map<String, Analyzer> analyzerMap = new HashMap<>();
            analyzerMap.put("m_nass", new StandardAnalyzer());
            //analyzerMap.put("m_nass", new MorphologyAnalyzer());
            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            String query_text = "\"" + Search_Words + "\"";

            Query query = new QueryParser("m_nass", wrapper).parse(query_text + " AND bookId: " + BookId);
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE, Sort.RELEVANCE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"font-weight:bold;color:red;\">", "</span>");
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            searchResult.results_count = hits.length + "";

            int limit = skip + 10;
            if (limit > hits.length) {
                limit = hits.length;
            }


            for (int i = skip; i < limit; i++) {
                int docId = hits[i].doc;

                Document docx = searcher.storedFields().document(docId);


                String nass = docx.get("nass");
                String removentachkil = docx.get("nass").replaceAll("([^\\u0621-\\u064A\\s])", "");
                String BestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "m_nass", removentachkil);

                BookPage bookPage = new BookPage("" + nass, "" + docId, "" + docx.get("id"), "" + docx.get("part"), "" + docx.get("page"), "" + docx.get("aya"), "" + docx.get("sora"), "" + docx.get("bookId"), "" + BestFragment);
                results.add(bookPage);

            }
            searchResult.result_BookPage = results;
            reader.close();


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return searchResult;
    }

    public Book getBookById(int BookId) {
        Book theBook = new Book();
        try {
            Directory dir2 = FSDirectory.open(Paths.get(path_Book));
            DirectoryReader reader2 = DirectoryReader.open(dir2);
            IndexSearcher searcher2 = new IndexSearcher(reader2);
            TopDocs topDocs = searcher2.search(new TermQuery(new Term("bookId", "" + BookId)), 1);
            Book book = new Book(searcher2.storedFields().document(topDocs.scoreDocs[0].doc));
            theBook = book;
            reader2.close();
            dir2.close();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return theBook;
    }

    public Page getPageById(int PageId) {
        Page thePage = new Page();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Document docx = searcher.storedFields().document(PageId);
            Page page = new Page(PageId+"",docx);
            thePage = page;
            reader.close();
            dir.close();

//            Directory dir2 = FSDirectory.open(Paths.get(lucene_database_path+"\\books"));
//            DirectoryReader reader2 = DirectoryReader.open(dir2);
//            IndexSearcher searcher2 = new IndexSearcher(reader2);
//            TopDocs topDocs = searcher2.search(new TermQuery(new Term("bookId",page.bookId)),1);
//            Book book = new Book(searcher2.storedFields().document(topDocs.scoreDocs[0].doc));
//            reader2.close();
//            dir2.close();
//
//            Directory dir3 = FSDirectory.open(Paths.get(lucene_database_path+"\\categories"));
//            DirectoryReader reader3 = DirectoryReader.open(dir3);
//            IndexSearcher searcher3 = new IndexSearcher(reader3);
//            TopDocs topDocs2 = searcher3.search(new TermQuery(new Term("catId",book.catId)),1);
//            Category category = new Category(searcher3.storedFields().document(topDocs2.scoreDocs[0].doc));
//            reader3.close();
//            dir3.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return thePage;
    }


    public Page getPageById(int BookId, int PageId) {
        Page thePage = new Page();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(new QueryParser("bookId", new StandardAnalyzer()).parse(BookId + " AND id:" + PageId), 1);
            Document docx = searcher.storedFields().document(topDocs.scoreDocs[0].doc);
            Page page = new Page(topDocs.scoreDocs[0].doc+"",docx);
            thePage = page;
            reader.close();
            dir.close();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return thePage;
    }

    public ArrayList<PagesResult> getPartPagesById(int BookId, int Part) {
        ArrayList<PagesResult> pagesArrayList = new ArrayList<>();

        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId + " AND part:" + Part);

            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                PagesResult pagesResult = new PagesResult();
                pagesResult.docum = docId;
                pagesResult.pagenum = Integer.parseInt(docx.get("page"));
                pagesResult.pageid = Integer.parseInt(docx.get("id"));
                pagesArrayList.add(pagesResult);
                //  Page page = new Page(docx);
                // pagesArrayList.add(page);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pagesArrayList;
    }

    public ArrayList<Integer> getBookParts(int BookId) {
        ArrayList<Integer> parts = new ArrayList();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT DISTINCT part FROM Page WHERE bookId =" + BookId + " order by part asc ");
            while (rs.next()) {
                parts.add(rs.getInt("part"));
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return parts;
    }

    public ArrayList<Title> getPartTitlesById(int BookId, int Part) {
        ArrayList<Title> pagesArrayList = new ArrayList<>();

        try {
            Directory dir = FSDirectory.open(Paths.get(path_Title));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId);

            TopDocs topDocs = searcher.search(query ,Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;


            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Title title = new Title();
                title.bookId = docx.get("bookId");
                title.title = docx.get("title");
                title.id = Integer.parseInt(docx.get("id"));
                title.lvl = docx.get("lvl");
                title.sub = docx.get("sub");


                pagesArrayList.add(title);
                //  Page page = new Page(docx);
                // pagesArrayList.add(page);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pagesArrayList;
    }

    public BookPartContent getPartPagesTitlesId(int BookId, int Part) {
        BookPartContent partContent = new BookPartContent();
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(path_Page))));
           // IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get("D:\\Projects\\test lucene\\database\\store\\pages"))));

           // Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId + " AND part:\"" + Part + "\"");

            BooleanQuery.Builder booleanPagesQueryBuilder = new BooleanQuery.Builder();
            booleanPagesQueryBuilder.add(new TermQuery(new Term("bookId",BookId+"")), BooleanClause.Occur.MUST);
            booleanPagesQueryBuilder.add(new TermQuery(new Term("part","" + Part + "")), BooleanClause.Occur.MUST);
            BooleanQuery booleanPagesquary = booleanPagesQueryBuilder.build();

            TopDocs topDocs = searcher.search(booleanPagesquary, 100000);
           // TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                PagesResult pagesResult = new PagesResult();
                pagesResult.docum = docId;
                pagesResult.pagenum = Integer.parseInt(docx.get("page"));
                pagesResult.pageid = Integer.parseInt(docx.get("id"));
                partContent.pagesList.add(pagesResult);
            }
            hits = new ScoreDoc[1];


            Directory dir2 = FSDirectory.open(Paths.get(path_Title));
            DirectoryReader reader2 = DirectoryReader.open(dir2);
            IndexSearcher searcher2 = new IndexSearcher(reader2);


            ArrayList<BytesRef> terms = new ArrayList<>();
            for (PagesResult page : partContent.pagesList) {
                terms.add(new BytesRef(""+page.pageid+""));
            }

            TermInSetQuery termsInSetQuery = new TermInSetQuery("id", terms);

            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(new TermQuery(new Term("bookId",BookId+"")), BooleanClause.Occur.MUST);
            booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST);
            BooleanQuery bq = booleanQuery.build();

            TopDocs topDocs2 = searcher2.search(bq, 100000);
            ScoreDoc[] hits2 = topDocs2.scoreDocs;

            for (int i = 0; i < hits2.length; i++) {

                int docId = hits2[i].doc;
                Document docx = searcher2.storedFields().document(docId);
                Title title = new Title(docId,docx);
                partContent.titlesList.add(title);

            }
            hits2 = new ScoreDoc[1];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return partContent;
    }
    public QuranContent get_quran(int BookId) {
        QuranContent quranContent = new QuranContent();

        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId );

            TopDocs topDocs = searcher.search(query, 7000);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Page pagesResult = new Page(docId+"",docx);

                quranContent.pagesList.add(pagesResult);

            }

            Directory dir2 = FSDirectory.open(Paths.get(path_Title));
            DirectoryReader reader2 = DirectoryReader.open(dir2);
            IndexSearcher searcher2 = new IndexSearcher(reader2);


            Query query2 = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId );

            TopDocs topDocs2 = searcher2.search(query2, 200);
            ScoreDoc[] hits2 = topDocs2.scoreDocs;

            for (int i = 0; i < hits2.length; i++) {
                int docId = hits2[i].doc;
                Document docx = searcher2.storedFields().document(docId);
                Title title = new Title(docId,docx);
                quranContent.titlesList.add(title);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return quranContent;
    }
    public ArrayList<Book> getTafseerBooks() {

        ArrayList<Book> bookArrayList = new ArrayList<>();
        ArrayList<Integer> TafsserBooksIds = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT bookId FROM TafseerBook  ");

            while (rs.next()) {
                TafsserBooksIds.add(rs.getInt("bookId"));
            }
            connLite.close();

            Directory dir = FSDirectory.open(Paths.get(path_Book));

            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);


            ArrayList<BytesRef> terms = new ArrayList<>();
            for (int bookId : TafsserBooksIds) {
                terms.add(new BytesRef(String.valueOf(bookId)));
            }

            TermInSetQuery termsInSetQuery = new TermInSetQuery("bookId", terms);

            TopDocs topDocs = searcher.search(termsInSetQuery, Integer.MAX_VALUE, Sort.RELEVANCE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Book book = new Book();
                book.catId = docx.get("catId");
                book.bookId = docx.get("bookId");
                book.name = docx.get("name");
                book.betaka = docx.get("betaka");
                bookArrayList.add(book);
            }


            reader.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return bookArrayList;

    }
    public ArrayList<Page> get_tafseer(int bookId,int sora,int aya){
        ArrayList<Page> pageArrayList = new ArrayList<>();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));

            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse(bookId+"");
            TopDocs topDocs = searcher.search(query,1000000);


            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i=0;i<hits.length;i++){
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Page page = new Page(docId+"",docx);
                pageArrayList.add(page);
            }
            pageArrayList.sort(Comparator.comparingInt(x -> Integer.parseInt(x.id)));
            List<Page> soraAyat =pageArrayList.stream().filter(x->Integer.parseInt(x.sora)==sora &&Integer.parseInt(x.aya)!=0).toList();
            List<Page> AyaPageBefore = soraAyat.stream().filter(x->Integer.parseInt(x.aya)<=aya).toList();
            var min = AyaPageBefore.get((AyaPageBefore.size()-1));
            List<Page> AyaPageAfter = soraAyat.stream().filter(x->Integer.parseInt(x.aya)>aya).toList();
            AyaPageBefore= new ArrayList<>();

            Page max ;
            if (AyaPageAfter.size()>0){
                max = AyaPageAfter.get((0));
                AyaPageAfter=new ArrayList<>();
            }else{
                if (sora != 114){
                    var sorsad =pageArrayList.stream().filter(x->Integer.parseInt(x.sora)==sora+1 &&Integer.parseInt(x.aya)!=0).toList();
                    max = sorsad.get((0));
                }else {
                    max=pageArrayList.get(pageArrayList.size()-1);
                }

                //  max=pageArrayList.get(pageArrayList.size()-1);
            }
            var dfdf=pageArrayList.stream().filter(x->Integer.parseInt(x.id)>=Integer.parseInt(min.id)&&Integer.parseInt(x.id)<Integer.parseInt(max.id)).toList();


            pageArrayList.clear();
            pageArrayList.addAll(dfdf);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pageArrayList;
    }
    public QuranContent get_TafseerBook(int BookId) {
        QuranContent quranContent = new QuranContent();

        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId );

            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Page pagesResult = new Page(docId+"",docx);

                quranContent.pagesList.add(pagesResult);

            }

            Directory dir2 = FSDirectory.open(Paths.get(path_Title));
            DirectoryReader reader2 = DirectoryReader.open(dir2);
            IndexSearcher searcher2 = new IndexSearcher(reader2);


            Query query2 = new QueryParser("bookId", new StandardAnalyzer()).parse("" + BookId );

            TopDocs topDocs2 = searcher2.search(query2, Integer.MAX_VALUE);
            ScoreDoc[] hits2 = topDocs2.scoreDocs;

            for (int i = 0; i < hits2.length; i++) {
                int docId = hits2[i].doc;
                Document docx = searcher2.storedFields().document(docId);
                Title title = new Title(docId,docx);
                quranContent.titlesList.add(title);

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return quranContent;
    }
    //********************Bookmarks**************************
    public String addBookMark(String bookmarkId) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();

            ResultSet rs = stmtLite.executeQuery("SELECT rowId FROM User WHERE bookmark is NULL or bookmark='' ORDER BY rowId ASC LIMIT 1");
            int rowid = 0;
            while (rs.next()) {
                rowid = rs.getInt("rowId");
            }
            if (rowid != 0) {
                stmtLite.executeUpdate("update [User] SET bookmark =  '" + bookmarkId + "'  where rowid =" + rowid);

            } else {
                stmtLite.executeUpdate("INSERT INTO [User] (bookmark) VALUES ('" + bookmarkId + "')");

            }
            connLite.close();
            return "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());

            return "error";
        }

    }
    public String deleteBookMark(String bookmarkId) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            stmtLite.executeUpdate("update [User] SET bookmark = null where bookmark= '" + bookmarkId + "'");
            connLite.close();
            return "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public ArrayList<BookMark> getBookMarks() {
        ArrayList<BookMark> bookMarksList = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT bookmark from [User] Where bookmark is not null");
            ArrayList<String> bookmarks = new ArrayList<>();
            while (rs.next()) {
                bookmarks.add(rs.getString("bookmark"));
            }
            for (String bookmarkId :
                    bookmarks) {
                var theBookmark = bookmarkId.split("-");
                String bookId = theBookmark[0];
                String pageId = theBookmark[1];
                Page page = getPageById(Integer.parseInt(bookId), Integer.parseInt(pageId));
                Book book = getBookById(Integer.parseInt(bookId));
                Category category = getCategoryById(Integer.parseInt(book.catId));
                BookMark bookMark = new BookMark();
                bookMark.docId = page.docId;
                bookMark.bookId = bookId;
                bookMark.pageId = pageId;
                bookMark.bookName = book.name;
                bookMark.categoryName = category.name;
                bookMark.part = page.part;
                bookMark.pageNum = page.page;
                bookMarksList.add(bookMark);

            }
            connLite.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bookMarksList;
    }
    public boolean checkIfBookmarked(String _bookmarkId) {
    boolean bookmarked=false;
    try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT bookmark from [User] Where bookmark ='"+_bookmarkId+"'");

            while (rs.next()){
                bookmarked=true;
            }
        connLite.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bookmarked;
    }
    //*****************END***Bookmarks***********************
    public String addCategory(Category doc) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT max(id) as id FROM Category ");
            int max =1;
            while (rs.next()){
                max = rs.getInt("id")+1;
            }
            IndexWriter writer = newIndexWriter(path_Category);
            Document newDoc = new Document();
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);

            IndexSearcher searcher = new IndexSearcher( DirectoryReader.open( FSDirectory.open(Paths.get(path_Category))));
            var resutls = searcher.search(new MatchAllDocsQuery(),Integer.MAX_VALUE);
            doc.catId = max+"";
            newDoc.add(new Field("catId", doc.catId, ft));
            newDoc.add(new Field("name", doc.name, ft));

            writer.addDocument(newDoc);
            writer.commit();
            writer.close();
            stmtLite.executeUpdate("INSERT INTO Category (id,catord,lvl)values ("+max+","+0+","+0+")");
            stmtLite.close();
            connLite.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String updateCategory(Category doc) {
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Category));


            IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
            indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);                             // create/overwrite index
            indexConfig.setRAMBufferSizeMB(8000);
            // create/overwrite index
            IndexWriter writer = new IndexWriter(dir, indexConfig);
            Document newDoc = new Document();
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);
            newDoc.add(new Field("catId", doc.catId, ft));
            newDoc.add(new Field("name", doc.name, ft));

            writer.updateDocument(new Term("catId",doc.catId),newDoc);
            writer.commit();
            writer.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String addBook(Book doc) {
        try {
            //Get max id from sqlite database
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT max(bookId) as bookId FROM Book ");
            int max =1;
            while (rs.next()){
               max = rs.getInt("bookId")+1;
            }
            //add new book in lucene index
            IndexWriter writer = newIndexWriter(path_Book);
            Document newDoc = new Document();
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);
            newDoc.add(new Field("catId", doc.catId, ft));
            newDoc.add(new Field("name", doc.name, ft));
            newDoc.add(new Field("bookId", max+"", ft));
            newDoc.add(new Field("betaka", doc.betaka, ft));

            writer.addDocument(newDoc);
            writer.commit();
            writer.close();
            //add new book in sqlite database
            stmtLite.executeUpdate("INSERT INTO Book (bookId,catId)values ("+max+","+doc.catId+")");
            stmtLite.close();
            connLite.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String updateBook(Book doc) {
        try {

            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            stmtLite.executeUpdate("update [Book] SET " +
                    "catId = "+doc.catId +" "+
                    "where bookId= " + doc.bookId );

            connLite.close();

            IndexWriter writer = newIndexWriter(path_Book);
            Document newDoc = new Document();
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);
            newDoc.add(new Field("catId", doc.catId, ft));
            newDoc.add(new Field("name", doc.name, ft));
            newDoc.add(new Field("bookId", doc.bookId, ft));
            newDoc.add(new Field("betaka", doc.betaka, ft));

            writer.updateDocument(new Term("bookId",doc.bookId),newDoc);
            writer.commit();
            writer.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String updateListBooks(ArrayList<Book> books) {
        try {

            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            for (Book book:
                 books) {
                stmtLite.executeUpdate("update [Book] SET " +
                        "catId = "+book.catId +" "+
                        "where bookId= " + book.bookId );
            }
            connLite.close();
            // create/overwrite index
            IndexWriter writer = newIndexWriter(path_Book);
            for (Book book:
                    books) {
                Document newDoc = new Document();
                FieldType ft = new FieldType(TextField.TYPE_STORED);
                ft.setOmitNorms(true);
                newDoc.add(new Field("catId", book.catId, ft));
                newDoc.add(new Field("name", book.name, ft));
                newDoc.add(new Field("bookId", book.bookId, ft));
                newDoc.add(new Field("betaka", book.betaka, ft));
                writer.updateDocument(new Term("bookId",book.bookId),newDoc);

            }


            writer.commit();
            writer.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String addPage(Page doc) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();

            ResultSet rs = stmtLite.executeQuery("SELECT max(id) as id FROM Page WHERE bookId="+doc.bookId);
            int max =1;
            while (rs.next()){
                max= rs.getInt("id")+1;
                doc.id = max+"";
            }
            stmtLite.executeUpdate("insert into [Page] (bookId,id,part,page,sora,aya) values " +
                    "("+doc.bookId+","+doc.id+","+doc.part+","+doc.page+"," +doc.sora+","+doc.aya+")");
            connLite.close();

            IndexWriter writer = newIndexWriter(path_Page);
            Document newDoc = new Document();
            FieldType ftd = new FieldType(TextField.TYPE_STORED);
            ftd.setOmitNorms(true);
            ftd.setIndexOptions(IndexOptions.NONE);

            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);

            FieldType ft2 = new FieldType(TextField.TYPE_NOT_STORED);
            ft2.setOmitNorms(true);

            String removentachkil = doc.nass.replaceAll("([^\\u0621-\\u064A\\s])","");
            newDoc.add(new CustomField("nass", doc.nass, ftd));
            newDoc.add(new CustomField("m_nass", removentachkil, ft2));
            newDoc.add(new CustomField("bookId", doc.bookId, ft));
            newDoc.add(new CustomField("id", doc.id, ft));
            newDoc.add(new CustomField("part", doc.part, ft));
            newDoc.add(new CustomField("page", doc.page, ft));
            newDoc.add(new CustomField("aya", doc.aya ==null  ? "0" : doc.aya, ft));
            newDoc.add(new CustomField("sora", doc.sora ==null ? "0" : doc.sora, ft));

            writer.addDocument(newDoc);
            writer.commit();
            writer.close();
            return  "ok";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String addTitle(Title doc) {
        try {
            IndexWriter writer =newIndexWriter(path_Title);
            Document newDoc = new Document();
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);
            newDoc.add(new Field("bookId", doc.bookId,ft));
            newDoc.add(new Field("title", doc.title, ft));
            newDoc.add(new Field("id", doc.id+"", ft));
            newDoc.add(new Field("lvl", doc.lvl==null?"0":doc.lvl, ft));
            newDoc.add(new Field("sub", doc.sub==null?"0":doc.sub, ft));

            writer.addDocument(newDoc);
            writer.commit();
            writer.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String updatePage(Page doc) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            stmtLite.executeUpdate("update [Page] SET " +
                    "page = "+doc.page+"," +
                    "part = "+doc.part+"," +
                    "aya = "+doc.aya+"," +
                    "sora = "+doc.sora+" " +
                    "where bookId= " + doc.bookId + " AND id = "+doc.id);
            connLite.close();


            Directory dir = FSDirectory.open(Paths.get(path_Page));
            IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
            indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);                             // create/overwrite index
            indexConfig.setRAMBufferSizeMB(8000);
            // create/overwrite index
            IndexWriter writer = new IndexWriter(dir, indexConfig);
            Document newDoc = new Document();
            FieldType ftd = new FieldType(TextField.TYPE_STORED);
            ftd.setOmitNorms(true);
            ftd.setIndexOptions(IndexOptions.NONE);

            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);

            FieldType ft2 = new FieldType(TextField.TYPE_NOT_STORED);
            ft2.setOmitNorms(true);

            String removentachkil = doc.nass.replaceAll("([^\\u0621-\\u064A\\s])","");

            newDoc.add(new CustomField("nass", doc.nass, ftd));
            newDoc.add(new CustomField("m_nass", removentachkil, ft2));
            newDoc.add(new CustomField("bookId", doc.bookId, ft));
            newDoc.add(new CustomField("id", doc.id, ft));
            newDoc.add(new CustomField("part", doc.part, ft));
            newDoc.add(new CustomField("page", doc.page, ft));
            newDoc.add(new CustomField("aya", doc.aya ==null  ? "0" : doc.aya, ft));
            newDoc.add(new CustomField("sora", doc.sora ==null ? "0" : doc.sora, ft));
            Query query = new QueryParser("bookId",new StandardAnalyzer()).parse(""+doc.bookId+" AND id:"+doc.id);
            ArrayList<Document> documents = new ArrayList<>();
            documents.add(newDoc);
            writer.updateDocuments(query,documents);
            writer.commit();
            writer.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String updateTitle(Title doc) {
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Title));


            IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
            indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);                             // create/overwrite index
            indexConfig.setRAMBufferSizeMB(8000);
            // create/overwrite index
            IndexWriter writer = new IndexWriter(dir, indexConfig);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
            Title oldTitleObjec=new Title(doc.docId,searcher.storedFields().document(doc.docId));
            //writer.deleteDocuments( new QueryParser("bookId",new StandardAnalyzer()).parse(doc.bookId+" AND id:"+doc.id));
            Document newDoc = new Document();
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);
            newDoc.add(new Field("bookId", doc.bookId,ft));
            newDoc.add(new Field("title", doc.title, ft));
            newDoc.add(new Field("id", doc.id+"", ft));
            newDoc.add(new Field("lvl", doc.lvl==null?"0":doc.lvl, ft));
            newDoc.add(new Field("sub", doc.sub==null?"0":doc.sub, ft));
            Query query = new QueryParser("title",new StandardAnalyzer()).parse(oldTitleObjec.title);
             ArrayList<Document> documents = new ArrayList<>();
             documents.add(newDoc);
            writer.updateDocuments(query,documents);
            writer.commit();
            writer.close();
            return  "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String addPart(String part,String bookId) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);

            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT max(id) as id FROM Page WHERE bookId="+bookId);
            int max =1;
            while (rs.next()){
                max= rs.getInt("id")+1;

            }
            stmtLite.executeUpdate("insert into [Page] (bookId,id,part,page,sora,aya) values " +
                    "("+bookId+","+max+","+part+","+1+"," +0+","+0+")");
            connLite.close();
            //********************LUCENE*********************

            IndexWriter writer = newIndexWriter(path_Page);
            Document newDoc = new Document();
            FieldType ftd = new FieldType(TextField.TYPE_STORED);
            ftd.setOmitNorms(true);
            ftd.setIndexOptions(IndexOptions.NONE);

            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);

            FieldType ft2 = new FieldType(TextField.TYPE_NOT_STORED);
            ft2.setOmitNorms(true);
            String nass = "صفحة الاولي في الجزء" + part;
            String removentachkil = nass.replaceAll("([^\\u0621-\\u064A\\s])","");
            newDoc.add(new CustomField("nass", nass, ftd));
            newDoc.add(new CustomField("m_nass", removentachkil, ft2));
            newDoc.add(new CustomField("bookId", bookId, ft));
            newDoc.add(new CustomField("id", max, ft));
            newDoc.add(new CustomField("part", part, ft));
            newDoc.add(new CustomField("page", 1, ft));
            newDoc.add(new CustomField("aya", "0" , ft));
            newDoc.add(new CustomField("sora", "0" , ft));

            writer.addDocument(newDoc);
            writer.commit();
            writer.close();
            return "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String updatePart(String part,String bookId,String oldPart) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            stmtLite.executeUpdate("update [Page] SET part = "+part+" where bookId= " + bookId + " AND part = "+oldPart);
            connLite.close();
            //********************LUCENE*********************
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
            indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);                             // create/overwrite index
            indexConfig.setRAMBufferSizeMB(8000);
            // create/overwrite index
            IndexWriter writer = new IndexWriter(dir, indexConfig);

            FieldType ftd = new FieldType(TextField.TYPE_STORED);
            ftd.setOmitNorms(true);
            ftd.setIndexOptions(IndexOptions.NONE);

            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setOmitNorms(true);

            FieldType ft2 = new FieldType(TextField.TYPE_NOT_STORED);
            ft2.setOmitNorms(true);

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + bookId + " AND part:\"" + oldPart + "\"");

            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            ArrayList<Document> documents = new ArrayList<>();
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Page page = new Page(docId+"",docx);
                String removentachkil = page.nass.replaceAll("([^\\u0621-\\u064A\\s])","");
                Document newDoc = new Document();
                newDoc.add(new CustomField("nass", page.nass, ftd));
                newDoc.add(new CustomField("m_nass", removentachkil, ft2));
                newDoc.add(new CustomField("bookId", page.bookId, ft));
                newDoc.add(new CustomField("id", page.id, ft));
                newDoc.add(new CustomField("part", part, ft));
                newDoc.add(new CustomField("page", page.page, ft));
                newDoc.add(new CustomField("aya", page.aya ==null  ? "0" : page.aya, ft));
                newDoc.add(new CustomField("sora", page.sora ==null ? "0" : page.sora, ft));
                documents.add(newDoc);

            }
            Query updateQuery = new QueryParser("bookId",new StandardAnalyzer()).parse("" + bookId + " AND part:\"" + oldPart + "\"");

            writer.updateDocuments(updateQuery,documents);

            writer.commit();
            writer.close();

            return "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String deletePart(String part,String bookId) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            stmtLite.executeUpdate("delete from [Page]  where bookId= " + bookId + " AND part = "+part);
            connLite.close();
            //********************LUCENE*********************
            ArrayList<Integer>PagesIdList = new ArrayList<>();
            Directory dir = FSDirectory.open(Paths.get(path_Page));
            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse("" + bookId + " AND part:\"" + part + "\"");


            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                PagesIdList.add(Integer.parseInt(docx.get("id")));
            }

            ArrayList<BytesRef> terms = new ArrayList<>();
            for (int page : PagesIdList) {
                terms.add(new BytesRef(""+String.valueOf(page)+""));
            }
            TermInSetQuery termsInSetQuery = new TermInSetQuery("id", terms);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(new TermQuery(new Term("bookId",bookId+"")), BooleanClause.Occur.MUST);
            booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST);
            BooleanQuery bq = booleanQuery.build();


            // create/overwrite index
            IndexWriter writer = newIndexWriter(path_Title);

            writer.deleteDocuments(bq);
            writer.commit();
            writer.close();
            deleteDocument(path_Page, "bookId",bookId + " AND part:\"" + part + "\"");
            return "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String deleteCategoryById(Category doc) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            ResultSet rs = stmtLite.executeQuery("SELECT bookId FROM Book where catId = "+doc.catId);
            ArrayList<String> booksIdList = new ArrayList<>();
            while (rs.next()){
                booksIdList.add(rs.getString("bookId"));
            }
            ArrayList<BytesRef> terms = new ArrayList<>();
            for (String bookId : booksIdList) {
                terms.add(new BytesRef(""+bookId+""));
            }

            TermInSetQuery termsInSetQuery = new TermInSetQuery("bookId", terms);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST);
            BooleanQuery bq = booleanQuery.build();

            ///delete books
            IndexWriter writer = newIndexWriter(path_Book);
            writer.deleteDocuments(bq);

            ///delete pages
            IndexWriter writer2 = newIndexWriter(path_Page);
            writer2.deleteDocuments(bq);
            ///delete titles
            IndexWriter writer3 = newIndexWriter(path_Title);
            writer3.deleteDocuments(bq);

            writer.commit();
            writer.close();
            writer2.commit();
            writer2.close();
            writer3.commit();
            writer3.close();

            for (String bookId : booksIdList) {
                stmtLite.executeUpdate("delete from [Book]  where bookId= " + bookId );
                stmtLite.executeUpdate("delete from [Page]  where bookId= " + bookId );
            }
            ///delete category
            stmtLite.executeUpdate("delete from [Category]  where id= " + doc.catId );
            connLite.close();

            deleteDocument(path_Category, "catId",doc.catId);
            return "ok";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String deleteBookById(Book doc) {
        try {
            deleteFromSqlite("Book","bookId="+doc.bookId);
            deleteFromSqlite("Page","bookId="+doc.bookId);
            deleteDocument(path_Book, "bookId",doc.bookId);
            deleteDocument(path_Title, "bookId",doc.bookId);
            deleteDocument(path_Page, "bookId",doc.bookId);
            return "ok";
            //return message + "\t" + message1 + "\t" + message2;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String deleteListBooksById(ArrayList<Book> books) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
            Statement stmtLite = connLite.createStatement();
            for (Book book:
                    books) {
                stmtLite.executeUpdate("delete from [Book]  where bookId= " + book.bookId );
                stmtLite.executeUpdate("delete from [Page]  where bookId= " + book.bookId );
            }
            connLite.close();
            ArrayList<BytesRef> terms = new ArrayList<>();
            for (Book book : books) {
                terms.add(new BytesRef(""+book.bookId+""));
            }

            TermInSetQuery termsInSetQuery = new TermInSetQuery("bookId", terms);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST);
            BooleanQuery bq = booleanQuery.build();

            ///delete books
            IndexWriter writer = newIndexWriter(path_Book);
            writer.deleteDocuments(bq);
            ///delete pages
            IndexWriter writer2 = newIndexWriter(path_Page);
            writer2.deleteDocuments(bq);
            ///delete titles
            IndexWriter writer3 = newIndexWriter(path_Title);
            writer3.deleteDocuments(bq);

            writer.commit();
            writer.close();
            writer2.commit();
            writer2.close();
            writer3.commit();
            writer3.close();
            return "ok";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String deletePageById(Page doc) {
        try {
             deleteFromSqlite("Page","bookId="+doc.bookId+" AND id="+doc.id);
             deleteDocument(path_Page, "bookId",doc.bookId +" AND id:"+doc.id);
             deleteDocument(path_Title, "bookId",doc.bookId +" AND id:"+doc.id);

            return "ok";
            //return "sqlite: " + message +" lucene: "+ message1;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }
    public String deleteTitleById(Title doc) {
        try {
            deleteDocument(path_Title, "title","\""+doc.title+"\" AND bookId:"+doc.bookId+" AND id:"+doc.id);
            return "ok";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "error";
        }
    }

    public void deleteDocument(String _path,String _field,String _query) throws Exception{

    Directory dir = FSDirectory.open(Paths.get(_path));
    IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
    indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);                             // create/overwrite index
    indexConfig.setRAMBufferSizeMB(8000);

    IndexWriter writer = new IndexWriter(dir, indexConfig);
    writer.deleteDocuments(new QueryParser(_field,new StandardAnalyzer()).parse(_query));
    writer.commit();
    writer.close();

}
    public String deleteFromSqlite(String _tableName,String _Condition){

       try {
        Class.forName("org.sqlite.JDBC");
        Connection connLite = DriverManager.getConnection(sql_Connetion_Url);
        Statement stmtLite = connLite.createStatement();
        stmtLite.executeUpdate("delete from ["+_tableName+"] where "+ _Condition);
        connLite.close();
        return "ok";
    } catch (Exception e) {
        System.out.println(e.getMessage());
        return "error";
    }

}

 //////IMPORT********Books

    static public String getMaxBookIdFromSqlite() throws Exception{
    Class.forName("org.sqlite.JDBC");
    Connection conn = DriverManager.getConnection(sql_Connetion_Url+"");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select max(booKId) as maxid from [Book]");
    String _bookId = "1";
    while (rs.next()) {
        _bookId = (rs.getInt("maxid")+1)+"";
    }
    conn.close();
    return _bookId;
}

    static public IndexWriter newIndexWriter(String path) throws Exception{

        Directory dir = FSDirectory.open(Paths.get(path));
        IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);                             // create/overwrite index
        indexConfig.setRAMBufferSizeMB(8000);
        // create/overwrite index

        return new IndexWriter(dir, indexConfig);
    }
    static public void  insertBook(String _bookId,Book book,Statement stmt) throws Exception{
        stmt.executeUpdate("insert into [Book] (bookId,catId) values (" + _bookId  + "," + book.catId +")");

        IndexWriter writerBook = newIndexWriter(path_Book);
        Document newBook = new Document();
        FieldType ftBook = new FieldType(TextField.TYPE_STORED);
        ftBook.setOmitNorms(true);
        newBook.add(new Field("catId", book.catId, ftBook));
        newBook.add(new Field("name", book.name, ftBook));
        newBook.add(new Field("bookId", _bookId+"", ftBook));
        newBook.add(new Field("betaka", book.betaka, ftBook));

        writerBook.addDocument(newBook);
        writerBook.commit();
        writerBook.close();
    }
    static public void  insertPage(String _bookId, List<Page> pages,Statement stmt)throws Exception{
        for (Page _page : pages) {


                stmt.executeUpdate("insert into [Page] (bookId,id,part,page,sora,aya) values " +
                        "("+_bookId+","+_page.id+","+_page.part+","+_page.page+"," +_page.sora+","+_page.aya+")");

        }

        IndexWriter writerPages =newIndexWriter(path_Page);
        for (Page _page : pages) {


                Document newDoc = new Document();
                FieldType ftd = new FieldType(TextField.TYPE_STORED);
                ftd.setOmitNorms(true);
                ftd.setIndexOptions(IndexOptions.NONE);

                FieldType ft = new FieldType(TextField.TYPE_STORED);
                ft.setOmitNorms(true);

                FieldType ft2 = new FieldType(TextField.TYPE_NOT_STORED);
                ft2.setOmitNorms(true);

                String removentachkil = _page.nass.replaceAll("([^\\u0621-\\u064A\\s])","");
                newDoc.add(new CustomField("nass", _page.nass, ftd));
                newDoc.add(new CustomField("m_nass", removentachkil, ft2));
                newDoc.add(new CustomField("bookId", _bookId+"", ft));
                newDoc.add(new CustomField("id", _page.id+"", ft));
                newDoc.add(new CustomField("part", _page.part+"", ft));
                newDoc.add(new CustomField("page", _page.page+"", ft));
                newDoc.add(new CustomField("aya", _page.aya ==null  ? "0" : _page.aya, ft));
                newDoc.add(new CustomField("sora", _page.sora ==null ? "0" : _page.sora, ft));

                writerPages.addDocument(newDoc);


        }
        writerPages.commit();
        writerPages.close();
    }
    static public void  insertTitle(String _bookId, List<Title> titles)throws Exception{
    //                    for (Title _title : titles) {
//                        try {
//                            String _st = "insert into title (bookid, id,tit,lvl,sub) values (" + _bookid + "," + _title.page + ",N'" + _title.body + "'," + _title.id + "," + _title.parent + ")";
//                            stmt.executeUpdate(_st);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            System.out.println("Error in titles "+_title.id+"currnet index : '"+b+"' crrent book id :'"+_bookid+"' library id :'"+booksList.get(b).id+"'");
//                            break;
//                        }
//                    }
        IndexWriter writerTitle =newIndexWriter(path_Title);
        for (Title _title : titles) {

                Document newDoc = new Document();
                FieldType ft = new FieldType(TextField.TYPE_STORED);
                ft.setOmitNorms(true);
                newDoc.add(new Field("bookId", _bookId+"",ft));
                newDoc.add(new Field("title", _title.title, ft));
                newDoc.add(new Field("id", _title.id+"", ft));
                newDoc.add(new Field("lvl", _title.lvl==null?"0":_title.lvl, ft));
                newDoc.add(new Field("sub", _title.sub==null?"0":_title.sub, ft));

                writerTitle.addDocument(newDoc);

        }
        writerTitle.commit();
        writerTitle.close();

    }
     public String deleteProblems(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(sql_Connetion_Url+"");
            Statement stmt = conn.createStatement();

            ///delete books that do not have category
            deleteFromSqlite("Book","catId NOT in (select id from Category)");
            ResultSet rs = stmt.executeQuery("select id from category");
            ArrayList<BytesRef> terms = new ArrayList<>();
            while (rs.next()) {
                terms.add(new BytesRef(""+rs.getString("id")+""));
            }

            TermInSetQuery termsInSetQuery = new TermInSetQuery("catId", terms);
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
            booleanQuery.add(termsInSetQuery, BooleanClause.Occur.MUST_NOT);
            BooleanQuery bq = booleanQuery.build();
            IndexWriter writer = newIndexWriter(path_Book);
            writer.deleteDocuments(bq);
            writer.commit();
            writer.close();
            ///delete pages that do not have book
            deleteFromSqlite("Page","bookId NOT in (select bookId from Book)");
            rs = stmt.executeQuery("select bookId from Book");
            ArrayList<BytesRef>   bookSterms = new ArrayList<>();
            while (rs.next()) {
                bookSterms.add(new BytesRef(""+rs.getString("bookId")+""));
            }
             TermInSetQuery bookstermsInSetQuery = new TermInSetQuery("bookId", bookSterms);
             BooleanQuery.Builder  booksBooleanQuery = new BooleanQuery.Builder();
             booksBooleanQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
             booksBooleanQuery.add(bookstermsInSetQuery, BooleanClause.Occur.MUST_NOT);
             BooleanQuery booksbq = booksBooleanQuery.build();

            IndexWriter PageWriter = newIndexWriter(path_Page);
            var num=  PageWriter.deleteDocuments(booksbq);
            PageWriter.commit();
            PageWriter.close();

            ///delete titles that do not have book
            IndexWriter TitleWriter = newIndexWriter(path_Title);
            TitleWriter.deleteDocuments(booksbq);
            TitleWriter.commit();
            TitleWriter.close();
             return "ok: "+num;
        }catch (Exception e){
            return "error: "+e.getMessage();
        }
    }

    public ArrayList<Page> get_aya_tafseer(int bookId,int sora,int aya){

        ArrayList<Page> pageArrayList = new ArrayList<>();
        try {
            Directory dir = FSDirectory.open(Paths.get(path_Page));

            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse(bookId+"");
            TopDocs topDocs = searcher.search(query,1000000);
//            Query query = new QueryParser("bookId", new StandardAnalyzer()).parse(bookId+" AND sora:"+sora+" AND aya:"+aya);
//            TopDocs topDocs = searcher.search(query,1000000);

            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i=0;i<hits.length;i++){
                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                Page page = new Page(docId+"",docx);
                pageArrayList.add(page);
            }
            pageArrayList.sort(Comparator.comparingInt(x -> Integer.parseInt(x.id)));
            List<Page> soraAyat =pageArrayList.stream().filter(x->Integer.parseInt(x.sora)==sora &&Integer.parseInt(x.aya)!=0).toList();
            List<Page> AyaPageBefore = soraAyat.stream().filter(x->Integer.parseInt(x.aya)<=aya).toList();
            var min = AyaPageBefore.get((AyaPageBefore.size()-1));
            List<Page> AyaPageAfter = soraAyat.stream().filter(x->Integer.parseInt(x.aya)>aya).toList();
            AyaPageBefore.clear();
            AyaPageAfter.clear();
            Page max ;
            if (AyaPageAfter.size()>0){
                max = AyaPageAfter.get((0));
            }else{
                if (sora != 114){
                    var sorsad =pageArrayList.stream().filter(x->Integer.parseInt(x.sora)==sora+1 &&Integer.parseInt(x.aya)!=0).toList();
                    max = sorsad.get((0));
                }else {
                    max=pageArrayList.get(pageArrayList.size()-1);
                }

              //  max=pageArrayList.get(pageArrayList.size()-1);
            }
            var dfdf=pageArrayList.stream().filter(x->Integer.parseInt(x.id)>=Integer.parseInt(min.id)&&Integer.parseInt(x.id)<Integer.parseInt(max.id)).toList();


            pageArrayList.clear();
            pageArrayList.addAll(dfdf);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pageArrayList;
    }
    public void test() {
        String results = "";
        try {
            Directory dir = FSDirectory.open(Paths.get("D:\\shamela4_2\\database\\store\\page"));

            Map<String, Analyzer> analyzerMap = new HashMap<>();
            analyzerMap.put("body", new ArabicAnalyzer());
            analyzerMap.put("m_body", new MorphologyAnalyzer());
            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);


            DirectoryReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            // PhraseQuery pq = new PhraseQuery("body", "الحقيقة والشريعة");


            // QueryParser parser = new QueryParser("m_body", wrapper);

//            ArrayList<QueryParser> queryParserList = new ArrayList<QueryParser> () ;
//            queryParserList.add(new QueryParser("m_body",new MorphologyAnalyzer() ));
//            queryParserList.add(new QueryParser("m_body",new ArabicAnalyzer() ));
//            queryParserList.add(new QueryParser("m_body",new StandardAnalyzer() ));
//            queryParserList.add(new QueryParser("n_body",new MorphologyAnalyzer() ));
//            queryParserList.add(new QueryParser("n_body",new ArabicAnalyzer() ));
//            queryParserList.add(new QueryParser("n_body",new StandardAnalyzer() ));
//            queryParserList.add(new QueryParser("body",new MorphologyAnalyzer() ));
//            queryParserList.add(new QueryParser("body",new ArabicAnalyzer() ));
//            queryParserList.add(new QueryParser("body",new StandardAnalyzer() ));
//
//            for (int i=0 ;i<queryParserList.size();i++){
//                QueryParser parser = queryParserList.get(i);
//                Query query = parser.parse("\"طالب\"");
//                TopDocs topDocs = searcher.search(query, reader.maxDoc());
//                results +="index: "+i+" value :"+topDocs.scoreDocs.length+"<br/>";
//            }
            IndexWriterConfig config = new IndexWriterConfig();
            QueryParser parser = new ComplexPhraseQueryParser("m_body", new MorphologyAnalyzer());


            //Query query = parser.parse("الحقيقة والشريعة");
            String textquray = "الحقيقة والشريعة";
            Query query = parser.parse("\"" + textquray + "\"");
            // Query query = parser.parse("\"طالب\"");
            Analyzer analyzer = new ArabicRootExtractorAnalyzer();
            // Create a builder with the given searcher and analyzer
            UnifiedHighlighter.Builder builder = UnifiedHighlighter.builder(searcher, new StandardAnalyzer());

            // Set some highlighter options
            //  builder.withFieldMatcher(Predicate.isEqual("body"));
            builder.withHighlightPhrasesStrictly(true);
            UnifiedHighlighter highlighter = new UnifiedHighlighter(builder);

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"font-weight:bold;color:red;\">", "</span>");
            QueryScorer scorer = new QueryScorer(query);
            // Highlighter highlighter = new Highlighter(formatter, scorer);
            TopDocs topDocs = searcher.search(query, 3);
            //TopDocs topDocs = searcher.search(query,10);

            // UnifiedHighlighter highlighterm = new UnifiedHighlighter (searcher,wrapper);
            //  highlighterm.setFormatter(new DefaultPassageFormatter("<span style=\"color:red\">", "</span>", "...", false));
            //  var fds= highlighterm.highlight("m_body",query,topDocs,20);

            ScoreDoc[] hits = topDocs.scoreDocs;
            results = String.valueOf(hits.length);
            System.out.println(results);
            results += "<br/><br/>";
            //  var sd = query.toString().replace("\"","").split(":")[1];
            //  var search_words_list= sd.split(" ");

            for (int i = 0; i < 1; i++) {

                int docId = hits[i].doc;
                Document docx = searcher.storedFields().document(docId);
                String textt = docx.get("body");

                //     textt.replaceAll("[^\u0621-\u064A\s]","");
                //   MatchHighlighter matchHighlighter = new MatchHighlighter(searcher,new MorphologyAnalyzer());
                //  var sres=    matchHighlighter.highlight(topDocs,query);
//                ArrayList<String> mateched_words = new ArrayList<>();
//                var lislis=textt.split(" ");
//                for (String word_in_text:
//                        lislis) {
//
//                    var results_word= new AnalyzerTokens().analyzerToken(word_in_text);
//                    if(results_word.size()!=0){
//                        Result first_word=(Result)results_word.get(0);
//                        var root_word = first_word.getRoot();
//                        var converted_word=root_word;
//                        for (String search_word:
//                                search_words_list) {
//                            if (converted_word.contains(search_word)||search_word.contains(converted_word)){
//                                mateched_words.add(word_in_text);
//                            }
//                        }
//                    }
//
//                }
//                Set<String> set = new HashSet<>(mateched_words);
//                ArrayList<String> sl = new ArrayList<>(set);
//
//                for (String sddfdf:
//                     sl) {
//                    textt=textt.replace(sddfdf,"<span style=\"color:red\">"+sddfdf+" </span>");
//                }
                results += textt;
                results += "<br/><br/>";
                //results+=highlighter.getBestFragment(wrapper, "m_body", docx.get("body"));
                // results+="<br/><br/>";
                //results+=docx.get("body")+"";

                //  results+=highlighter.getBestFragment(analyzer,"body",docx.get("body"))+"";

                var dfd = highlighter.highlight("body", query, topDocs);
                results += dfd;
                // System.out.println("-------------------------");
                // System.out.println( highlighter.getBestFragment(analyzer, "body", docx.get("body")));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            results += "<br/><br/>";
            results += ex.getMessage();
        }
    }
}
