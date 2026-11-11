package controller;

import controller.category.CategoryBO;
import controller.comment.CommentBO;
import controller.news.NewsBO;
import model.IdSequence;
import model.InMemoryStore;
import view.api.dto.CategoryDTO;
import view.api.dto.CommentDTO;
import view.api.dto.CreateCommentRequestDTO;
import view.api.dto.CreateNewsRequestDTO;
import view.api.dto.NewsItemDTO;
import view.api.dto.NewsTitleDTO;
import view.api.dto.UpdateCommentRequestDTO;
import view.api.dto.UpdateNewsRequestDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Фасад (паттерн Facade) между слоем представления и предметной областью.
 *
 * <p>Скрывает от API бизнес-объекты ({@link NewsBO}, {@link CategoryBO},
 * {@link CommentBO}) и хранилище; принимает DTO запросов и возвращает DTO ответов.
 * Реализует поиск новостей по ключевым словам с объединением результатов из
 * текста новостей и названий категорий.</p>
 */
public final class FacadeController {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final long DAY_MILLIS = 24L * 60L * 60L * 1000L;

    private static final FacadeController INSTANCE = new FacadeController();

    private final InMemoryStore<NewsBO> newsStore = new InMemoryStore<NewsBO>();
    private final InMemoryStore<CategoryBO> categoryStore = new InMemoryStore<CategoryBO>();
    private final InMemoryStore<CommentBO> commentStore = new InMemoryStore<CommentBO>();

    private final IdSequence newsIds = new IdSequence();
    private final IdSequence categoryIds = new IdSequence();
    private final IdSequence commentIds = new IdSequence();

    private FacadeController() {
        seedCategories();
        seedNews();
        seedComments();
    }

    public static FacadeController getInstance() {
        return INSTANCE;
    }

    // Чтение

    public synchronized List<NewsTitleDTO> getAllNews() {
        return toTitleList(newsStore.findAll());
    }

    public synchronized NewsItemDTO getNewsById(long id) {
        NewsBO news = newsStore.find(id);
        return news == null ? null : toNewsItemDTO(news);
    }

