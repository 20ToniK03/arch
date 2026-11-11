package view.api.dto;

/**
 * Универсальный DTO результата действия (создание, изменение, удаление,
 * добавление в закладки и т.п.).
 */
public class ActionStatusDTO {

    private String status;
    private String message;

    public ActionStatusDTO() {
    }

    public ActionStatusDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    /** Успешное выполнение действия. */
    public static ActionStatusDTO ok(String message) {
        return new ActionStatusDTO("OK", message);
    }

    /** Неуспешное выполнение действия. */
    public static ActionStatusDTO error(String message) {
        return new ActionStatusDTO("ERROR", message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
