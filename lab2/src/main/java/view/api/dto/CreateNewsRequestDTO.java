package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO запроса на создание новости (то, что клиент присылает в теле POST-запроса).
 */
public class CreateNewsRequestDTO {

    private String title;
    private String annotation;
    private String text;
    private Long categoryId;
    private String author;
    private String region;
    private List<String> tags = new ArrayList<String>();

    public CreateNewsRequestDTO() {
    }

    public CreateNewsRequestDTO(String title, String annotation, String text,
                                Long categoryId, String author, String region,
                                List<String> tags) {
        this.title = title;
        this.annotation = annotation;
        this.text = text;
        this.categoryId = categoryId;
        this.author = author;
        this.region = region;
        this.tags = tags;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
