package controller.person;

import controller.comment.CommentBO;
import controller.role.RoleBO;

/**
 * Бизнес-объект «Администратор» (UML: {@code Admin}).
 *
 * <p>Отвечает за use case «Администрирование»: блокировку пользователей
 * ({@code blockUser}), назначение ролей ({@code assignRole}) и модерацию
 * комментариев ({@code moderateComment}).</p>
 */
public class AdminBO extends UserBO {

    public AdminBO(Long id, String name, String email) {
        super(id, name, email);
    }

    /** Заблокировать пользователя (метод {@code blockUser} из UML). */
    public void blockUser(UserBO user) {
        if (user != null) {
            user.setBlocked(true);
        }
    }

    /** Назначить пользователю роль (метод {@code assignRole} из UML). */
    public void assignRole(UserBO user, RoleBO role) {
        if (user != null) {
            user.addRole(role);
        }
    }

    /** Одобрить комментарий (метод {@code moderateComment} из UML). */
    public void moderateComment(CommentBO comment) {
        if (comment != null) {
            comment.setApproved(true);
        }
    }
}
