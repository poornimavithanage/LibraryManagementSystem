package util;

public class BookTM {
    private String isbn;
    private String bookTitle;
    private String author;
    private String edition;

    public BookTM() {
    }

    public BookTM(String isbn, String bookTitle, String author, String edition) {
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.author = author;
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    @Override
    public String toString() {
        return isbn;
    }
}
