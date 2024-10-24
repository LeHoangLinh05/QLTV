package models;

import java.util.*;

public class Book {
    private String title;
    private String author;
    private String ISBN;
    private String publishedDate;
    private String publisher;
    private long pageCount;
    private String categories;
    private String description;
    private String thumbnailLink;

    public Book() {}

    public Book(String title, String ISBN, String author, String publishedDate, String publisher, long pageCount, String categories, String description, String thumbnailLink) {
        this.title = title;
        this.ISBN = ISBN;
        this.author = author;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.pageCount = pageCount;
        this.categories = categories;
        this.description = description;
        this.thumbnailLink = thumbnailLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void getDescription(String description) {
        this.description = description;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

}
