package view.api.dto;

/**
 * DTO запроса на редактирование комментария.
 */
public class UpdateCommentRequestDTO {

    private String text;

    public UpdateCommentRequestDTO() {
    }

    public UpdateCommentRequestDTO(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
