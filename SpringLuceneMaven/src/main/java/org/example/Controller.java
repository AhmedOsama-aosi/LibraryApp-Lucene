package org.example;


import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api")
public class Controller {
    private final MyServices myServices;
    private final ImportFromMsAccess importFromMsAccess;
    private final ImportFromShamla importFromShamla;
    private final ImportFromWord importFromWord;

    public Controller(MyServices myServices,ImportFromMsAccess importFromMsAccess,ImportFromShamla importFromShamla,ImportFromWord importFromWord) {
        this.myServices = myServices;
        this.importFromMsAccess = importFromMsAccess;
        this.importFromShamla = importFromShamla;
        this.importFromWord = importFromWord;
    }


    //@GetMapping(value="/")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Search_Result get_Search(@PathParam("words") String words, @PathParam("skip") int skip) {

        return myServices.getSearchPages(words, skip);
    }
    @RequestMapping(value = "/searchedPages", method = RequestMethod.POST)
    public Search_Result get_Searched_Pages(@PathParam("words") String words, @RequestBody ArrayList<String> docs) {

        return myServices.getSearchedPages(words,docs);
    }
    @RequestMapping(value = "/searchdocs", method = RequestMethod.GET)
    public Stream<String> get_Search_docs(@PathParam("words") String words,@PathParam("skip") int skip) throws Exception {
        return myServices.getSearchDocs(words,skip);
    }
    @RequestMapping(value = "/searchdocsInSelectedBook", method = RequestMethod.POST)
    public Stream<String> get_Search_docs_in_selected_books(@PathParam("words") String words ,@RequestBody ArrayList<String> books) throws Exception {

        return myServices.getSearchDocsInselectedBooks(words,books);
    }
    @RequestMapping(value = "/searchInSelectedBook", method = RequestMethod.POST)
    public Search_Result get_Search_in_selected_books(@PathParam("words") String words, @PathParam("skip") int skip,@RequestBody ArrayList<String> books) {

        return myServices.getSearchPagesInselectedBooks(words, skip,books);
    }
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public ArrayList<Category> get_Cat() {

        return myServices.getCategories();
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public ArrayList<Book> get_Cat_Books(@PathParam("catId") int catId) {

        return myServices.getCategoryBooks(catId);
    }

    @RequestMapping(value = "/book", method = RequestMethod.GET)
    public Book get_book(@PathParam("bookId") int bookId) {

        return myServices.getBookById(bookId);
    }

    @RequestMapping(value = "/bookParts", method = RequestMethod.GET)
    public ArrayList<Integer> get_book_Parts(@PathParam("bookId") int bookId) {

        return myServices.getBookParts(bookId);
    }

    @RequestMapping(value = "/searchBooks", method = RequestMethod.GET)
    public ArrayList<Book> get_search_books(@PathParam("words") String words) {

        return myServices.getSearchBooks(words);
    }

    @RequestMapping(value = "/searchInBook", method = RequestMethod.GET)
    public Search_Result get_Search_In_Book(@PathParam("words") String words, @PathParam("bookId") int bookId, @PathParam("skip") int skip) {

        return myServices.getSearchInBookPages(words, bookId, skip);
    }

    @RequestMapping(value = "/bookPages", method = RequestMethod.GET)
    public ArrayList<Page> get_book_Pages(@PathParam("bookId") int bookId) {

        return myServices.getPagesByBookId(bookId);
    }

    @RequestMapping(value = "/bookPartPages", method = RequestMethod.GET)
    public ArrayList<PagesResult> get_book_Part_Pages(@PathParam("bookId") int bookId, @PathParam("part") int part) {

        return myServices.getPartPagesById(bookId, part);
    }

    @RequestMapping(value = "/bookPartContants", method = RequestMethod.GET)
    public BookPartContent get_book_Part_Titles_and_Pages(@PathParam("bookId") int bookId, @PathParam("part") int part) {

        return myServices.getPartPagesTitlesId(bookId, part);
    }
    @RequestMapping(value = "/quran", method = RequestMethod.GET)
    public QuranContent get_quran_Titles_and_Pages(@PathParam("bookId") int bookId) {

        return myServices.get_quran(bookId);
    }
    @RequestMapping(value = "/TafseerBooks", method = RequestMethod.GET)
    public ArrayList<Book> get_search_books() {

        return myServices.getTafseerBooks();
    }
    @RequestMapping(value = "/TafseerAya", method = RequestMethod.GET)
    public ArrayList<Page> get_Aya_Tafseer(@PathParam("bookId") int bookId,@PathParam("sora") int sora,@PathParam("aya") int aya) {
        return myServices.get_tafseer(bookId,sora,aya);
    }
//    @RequestMapping(value = "/AyaTafseer", method = RequestMethod.GET)
//    public ArrayList<Page> get_AyaTafseer(@PathParam("bookId") int bookId,@PathParam("sora") int sora,@PathParam("aya") int aya) {
//        return myServices.get_aya_tafseer(bookId,sora,aya);
//    }
    @RequestMapping(value = "/bookPageByDoc", method = RequestMethod.GET)
    public Page get_book_Page(@PathParam("page") int page) {

        return myServices.getPageById(page);
    }

    @RequestMapping(value = "/bookPage", method = RequestMethod.GET)
    public Page get_book_Page(@PathParam("book") int bookId,@PathParam("page") int page) {

        return myServices.getPageById(bookId,page);
    }
    ///***********ADD********ADD*************
    @RequestMapping(value = "/addCategory", method = RequestMethod.POST)
    public String add_Category(@RequestBody  Category cat) {

        return myServices.addCategory(cat);
    }
    @RequestMapping(value = "/addBook", method = RequestMethod.POST)
    public String add_Book(@RequestBody  Book book) {

        return myServices.addBook(book);
    }
    @RequestMapping(value = "/addPart", method = RequestMethod.GET)
    public String add_Part(@PathParam("part") String part,@PathParam("bookId") String bookId) {
        return myServices.addPart(part,bookId);
    }

    @RequestMapping(value = "/addPage", method = RequestMethod.POST)
    public String add_Page(@RequestBody  Page page) {

        return myServices.addPage(page);
    }
    @RequestMapping(value = "/addTitle", method = RequestMethod.POST)
    public String add_Title(@RequestBody  Title title) {

        return myServices.addTitle(title);
    }
    ///**********************ADD**END***********************
    ///***********UPDATE********UPDATE*************
    @RequestMapping(value = "/updateCategory", method = RequestMethod.POST)
    public String update_Category(@RequestBody  Category cat) {

        return myServices.updateCategory(cat);
    }
    @RequestMapping(value = "/updateBook", method = RequestMethod.POST)
    public String update_Book(@RequestBody  Book book) {

        return myServices.updateBook(book);
    }
    @RequestMapping(value = "/updateListBooks", method = RequestMethod.POST)
    public String update_List_Books(@RequestBody  ArrayList<Book> books) {
        return myServices.updateListBooks(books);
    }
    @RequestMapping(value = "/updatePart", method = RequestMethod.GET)
    public String update_Part(@PathParam("part") String part,@PathParam("bookId") String bookId,@PathParam("oldPart") String oldPart) {
        return myServices.updatePart(part,bookId,oldPart);
    }
    @RequestMapping(value = "/updatePage", method = RequestMethod.POST)
    public String update_Page(@RequestBody  Page page) {
        return myServices.updatePage(page);
    }
    @RequestMapping(value = "/updateTitle", method = RequestMethod.POST)
    public String update_Title(@RequestBody  Title title) {

        return myServices.updateTitle(title);
    }
    ///**********************UPDATE**END***********************
    ///*******************DELETE**************DELETE**********
    @RequestMapping(value = "/deleteCategory", method = RequestMethod.POST)
    public String delete_Category(@RequestBody  Category category) {

        return myServices.deleteCategoryById(category);
    }
    @RequestMapping(value = "/deleteBook", method = RequestMethod.POST)
    public String delete_Book(@RequestBody  Book book) {

        return myServices.deleteBookById(book);
    }
    @RequestMapping(value = "/deleteListBooks", method = RequestMethod.POST)
    public String delete_List_Books(@RequestBody  ArrayList<Book> books) {

        return myServices.deleteListBooksById(books);
    }
    @RequestMapping(value = "/deletePart", method = RequestMethod.GET)
    public String delete_Part(@PathParam("part") String part,@PathParam("bookId") String bookId) {
        return myServices.deletePart(part,bookId);
    }
    @RequestMapping(value = "/deletePage", method = RequestMethod.POST)
    public String delete_Page(@RequestBody  Page page) {

        return myServices.deletePageById(page);
    }
    @RequestMapping(value = "/deleteTitle", method = RequestMethod.POST)
    public String delete_Title(@RequestBody  Title title) {

        return myServices.deleteTitleById(title);
    }

    ///**********************DELETE**END*******************
    @RequestMapping(value = "/addBookmark", method = RequestMethod.GET)
    public String add_bookmark(@PathParam("bookmark") String bookmark) {

        return myServices.addBookMark(bookmark);
    }

    @RequestMapping(value = "/deleteBookmark", method = RequestMethod.GET)
    public String delete_bookmark(@PathParam("bookmark") String bookmark) {

        return myServices.deleteBookMark(bookmark);
    }

    @RequestMapping(value = "/bookmarks", method = RequestMethod.GET)
    public ArrayList<BookMark> get_bookmarks() {

        return myServices.getBookMarks();
    }

    @RequestMapping(value = "/checkBookmarked", method = RequestMethod.GET)
    public boolean check_bookmarks(@PathParam("bookmark") String bookmark) {

        return myServices.checkIfBookmarked(bookmark);
    }
//*******************IMPORT***Books**************************
    @RequestMapping(value = "/importShamelaBooks", method = RequestMethod.POST)
    public String import_Books_Shamla(@RequestBody  ArrayList<String>BooksNames) {
        return importFromShamla.getBooksFromShamela(BooksNames);
    }
    @RequestMapping(value = "/importFromBok", method = RequestMethod.GET)
    public String import_Books_Sofeya() {
        return importFromMsAccess.getData();
    }
//    @RequestMapping(value = "/importSofeyaBooks", method = RequestMethod.POST)
//    public String importd_Books_Sofeya(@RequestBody String filename) {
//        return importFromMsAccess.getData(filename);
//
//    }

    @RequestMapping(value = "/importWordDoc", method = RequestMethod.GET)
    public String importd_Books_Word() throws Exception {
        return importFromWord.importFromDoc();
    }
    @RequestMapping(value = "/deleteProblems", method = RequestMethod.GET)
    public String delete_Problems() throws Exception {
        return myServices.deleteProblems();
    }

    @GetMapping(value = "/{name}")
    public String getName(@PathVariable String name) {
        return "the " + name + " hello";
    }
}
