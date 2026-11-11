package controller.person;

import controller.interfaces.IAuthorizable;

/**
 * Абстрактный класс «Персона» (UML: {@code Person} - {@code id}, {@code name},
 * {@code email}, {@code passwordHash}). Реализует {@link IAuthorizable}.
 *
 * <p>Иерархия наследования: {@code Person <- Guest}, {@code Person <- User},
 * {@code User <- Journalist}, {@code User <- Admin}.</p>
 */
public abstract class PersonBO implements IAuthorizable {

    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private boolean authorized;

    public PersonBO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean login(String login, String password) {
        boolean ok = email != null && email.equals(login)
                && passwordHash != null && passwordHash.equals(password);
        this.authorized = ok;
        return ok;
    }

    @Override
    public void logout() {
        this.authorized = false;
    }

    @Override
    public boolean isAuthorized() {
        return authorized;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
