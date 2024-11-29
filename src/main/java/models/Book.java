package models;

import java.util.*;

/**
 * Represents a book with various attributes such as title, author, ISBN, etc.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String ISBN;
    private String publishedDate;
    private String publisher;
    private long pageCount;
    private String categories;
    private String description;
    private String thumbnailLink;
    private int quantity;

    /**
     * Default constructor for creating an empty Book object.
     */
    public Book() {}

    /**
     * Constructs a Book with the specified id and title.
     *
     * @param id the id of the book
     * @param title the title of the book
     */
    public Book(int id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Constructs a Book with the specified attributes.
     *
     * @param title the title of the book
     * @param ISBN the ISBN of the book
     * @param author the author of the book
     * @param publishedDate the published date of the book
     * @param publisher the publisher of the book
     * @param pageCount the number of pages in the book
     * @param categories the categories of the book
     * @param description the description of the book
     * @param thumbnailLink the thumbnail link of the book
     */
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
        this.quantity = 10;
    }

    /**
     * Returns the id of the book.
     *
     * @return the id of the book
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the book.
     *
     * @param id the new id of the book
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the new title of the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param ISBN the new ISBN of the book
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * Returns the author of the book.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the new author of the book
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the published date of the book.
     *
     * @return the published date of the book
     */
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * Sets the published date of the book.
     *
     * @param publishedDate the new published date of the book
     */
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * Returns the publisher of the book.
     *
     * @return the publisher of the book
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     *
     * @param publisher the new publisher of the book
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Returns the number of pages in the book.
     *
     * @return the number of pages in the book
     */
    public long getPageCount() {
        return pageCount;
    }

    /**
     * Sets the number of pages in the book.
     *
     * @param pageCount the new number of pages in the book
     */
    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Returns the categories of the book.
     *
     * @return the categories of the book
     */
    public String getCategories() {
        return categories;
    }

    /**
     * Sets the categories of the book.
     *
     * @param categories the new categories of the book
     */
    public void setCategories(String categories) {
        this.categories = categories;
    }

    /**
     * Returns the description of the book.
     *
     * @return the description of the book
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the book.
     *
     * @param description the new description of the book
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the thumbnail link of the book.
     *
     * @return the thumbnail link of the book
     */
    public String getThumbnailLink() {
        return thumbnailLink;
    }

    /**
     * Sets the thumbnail link of the book.
     *
     * @param thumbnailLink the new thumbnail link of the book
     */
    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    /**
     * Returns the quantity of the book.
     *
     * @return the quantity of the book
     */
    public int getQuantity() { return quantity; }

    /**
     * Sets the quantity of the book.
     *
     * @param quantity the new quantity of the book
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }

}