package view.api.dto;

/**
 * DTO краткой информации о новости - элемент списка новостей.
 *
 * <p>В списке клиенту не нужен полный текст, поэтому передаются только заголовок,
 * категория и дата публикации.</p>
 */
public class NewsTitleDTO {

    private Long id;
    private String title;
    private String category;
    private String publishedAt;

    public NewsTitleDTO() {
    }

    public NewsTitleDTO(Long id, String title, String category, String publishedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.publishedAt = publishedAt;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
