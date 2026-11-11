package controller.person;

/**
 * Бизнес-объект «Гость» (UML: {@code Guest}).
 *
 * <p>Неавторизованный посетитель: может просматривать/искать новости и
 * регистрироваться ({@code register(data): User}).</p>
 */
public class GuestBO extends PersonBO {

    public GuestBO() {
        super(null, "Гость", null);
    }

    /** Регистрирует нового пользователя (метод {@code register} из UML). */
    public UserBO register(RegistrationData data) {
        if (data == null) {
            return null;
        }
        UserBO user = new UserBO(null, data.getName(), data.getEmail());
        user.setPasswordHash(data.getPassword());
        return user;
    }
}
