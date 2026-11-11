package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO полной новости - то, что клиент получает при открытии одной новости.
 *
 * <p>Класс намеренно не содержит бизнес-логики: это только «плоская» структура
 * для передачи данных клиенту (шаблон DTO). Внутренние поля предметной области
 * (например, рейтинг и служебные данные автора) здесь не раскрываются.</p>
 */
public class NewsItemDTO {

    private Long id;
    private String title;
    private String annotation;
    private String text;
    private String category;
    private String author;
    private String publishedAt;
    private String region;
    private String status;
    private List<String> tags = new ArrayList<String>();
    private List<String> media = new ArrayList<String>();
    private int rating;

    public NewsItemDTO() {
    }

    public NewsItemDTO(Long id, String title, String annotation, String text,
                       String category, String author, String publishedAt,
                       String region, List<String> tags, List<String> media, int rating) {
        this.id = id;
        this.title = title;
        this.annotation = annotation;
        this.text = text;
        this.category = category;
        this.author = author;
        this.publishedAt = publishedAt;
        this.region = region;
        this.tags = tags;
        this.media = media;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
