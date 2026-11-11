package controller.news;

import controller.category.CategoryBO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Бизнес-объект «Новость» (UML: {@code News}).
 *
 * <p>Описывает предметную область новости: заголовок, аннотация, текст,
 * категория, автор, регион, дата публикации, теги и рейтинг.</p>
 */
public class NewsBO {

    private Long id;
    private String title;
    private String annotation;
    private String text;
    private CategoryBO category;
    private String author;
    private String region;
    private Date publishedAt;
    private List<String> tags = new ArrayList<String>();
    private int rating;

    public NewsBO() {
        this.publishedAt = new Date();
    }

    public NewsBO(Long id, String title, String text) {
        this();
        this.id = id;
        this.title = title;
        this.text = text;
    }

    /** Совпадает ли новость с ключевым словом (заголовок, аннотация, текст, теги). */
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String needle = keyword.toLowerCase(Locale.ROOT);
        if (containsIgnoreCase(title, needle)
                || containsIgnoreCase(annotation, needle)
                || containsIgnoreCase(text, needle)) {
            return true;
        }
        for (String tag : tags) {
            if (containsIgnoreCase(tag, needle)) {
                return true;
            }
        }
        return false;
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            String trimmed = tag.trim();
            for (String existing : tags) {
                if (existing.equalsIgnoreCase(trimmed)) {
                    return;
                }
            }
            tags.add(trimmed);
        }
    }

    private static boolean containsIgnoreCase(String value, String lowerNeedle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(lowerNeedle);
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

    public CategoryBO getCategory() {
        return category;
    }

    public void setCategory(CategoryBO category) {
        this.category = category;
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

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags == null ? new ArrayList<String>() : tags;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
