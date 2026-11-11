package controller.interfaces;

/**
 * Интерфейс авторизации (из UML-диаграммы классов: {@code IAuthorizable}).
 * Реализуется иерархией {@code PersonBO}.
 */
public interface IAuthorizable {

    /** Попытка входа по логину (email) и паролю. */
    boolean login(String login, String password);

    /** Выход из учётной записи. */
    void logout();

    /** Авторизован ли пользователь в данный момент. */
    boolean isAuthorized();
}
