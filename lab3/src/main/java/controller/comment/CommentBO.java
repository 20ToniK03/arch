package controller.comment;

import java.util.Date;

/**
 * Бизнес-объект «Комментарий» (UML: {@code Comment}).
 */
public class CommentBO {

    private Long id;
    private Long newsId;
    private String author;
    private String text;
    private Date createdAt;

    public CommentBO() {
    }

    public CommentBO(Long id, Long newsId, String author, String text, Date createdAt) {
        this.id = id;
        this.newsId = newsId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    /** Редактировать текст комментария (метод {@code edit} из UML). */
    public void edit(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
}
