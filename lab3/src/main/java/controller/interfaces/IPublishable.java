package controller.interfaces;

/**
 * Интерфейс публикуемой сущности (из UML: {@code IPublishable}).
 * Реализуется классом {@code NewsBO} и отражает жизненный цикл новости
 * (см. диаграмму состояний: Создана → Опубликована → Отредактирована → … → Удалена).
 */
public interface IPublishable {

    /** Опубликовать. */
    void publish();

    /** Снять с публикации (вернуть в черновик). */
    void unpublish();

    /** Перевести в архив. */
    void archive();

    /** Опубликована ли сущность в данный момент. */
    boolean isPublished();
}
