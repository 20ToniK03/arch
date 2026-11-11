package controller.category;

import java.util.Locale;

/**
 * Бизнес-объект «Категория» (UML: {@code Category} - {@code id}, {@code name}).
 * Поле {@code description} - вспомогательное (для подсказок в интерфейсе).
 */
public class CategoryBO {

    private Long id;
    private String name;
    private String description;

    public CategoryBO() {
    }

    public CategoryBO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Совпадает ли категория с ключевым словом (по названию или описанию).
     * Используется в алгоритме поиска новостей: результат объединяется
     * с совпадениями по тексту новостей.
     */
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String needle = keyword.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(name, needle) || containsIgnoreCase(description, needle);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
