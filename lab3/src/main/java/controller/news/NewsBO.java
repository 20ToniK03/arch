package controller.news;

import controller.category.CategoryBO;
import controller.comment.CommentBO;
import controller.interfaces.ICommentable;
import controller.interfaces.IPublishable;
import controller.person.JournalistBO;
import controller.tag.TagBO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Бизнес-объект «Новость» (UML: {@code News}).
 *
 * <p>Реализует {@link IPublishable} (жизненный цикл публикации) и
 * {@link ICommentable} (комментарии, композиция {@code News *-- Comment}).
 * Связана с категориями ({@code News 0..* -- 1..* Category}), тегами
 * ({@code News 0..* -- 0..* Tag}) и автором-журналистом
 * ({@code Journalist 1 -- 0..* News}).</p>
 */
public class NewsBO implements IPublishable, ICommentable {

    private Long id;
    private String title;
    private String text;
    private Date createdAt;
    private Date updatedAt;
    private NewsStatus status = NewsStatus.DRAFT;

    private JournalistBO author;
    private List<CategoryBO> categories = new ArrayList<CategoryBO>();
    private List<TagBO> tags = new ArrayList<TagBO>();
    private List<CommentBO> comments = new ArrayList<CommentBO>();

    // Вспомогательные поля (аннотация и регион - для поиска и интерфейса).
    private String annotation;
    private String region;
    private List<String> media = new ArrayList<String>();
    private int rating;

    public NewsBO() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public NewsBO(Long id, String title, String text) {
        this();
        this.id = id;
        this.title = title;
        this.text = text;
    }

    // IPublishable

    @Override
    public void publish() {
        this.status = NewsStatus.PUBLISHED;
        touch();
    }

    @Override
    public void unpublish() {
        this.status = NewsStatus.DRAFT;
        touch();
    }

    @Override
    public void archive() {
        this.status = NewsStatus.ARCHIVED;
        touch();
    }

    @Override
    public boolean isPublished() {
        return this.status == NewsStatus.PUBLISHED || this.status == NewsStatus.EDITED;
    }

    // ICommentable

    @Override
    public void addComment(CommentBO comment) {
        comments.add(comment);
    }

    @Override
    public void removeComment(Long id) {
        comments.removeIf(comment -> comment.getId() != null && comment.getId().equals(id));
    }

    @Override
    public List<CommentBO> getComments() {
        return comments;
    }

    // Поиск

    /**
     * Совпадает ли новость с ключевым словом (заголовок, аннотация, текст, теги).
     * Совпадения по категориям добавляет фасад.
     */
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
        for (TagBO tag : tags) {
            if (containsIgnoreCase(tag.getName(), needle)) {
                return true;
            }
        }
        return false;
    }

    // Бизнес-операции

    public void addCategory(CategoryBO category) {
        if (category != null && !categories.contains(category)) {
            categories.add(category);
        }
    }

    public void addTag(TagBO tag) {
        if (tag != null) {
            for (TagBO existing : tags) {
                if (existing.getName() != null && existing.getName().equalsIgnoreCase(tag.getName())) {
                    return;
                }
            }
            tags.add(tag);
        }
    }

    public void addMedia(String url) {
        if (url != null && !url.trim().isEmpty() && !media.contains(url)) {
            media.add(url);
        }
    }

    /** Обновить дату изменения. */
    public void touch() {
        this.updatedAt = new Date();
    }

    private static boolean containsIgnoreCase(String value, String lowerNeedle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(lowerNeedle);
    }

    // Геттеры / сеттеры

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public NewsStatus getStatus() {
        return status;
    }

    public void setStatus(NewsStatus status) {
        this.status = status;
    }

    public JournalistBO getAuthor() {
        return author;
    }

    public void setAuthor(JournalistBO author) {
        this.author = author;
    }

    public List<CategoryBO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryBO> categories) {
        this.categories = categories == null ? new ArrayList<CategoryBO>() : categories;
    }

    public List<TagBO> getTags() {
        return tags;
    }

    public void setTags(List<TagBO> tags) {
        this.tags = tags == null ? new ArrayList<TagBO>() : tags;
    }

    public void setComments(List<CommentBO> comments) {
        this.comments = comments == null ? new ArrayList<CommentBO>() : comments;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media == null ? new ArrayList<String>() : media;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
