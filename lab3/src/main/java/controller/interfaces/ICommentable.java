package controller.interfaces;

import controller.comment.CommentBO;

import java.util.List;

/**
 * Интерфейс комментируемой сущности (из UML: {@code ICommentable}).
 * Реализуется классом {@code NewsBO}.
 */
public interface ICommentable {

    /** Добавить комментарий. */
    void addComment(CommentBO comment);

    /** Удалить комментарий по идентификатору. */
    void removeComment(Long id);

    /** Получить все комментарии. */
    List<CommentBO> getComments();
}
