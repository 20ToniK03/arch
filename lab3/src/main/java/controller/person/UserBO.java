package controller.person;

import controller.news.NewsBO;
import controller.favorite.FavoriteBO;
import controller.role.RoleBO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Бизнес-объект «Пользователь» (UML: {@code User}).
 *
 * <p>Содержит дату регистрации, признак блокировки, роли и избранное.
 * Связан с комментариями ({@code User 1 -- 0..* Comment}) и избранным
 * ({@code User 1 -- 0..* Favorite}).</p>
 */
public class UserBO extends PersonBO {

    private Date registrationDate;
    private boolean blocked;
    private List<RoleBO> roles = new ArrayList<RoleBO>();
    private List<FavoriteBO> favorites = new ArrayList<FavoriteBO>();

    public UserBO(Long id, String name, String email) {
        super(id, name, email);
        this.registrationDate = new Date();
    }

    /** Добавить новость в избранное (метод {@code addToFavorites} из UML). */
    public void addToFavorites(NewsBO news) {
        for (FavoriteBO favorite : favorites) {
            if (sameNews(favorite.getNews(), news)) {
                return;
            }
        }
        favorites.add(new FavoriteBO(null, new Date(), news));
    }

    /** Удалить новость из избранного (метод {@code removeFromFavorites} из UML). */
    public void removeFromFavorites(NewsBO news) {
        favorites.removeIf(favorite -> sameNews(favorite.getNews(), news));
    }

    /** Проверить, есть ли новость в избранном. */
    public boolean isFavorite(NewsBO news) {
        for (FavoriteBO favorite : favorites) {
            if (sameNews(favorite.getNews(), news)) {
                return true;
            }
        }
        return false;
    }

    /** Добавить роль, если её ещё нет. */
    public boolean addRole(RoleBO role) {
        if (role == null) {
            return false;
        }
        for (RoleBO existing : roles) {
            if (existing.getName() != null && existing.getName().equalsIgnoreCase(role.getName())) {
                return false;
            }
        }
        roles.add(role);
        return true;
    }

    private static boolean sameNews(NewsBO first, NewsBO second) {
        if (first == null || second == null) {
            return first == second;
        }
        return first.getId() != null && first.getId().equals(second.getId());
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public List<RoleBO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleBO> roles) {
        this.roles = roles == null ? new ArrayList<RoleBO>() : roles;
    }

    public List<FavoriteBO> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoriteBO> favorites) {
        this.favorites = favorites == null ? new ArrayList<FavoriteBO>() : favorites;
    }
}
