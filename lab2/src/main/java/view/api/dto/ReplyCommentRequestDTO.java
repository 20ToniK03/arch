package view.api.dto;

/**
 * DTO запроса-ответа редактора на комментарий читателя.
 */
public class ReplyCommentRequestDTO {

    private String text;

    public ReplyCommentRequestDTO() {
    }

    public ReplyCommentRequestDTO(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
