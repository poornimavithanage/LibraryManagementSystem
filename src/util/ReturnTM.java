package util;

public class ReturnTM {
    private String borrowId;
    private String isbn;
    private String title;
    private String author;
    private String borrowedDate;

    public ReturnTM() {
    }

    public ReturnTM(String borrowId, String isbn, String title, String author, String borrowedDate) {
        this.borrowId = borrowId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.borrowedDate = borrowedDate;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(String borrowedDate) {
        this.borrowedDate = borrowedDate;
    }
}
