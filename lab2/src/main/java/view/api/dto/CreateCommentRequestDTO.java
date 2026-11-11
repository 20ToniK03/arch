package view.api.dto;

/**
 * DTO запроса на добавление комментария.
 */
public class CreateCommentRequestDTO {

    private Long newsId;
    private String author;
    private String text;

    public CreateCommentRequestDTO() {
    }

    public CreateCommentRequestDTO(Long newsId, String author, String text) {
        this.newsId = newsId;
        this.author = author;
        this.text = text;
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
}
