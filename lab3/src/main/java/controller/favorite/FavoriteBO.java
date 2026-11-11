package controller.favorite;

import controller.news.NewsBO;

import java.util.Date;

/**
 * Бизнес-объект «Избранное» (UML: {@code Favorite} - {@code id}, {@code addedAt}).
 * Связывает пользователя с сохранённой новостью (use case «Избранное»).
 */
public class FavoriteBO {

    private Long id;
    private Date addedAt;
    private NewsBO news;

    public FavoriteBO() {
    }

    public FavoriteBO(Long id, Date addedAt, NewsBO news) {
        this.id = id;
        this.addedAt = addedAt;
        this.news = news;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public NewsBO getNews() {
        return news;
    }

    public void setNews(NewsBO news) {
        this.news = news;
    }
}
