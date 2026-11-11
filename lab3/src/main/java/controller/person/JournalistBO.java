package controller.person;

import controller.news.NewsBO;
import controller.news.NewsData;
import controller.news.NewsStatus;

/**
 * Бизнес-объект «Журналист» (UML: {@code Journalist}) - создатель контента.
 *
 * <p>Связан с новостями ({@code Journalist 1 -- 0..* News}) и имеет методы
 * {@code createNews}, {@code editNews}, {@code deleteNews}. Дополнительно
 * используется в диаграмме деятельности (создание и публикация новости).</p>
 */
public class JournalistBO extends UserBO {

    private double rating;

    public JournalistBO(Long id, String name, String email) {
        super(id, name, email);
    }

    /** Создать новость-черновик (метод {@code createNews} из UML). */
    public NewsBO createNews(NewsData data) {
        NewsBO news = new NewsBO(null, data.getTitle(), data.getText());
        news.setAuthor(this);
        news.setAnnotation(data.getAnnotation());
        news.setRegion(data.getRegion());
        news.setStatus(NewsStatus.DRAFT);
        return news;
    }

    /** Отредактировать новость (метод {@code editNews} из UML). */
    public void editNews(NewsBO news, NewsData data) {
        if (news == null || data == null) {
            return;
        }
        news.setTitle(data.getTitle());
        news.setText(data.getText());
        news.setAnnotation(data.getAnnotation());
        news.setRegion(data.getRegion());
        news.touch();
        if (news.getStatus() == NewsStatus.PUBLISHED) {
            news.setStatus(NewsStatus.EDITED);
        }
    }

    /** Удалить новость (метод {@code deleteNews} из UML). */
    public void deleteNews(NewsBO news) {
        if (news != null) {
            news.setStatus(NewsStatus.DELETED);
        }
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
