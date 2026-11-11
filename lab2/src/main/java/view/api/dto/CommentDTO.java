package view.api.dto;

/**
 * DTO комментария читателя.
 */
public class CommentDTO {

    private Long id;
    private Long newsId;
    private String author;
    private String text;
    private String createdAt;
    private String reply;

    public CommentDTO() {
    }

    public CommentDTO(Long id, Long newsId, String author, String text,
                      String createdAt, String reply) {
        this.id = id;
        this.newsId = newsId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
        this.reply = reply;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
