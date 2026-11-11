package controller.news;

/**
 * Статус публикации новости - соответствует диаграмме состояний UML:
 * Создана (Draft) → Опубликована (Published) → Отредактирована (Edited)
 * → Опубликована → Удалена (Deleted), а также Архив (Archived) из
 * диаграммы деятельности.
 */
public enum NewsStatus {
    DRAFT,
    PUBLISHED,
    EDITED,
    ARCHIVED,
    DELETED
}