    public synchronized List<NewsTitleDTO> getLastWeekNews() {
        long from = System.currentTimeMillis() - 7 * DAY_MILLIS;
        List<NewsBO> result = new ArrayList<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            if (news.getPublishedAt() != null && news.getPublishedAt().getTime() >= from) {
                result.add(news);
            }
        }
        return toTitleList(result);
    }

    public synchronized List<NewsTitleDTO> searchNews(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<NewsTitleDTO>();
        }
        Set<NewsBO> found = new LinkedHashSet<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            for (String keyword : keywords) {
                if (news.matchesKeyword(keyword) || categoryMatches(news, keyword)) {
                    found.add(news);
                    break;
                }
            }
        }
        return toTitleList(new ArrayList<NewsBO>(found));
    }

    public synchronized List<NewsTitleDTO> getNewsByRegion(String region) {
        if (region == null) {
            return new ArrayList<NewsTitleDTO>();
        }
        String needle = region.trim();
        List<NewsBO> result = new ArrayList<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            if (news.getRegion() != null && news.getRegion().equalsIgnoreCase(needle)) {
                result.add(news);
            }
        }
        return toTitleList(result);
    }

    public synchronized List<CategoryDTO> getCategories() {
        List<CategoryDTO> result = new ArrayList<CategoryDTO>();
        for (CategoryBO category : categoryStore.findAll()) {
            result.add(toCategoryDTO(category));
        }
        return result;
    }

    // Изменение

    public synchronized NewsItemDTO createNews(CreateNewsRequestDTO request) {
        if (request == null || isBlank(request.getTitle())) {
            return null;
        }
        CategoryBO category = request.getCategoryId() == null
                ? null : categoryStore.find(request.getCategoryId());
        if (category == null) {
            return null;
        }

        NewsBO news = new NewsBO(newsIds.next(), request.getTitle().trim(), trimToNull(request.getText()));
        news.setAnnotation(trimToNull(request.getAnnotation()));
        news.setCategory(category);
        news.setAuthor(isBlank(request.getAuthor()) ? "Редакция" : request.getAuthor().trim());
        news.setRegion(isBlank(request.getRegion()) ? "Москва" : request.getRegion().trim());
        news.setPublishedAt(new Date());
        if (request.getTags() != null) {
            for (String tag : request.getTags()) {
                news.addTag(tag);
            }
        }
        newsStore.put(news.getId(), news);
        return toNewsItemDTO(news);
    }

    public synchronized NewsItemDTO updateNews(long id, UpdateNewsRequestDTO request) {
        NewsBO news = newsStore.find(id);
        if (news == null || request == null) {
            return null;
        }
        if (!isBlank(request.getTitle())) {
            news.setTitle(request.getTitle().trim());
        }
        if (!isBlank(request.getAnnotation())) {
            news.setAnnotation(request.getAnnotation().trim());
        }
        if (!isBlank(request.getText())) {
            news.setText(request.getText().trim());
        }
        if (!isBlank(request.getRegion())) {
            news.setRegion(request.getRegion().trim());
        }
        if (request.getCategoryId() != null) {
            CategoryBO category = categoryStore.find(request.getCategoryId());
            if (category != null) {
                news.setCategory(category);
            }
        }
        if (request.getTags() != null) {
            news.setTags(new ArrayList<String>(request.getTags()));
        }
        return toNewsItemDTO(news);
    }

    public synchronized boolean deleteNews(long id) {
        if (newsStore.find(id) == null) {
            return false;
        }
        newsStore.remove(id);
        for (CommentBO comment : commentStore.findAll()) {
            if (comment.getNewsId() != null && comment.getNewsId() == id) {
                commentStore.remove(comment.getId());
            }
        }
        return true;
    }

    public synchronized CommentDTO createComment(CreateCommentRequestDTO request) {
        if (request == null || request.getNewsId() == null || isBlank(request.getText())) {
            return null;
        }
        if (newsStore.find(request.getNewsId()) == null) {
            return null;
        }
        CommentBO comment = new CommentBO(
                commentIds.next(),
                request.getNewsId(),
                isBlank(request.getAuthor()) ? "Аноним" : request.getAuthor().trim(),
                request.getText().trim(),
                new Date()
        );
        commentStore.put(comment.getId(), comment);
        return toCommentDTO(comment);
    }

    public synchronized CommentDTO updateComment(long id, UpdateCommentRequestDTO request) {
        CommentBO comment = commentStore.find(id);
        if (comment == null || request == null || isBlank(request.getText())) {
            return null;
        }
        comment.edit(request.getText().trim());
        return toCommentDTO(comment);
    }

    public synchronized boolean deleteComment(long id) {
        return commentStore.remove(id);
    }

    // BO -> DTO

    private List<NewsTitleDTO> toTitleList(List<NewsBO> newsList) {
        List<NewsBO> sorted = new ArrayList<NewsBO>(newsList);
        sorted.sort((first, second) -> {
            long a = first.getPublishedAt() == null ? 0L : first.getPublishedAt().getTime();
            long b = second.getPublishedAt() == null ? 0L : second.getPublishedAt().getTime();
            return Long.compare(b, a);
        });

        List<NewsTitleDTO> result = new ArrayList<NewsTitleDTO>();
        for (NewsBO news : sorted) {
            result.add(toNewsTitleDTO(news));
        }
        return result;
    }

    private NewsTitleDTO toNewsTitleDTO(NewsBO news) {
        return new NewsTitleDTO(
                news.getId(),
                news.getTitle(),
                news.getCategory() == null ? null : news.getCategory().getName(),
                format(news.getPublishedAt())
        );
    }

    private NewsItemDTO toNewsItemDTO(NewsBO news) {
        return new NewsItemDTO(
                news.getId(),
                news.getTitle(),
                news.getAnnotation(),
                news.getText(),
                news.getCategory() == null ? null : news.getCategory().getName(),
                news.getAuthor(),
                format(news.getPublishedAt()),
                news.getRegion(),
                new ArrayList<String>(news.getTags()),
                new ArrayList<String>(),
                news.getRating()
        );
    }

    private CategoryDTO toCategoryDTO(CategoryBO category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }

    private CommentDTO toCommentDTO(CommentBO comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getNewsId(),
                comment.getAuthor(),
                comment.getText(),
                format(comment.getCreatedAt())
        );
    }

    // Вспомогательные методы

    private boolean categoryMatches(NewsBO news, String keyword) {
        return news.getCategory() != null && news.getCategory().matchesKeyword(keyword);
    }

    private String format(Date date) {
        return date == null ? null : DATE_TIME_FORMAT.format(date);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static Date daysAgo(int days) {
        return new Date(System.currentTimeMillis() - (long) days * DAY_MILLIS);
    }

    // Начальные (тестовые) данные

    private void seedCategories() {
        addCategory("Технологии", "IT, гаджеты и инновации");
        addCategory("Общество", "Городские события и жизнь горожан");
        addCategory("Спорт", "Соревнования и спортивные результаты");
        addCategory("Наука", "Открытия, исследования и образование");
        addCategory("Экономика", "Финансы, бизнес и экономическая политика");
        addCategory("Культура", "Выставки, театр, музыка и кино");
    }

    private void addCategory(String name, String description) {
        CategoryBO category = new CategoryBO(categoryIds.next(), name, description);
        categoryStore.put(category.getId(), category);
    }

    private void seedNews() {
        addNews("Запущена новая версия городского портала",
                "Портал стал быстрее и получил удобный поиск по новостям.",
                "Городской портал обновился: ускорена загрузка страниц, появился умный поиск "
                        + "и раздел с персональными подборками новостей.",
                "Москва", 1L, new String[]{"технологии", "портал"}, 42, daysAgo(0));

        addNews("Открытие новой станции метро",
                "Новая станция свяжет центр города с жилым районом.",
                "Сегодня открылась новая станция метро. Пассажирам доступны современные "
                        + "вестибюли, лифты и удобные пересадки на наземный транспорт.",
                "Москва", 2L, new String[]{"метро", "транспорт"}, 35, daysAgo(1));

        addNews("Чемпионат города по плаванию",
                "Определены победители в пяти дистанциях.",
                "На городском чемпионате по плаванию спортсмены установили три новых рекорда. "
                        + "Соревнования собрали более двухсот участников.",
                "Санкт-Петербург", 3L, new String[]{"спорт", "плавание"}, 18, daysAgo(3));

        addNews("Городской фестиваль науки",
                "Школьники и студенты представили свои исследования.",
                "На фестивале науки работали интерактивные площадки по робототехнике, "
                        + "химии и программированию. Лучшие проекты получили гранты.",
                "Казань", 4L, new String[]{"наука", "фестиваль"}, 27, daysAgo(5));

        addNews("Итоги экономического форума",
                "Обсудили поддержку малого бизнеса и инвестиции.",
                "На форуме представили программу льготного кредитования малого бизнеса "
                        + "и новые меры поддержки экспорта.",
                "Москва", 5L, new String[]{"экономика", "форум"}, 12, daysAgo(10));

        addNews("Ретроспектива городской архитектуры",
                "Выставка рассказывает, как менялся облик города.",
                "В музее открылась выставка архивных фотографий, макетов и чертежей, "
                        + "посвящённая истории городской архитектуры XX века.",
                "Москва", 6L, new String[]{"культура", "архитектура"}, 9, daysAgo(30));
    }

    private void addNews(String title, String annotation, String text, String region,
                         Long categoryId, String[] tags, int rating, Date publishedAt) {
        NewsBO news = new NewsBO(newsIds.next(), title, text);
        news.setAnnotation(annotation);
        news.setRegion(region);
        news.setCategory(categoryStore.find(categoryId));
        news.setAuthor("Редакция");
        news.setPublishedAt(publishedAt);
        news.setTags(new ArrayList<String>(Arrays.asList(tags)));
        news.setRating(rating);
        newsStore.put(news.getId(), news);
    }

    private void seedComments() {
        CommentBO comment = new CommentBO(
                commentIds.next(),
                1L,
                "Иван",
                "Отличная новость, давно ждал обновления портала!",
                daysAgo(0)
        );
        commentStore.put(comment.getId(), comment);
    }
}
