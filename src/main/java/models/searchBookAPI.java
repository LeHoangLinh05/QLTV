package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.net.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class searchBookAPI {

    public static List<Book> searchResult = new ArrayList<Book>();

    public void getBookInfos(String searchName) throws IOException {
        String encodedSearchName = URLEncoder.encode(searchName, "UTF-8");
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + encodedSearchName + "&maxResults=30&key=AIzaSyCyQ75JWs7yCqz3-JJULg-ZbzymlOfwEm4";
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        ObjectMapper obj = new ObjectMapper();
        JsonNode rootNode = obj.readTree(response.toString());
        JsonNode itemsNode = rootNode.get("items");

        int maxResults = Math.min(30, itemsNode.size());

        for (int i=0; i< maxResults; i++) {
            JsonNode itemNode = itemsNode.get(i);
            if (itemNode != null && itemNode.has("volumeInfo")) {
                JsonNode bookNode = itemsNode.get(i).get("volumeInfo");
                String title = bookNode.get("title").asText();
                String isbn = bookNode.has("industryIdentifiers") && bookNode.get("industryIdentifiers").get(1) != null
                        ? bookNode.get("industryIdentifiers").get(1).get("identifier").asText()
                        : "Unknown ISBN";
                String authors = bookNode.has("authors") ? bookNode.get("authors").get(0).asText() : "Unknown Authors";
                String publishedDate = bookNode.has("publishedDate") ? bookNode.get("publishedDate").asText() : "Unknown Published Date";
                String publisher = bookNode.has("publisher") ? bookNode.get("publisher").asText() : "Unknown Publisher";

                long pageCount = bookNode.has("pageCount") ? bookNode.get("pageCount").asLong() : 0;

                String categories = bookNode.has("categories") && bookNode.get("categories").get(0) != null
                        ? bookNode.get("categories").get(0).asText()
                        : "Unknown Categories";

                String description = bookNode.has("description") ? bookNode.get("description").asText() : " ";

                String thumbnailLink;
                if (bookNode.has("imageLinks") && bookNode.get("imageLinks").get("thumbnail") != null) {
                    JsonNode thumbnailNode = bookNode.get("imageLinks").get("thumbnail");
                    thumbnailLink = thumbnailNode.asText();
                } else {
                    thumbnailLink = "E:\\BookAPI\\src\\main\\resources\\images\\no_cover_thumb.png";
                }

                Book result = new Book(title, isbn, authors, publishedDate, publisher, pageCount, categories, description, thumbnailLink);

                searchResult.add(result);
            }
        }
    }

    public static void main(String[] args) throws IOException {
    }
}