package controller;

import controller.category.CategoryBO;
import controller.comment.CommentBO;
import controller.favorite.FavoriteBO;
import controller.news.NewsBO;
import controller.news.NewsData;
import controller.news.NewsStatus;
import controller.person.AdminBO;
import controller.person.GuestBO;
import controller.person.JournalistBO;
import controller.person.RegistrationData;
import controller.person.UserBO;
import controller.role.RoleBO;
import controller.tag.TagBO;
import model.IdSequence;
import model.InMemoryStore;
import view.api.dto.CategoryDTO;
import view.api.dto.CommentDTO;
import view.api.dto.CreateCommentRequestDTO;
import view.api.dto.CreateNewsRequestDTO;
import view.api.dto.NewsItemDTO;
import view.api.dto.NewsTitleDTO;
import view.api.dto.RoleDTO;
import view.api.dto.UpdateCommentRequestDTO;
import view.api.dto.UpdateNewsRequestDTO;
import view.api.dto.UserDTO;

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
 * <p>Соответствует диаграмме классов UML: скрывает бизнес-объекты
 * ({@link NewsBO}, {@link CategoryBO}, {@link CommentBO}, {@link UserBO},
 * {@link JournalistBO}, {@link AdminBO}, {@link TagBO}, {@link RoleBO})
 * и хранилища за единым интерфейсом. Принимает DTO запросов и возвращает
 * DTO ответов.</p>
 *
 * <p>Роль «Сервиса контента» из диаграммы коммуникаций выполняет именно фасад:
 * {@code validate -> generateId -> create News -> linkCategories -> save}.</p>
 */
public final class FacadeController {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final long DAY_MILLIS = 24L * 60L * 60L * 1000L;

    private static final FacadeController INSTANCE = new FacadeController();

    private final InMemoryStore<NewsBO> newsStore = new InMemoryStore<NewsBO>();
    private final InMemoryStore<CategoryBO> categoryStore = new InMemoryStore<CategoryBO>();
    private final InMemoryStore<TagBO> tagStore = new InMemoryStore<TagBO>();
    private final InMemoryStore<UserBO> userStore = new InMemoryStore<UserBO>();
    private final InMemoryStore<RoleBO> roleStore = new InMemoryStore<RoleBO>();

    private final IdSequence newsIds = new IdSequence();
    private final IdSequence categoryIds = new IdSequence();
    private final IdSequence tagIds = new IdSequence();
    private final IdSequence userIds = new IdSequence();
    private final IdSequence roleIds = new IdSequence();
    private final IdSequence commentIds = new IdSequence();

    private final GuestBO guest = new GuestBO();
    private JournalistBO journalist;
    private AdminBO admin;

    private FacadeController() {
        seedRoles();
        seedUsers();
        seedCategories();
        seedTags();
        seedNews();
        seedComments();
    }

    public static FacadeController getInstance() {
        return INSTANCE;
    }

    // Чтение (актёры «Гость» и «Пользователь»)

    /** Все опубликованные новости (сначала свежие). */
    public synchronized List<NewsTitleDTO> getAllNews() {
        List<NewsBO> visible = new ArrayList<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            if (isVisible(news)) {
                visible.add(news);
            }
        }
        return toTitleList(visible);
    }

    /** Новость по id (только опубликованная). */
    public synchronized NewsItemDTO getNewsById(long id) {
        NewsBO news = newsStore.find(id);
        if (news == null || !isVisible(news)) {
            return null;
        }
        return toNewsItemDTO(news);
    }

    /** Новости за последние 7 дней. */
    public synchronized List<NewsTitleDTO> getLastWeekNews() {
        long from = System.currentTimeMillis() - 7 * DAY_MILLIS;
        List<NewsBO> result = new ArrayList<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            if (isVisible(news) && news.getCreatedAt() != null
                    && news.getCreatedAt().getTime() >= from) {
                result.add(news);
            }
        }
        return toTitleList(result);
    }

    /** Поиск с объединением результатов по тексту новости и названию категории. */
    public synchronized List<NewsTitleDTO> searchNews(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<NewsTitleDTO>();
        }
        Set<NewsBO> found = new LinkedHashSet<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            if (!isVisible(news)) {
                continue;
            }
            for (String keyword : keywords) {
                if (news.matchesKeyword(keyword) || categoryMatches(news, keyword)) {
                    found.add(news);
                    break;
                }
            }
        }
        return toTitleList(new ArrayList<NewsBO>(found));
    }

    /** Новости региона. */
    public synchronized List<NewsTitleDTO> getNewsByRegion(String region) {
        if (region == null) {
            return new ArrayList<NewsTitleDTO>();
        }
        String needle = region.trim();
        List<NewsBO> result = new ArrayList<NewsBO>();
        for (NewsBO news : newsStore.findAll()) {
            if (isVisible(news) && news.getRegion() != null
                    && news.getRegion().equalsIgnoreCase(needle)) {
                result.add(news);
            }
        }
        return toTitleList(result);
    }

    /** Все категории. */
    public synchronized List<CategoryDTO> getCategories() {
        List<CategoryDTO> result = new ArrayList<CategoryDTO>();
        for (CategoryBO category : categoryStore.findAll()) {
            result.add(toCategoryDTO(category));
        }
        return result;
    }

    // Авторизация (use case «Регистрация / Авторизация», IAuthorizable)

    /** Регистрация пользователя (метод {@code Guest.register}). */
    public synchronized UserDTO register(RegistrationData data) {
        if (data == null || isBlank(data.getName()) || isBlank(data.getEmail())) {
            return null;
        }
        if (findUserByEmail(data.getEmail()) != null) {
            return null;
        }
        UserBO user = guest.register(data);
        user.setId(userIds.next());
        RoleBO role = roleStore.find(2L); // USER
        if (role != null) {
            user.addRole(role);
        }
        userStore.put(user.getId(), user);
        return toUserDTO(user);
    }

    /** Вход по логину (email) и паролю (метод {@code IAuthorizable.login}). */
    public synchronized boolean login(String login, String password) {
        UserBO user = findUserByEmail(login);
        return user != null && user.login(login, password);
    }

    // Избранное (use case «Избранное»)

    public synchronized boolean addBookmark(long userId, long newsId) {
        UserBO user = userStore.find(userId);
        NewsBO news = newsStore.find(newsId);
        if (user == null || news == null) {
            return false;
        }
        user.addToFavorites(news);
        return true;
    }

    public synchronized boolean removeBookmark(long userId, long newsId) {
        UserBO user = userStore.find(userId);
        NewsBO news = newsStore.find(newsId);
        if (user == null || news == null) {
            return false;
        }
        user.removeFromFavorites(news);
        return true;
    }

    /** Избранное пользователя (use case «Избранное»). */
    public synchronized List<NewsTitleDTO> getFavorites(long userId) {
        UserBO user = userStore.find(userId);
        if (user == null) {
            return new ArrayList<NewsTitleDTO>();
        }
        List<NewsBO> result = new ArrayList<NewsBO>();
        for (FavoriteBO favorite : user.getFavorites()) {
            if (favorite.getNews() != null && isVisible(favorite.getNews())) {
                result.add(favorite.getNews());
            }
        }
        return toTitleList(result);
    }

    // Комментарии (use case «Комментировать», ICommentable)

    public synchronized CommentDTO createComment(CreateCommentRequestDTO request) {
        if (request == null || request.getNewsId() == null || isBlank(request.getText())) {
            return null;
        }
        NewsBO news = newsStore.find(request.getNewsId());
        if (news == null || !isVisible(news)) {
            return null;
        }
        CommentBO comment = new CommentBO(
                commentIds.next(),
                request.getText().trim(),
                new Date(),
                resolveAuthor(request.getAuthor())
        );
        news.addComment(comment);
        return toCommentDTO(comment);
    }

    public synchronized CommentDTO updateComment(long id, UpdateCommentRequestDTO request) {
        if (request == null || isBlank(request.getText())) {
            return null;
        }
        CommentBO comment = findComment(id);
        if (comment == null) {
            return null;
        }
        comment.edit(request.getText().trim());
        return toCommentDTO(comment);
    }

    public synchronized boolean deleteComment(long id) {
        for (NewsBO news : newsStore.findAll()) {
            if (hasComment(news, id)) {
                news.removeComment(id);
                return true;
            }
        }
        return false;
    }

    // Контент (актёр «Журналист»)

    /** Создать новость (алгоритм из диаграммы коммуникаций). */
    public synchronized NewsItemDTO createNews(CreateNewsRequestDTO request) {
        if (request == null || isBlank(request.getTitle())) {
            return null;
        }
        CategoryBO category = request.getCategoryId() == null
                ? null : categoryStore.find(request.getCategoryId());
        if (category == null) {
            return null;
        }

        // validate -> create (Journalist.createNews)
        NewsData data = new NewsData(
                request.getTitle().trim(),
                trimToNull(request.getText()),
                trimToNull(request.getAnnotation()),
                isBlank(request.getRegion()) ? "Москва" : request.getRegion().trim(),
                Arrays.asList(category.getId()),
                request.getTags() == null ? new ArrayList<String>() : request.getTags()
        );
        NewsBO news = journalist.createNews(data);

        // generateId -> link categories/tags -> save
        news.setId(newsIds.next());
        news.addCategory(category);
        linkTags(news, data.getTagNames());
        news.setRating(0);
        news.publish(); // ветвь «Опубликовать сразу» диаграммы деятельности
        newsStore.put(news.getId(), news);
        return toNewsItemDTO(news);
    }

    /** Изменить новость (Journalist.editNews). */
    public synchronized NewsItemDTO updateNews(long id, UpdateNewsRequestDTO request) {
        NewsBO news = newsStore.find(id);
        if (news == null || request == null) {
            return null;
        }
        NewsData data = new NewsData(
                request.getTitle() == null ? news.getTitle() : request.getTitle(),
                request.getText() == null ? news.getText() : request.getText(),
                request.getAnnotation() == null ? news.getAnnotation() : request.getAnnotation(),
                request.getRegion() == null ? news.getRegion() : request.getRegion(),
                news.getCategories().isEmpty() ? new ArrayList<Long>()
                        : Arrays.asList(news.getCategories().get(0).getId()),
                new ArrayList<String>()
        );
        if (request.getCategoryId() != null) {
            CategoryBO category = categoryStore.find(request.getCategoryId());
            if (category != null) {
                news.addCategory(category);
            }
        }
        if (request.getTags() != null) {
            linkTags(news, request.getTags());
        }
        journalist.editNews(news, data);
        return toNewsItemDTO(news);
    }

    /** Удалить новость (мягкое удаление - статус DELETED). */
    public synchronized boolean deleteNews(long id) {
        NewsBO news = newsStore.find(id);
        if (news == null) {
            return false;
        }
        journalist.deleteNews(news);
        return true;
    }

    /** Опубликовать новость (IPublishable.publish). */
    public synchronized NewsItemDTO publishNews(long id) {
        NewsBO news = newsStore.find(id);
        if (news == null) {
            return null;
        }
        news.publish();
        return toNewsItemDTO(news);
    }

    /** Снять с публикации (IPublishable.unpublish). */
    public synchronized NewsItemDTO unpublishNews(long id) {
        NewsBO news = newsStore.find(id);
        if (news == null) {
            return null;
        }
        news.unpublish();
        return toNewsItemDTO(news);
    }

    /** Перевести в архив (IPublishable.archive). */
    public synchronized NewsItemDTO archiveNews(long id) {
        NewsBO news = newsStore.find(id);
        if (news == null) {
            return null;
        }
        news.archive();
        return toNewsItemDTO(news);
    }

    public synchronized boolean addTag(long newsId, String tagName) {
        NewsBO news = newsStore.find(newsId);
        if (news == null || isBlank(tagName)) {
            return false;
        }
        news.addTag(findOrCreateTag(tagName.trim()));
        return true;
    }

    public synchronized boolean attachMedia(long newsId, String url) {
        NewsBO news = newsStore.find(newsId);
        if (news == null || isBlank(url)) {
            return false;
        }
        news.addMedia(url.trim());
        return true;
    }

    // Администрирование (актёр «Администратор»)

    public synchronized List<UserDTO> getUsers() {
        List<UserDTO> result = new ArrayList<UserDTO>();
        for (UserBO user : userStore.findAll()) {
            result.add(toUserDTO(user));
        }
        return result;
    }

    public synchronized List<RoleDTO> getRoles() {
        List<RoleDTO> result = new ArrayList<RoleDTO>();
        for (RoleBO role : roleStore.findAll()) {
            result.add(toRoleDTO(role));
        }
        return result;
    }

    public synchronized boolean blockUser(long userId) {
        UserBO user = userStore.find(userId);
        if (user == null) {
            return false;
        }
        admin.blockUser(user);
        return true;
    }

    public synchronized boolean assignRole(long userId, String roleName) {
        UserBO user = userStore.find(userId);
        RoleBO role = findRoleByName(roleName);
        if (user == null || role == null) {
            return false;
        }
        admin.assignRole(user, role);
        return true;
    }

    /** Модерация комментария: approve - одобрить, иначе - отклонить. */
    public synchronized boolean moderateComment(long commentId, String action) {
        CommentBO comment = findComment(commentId);
        if (comment == null) {
            return false;
        }
        if ("approve".equalsIgnoreCase(action)) {
            admin.moderateComment(comment);
        } else if ("reject".equalsIgnoreCase(action)) {
            comment.setApproved(false);
        } else {
            return false;
        }
        return true;
    }

    // Преобразование BO -> DTO

    private List<NewsTitleDTO> toTitleList(List<NewsBO> newsList) {
        List<NewsBO> sorted = new ArrayList<NewsBO>(newsList);
        sorted.sort((first, second) -> {
            long a = first.getCreatedAt() == null ? 0L : first.getCreatedAt().getTime();
            long b = second.getCreatedAt() == null ? 0L : second.getCreatedAt().getTime();
            return Long.compare(b, a);
        });

        List<NewsTitleDTO> result = new ArrayList<NewsTitleDTO>();
        for (NewsBO news : sorted) {
            result.add(toNewsTitleDTO(news));
        }
        return result;
    }

    private NewsTitleDTO toNewsTitleDTO(NewsBO news) {
        return new NewsTitleDTO(news.getId(), news.getTitle(), firstCategoryName(news), format(news.getCreatedAt()));
    }

    private NewsItemDTO toNewsItemDTO(NewsBO news) {
        NewsItemDTO dto = new NewsItemDTO(
                news.getId(),
                news.getTitle(),
                news.getAnnotation(),
                news.getText(),
                firstCategoryName(news),
                news.getAuthor() == null ? null : news.getAuthor().getName(),
                format(news.getCreatedAt()),
                news.getRegion(),
                tagNames(news),
                new ArrayList<String>(news.getMedia()),
                news.getRating()
        );
        dto.setStatus(news.getStatus() == null ? null : news.getStatus().name());
        return dto;
    }

    private CategoryDTO toCategoryDTO(CategoryBO category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }

    private UserDTO toUserDTO(UserBO user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.isBlocked(), roleNames(user));
    }

    private RoleDTO toRoleDTO(RoleBO role) {
        return new RoleDTO(role.getId(), role.getName(), new ArrayList<String>(role.getPermissions()));
    }

    private CommentDTO toCommentDTO(CommentBO comment) {
        NewsBO owner = findNewsOfComment(comment);
        return new CommentDTO(
                comment.getId(),
                owner == null ? null : owner.getId(),
                comment.getAuthor() == null ? null : comment.getAuthor().getName(),
                comment.getText(),
                format(comment.getCreatedAt())
        );
    }

    // Вспомогательные методы

    private boolean isVisible(NewsBO news) {
        return news.getStatus() == NewsStatus.PUBLISHED || news.getStatus() == NewsStatus.EDITED;
    }

    private boolean categoryMatches(NewsBO news, String keyword) {
        for (CategoryBO category : news.getCategories()) {
            if (category.matchesKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String firstCategoryName(NewsBO news) {
        return news.getCategories().isEmpty() ? null : news.getCategories().get(0).getName();
    }

    private List<String> tagNames(NewsBO news) {
        List<String> names = new ArrayList<String>();
        for (TagBO tag : news.getTags()) {
            names.add(tag.getName());
        }
        return names;
    }

    private List<String> roleNames(UserBO user) {
        List<String> names = new ArrayList<String>();
        for (RoleBO role : user.getRoles()) {
            names.add(role.getName());
        }
        return names;
    }

    private void linkTags(NewsBO news, List<String> tagNames) {
        if (tagNames == null) {
            return;
        }
        for (String name : tagNames) {
            if (!isBlank(name)) {
                news.addTag(findOrCreateTag(name.trim()));
            }
        }
    }

    private TagBO findOrCreateTag(String name) {
        for (TagBO tag : tagStore.findAll()) {
            if (tag.getName() != null && tag.getName().equalsIgnoreCase(name)) {
                return tag;
            }
        }
        TagBO tag = new TagBO(tagIds.next(), name);
        tagStore.put(tag.getId(), tag);
        return tag;
    }

    private UserBO findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        for (UserBO user : userStore.findAll()) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    private RoleBO findRoleByName(String name) {
        if (name == null) {
            return null;
        }
        for (RoleBO role : roleStore.findAll()) {
            if (name.equalsIgnoreCase(role.getName())) {
                return role;
            }
        }
        return null;
    }

    private UserBO resolveAuthor(String name) {
        if (name != null) {
            for (UserBO user : userStore.findAll()) {
                if (name.equalsIgnoreCase(user.getName())) {
                    return user;
                }
            }
        }
        return new UserBO(null, isBlank(name) ? "Аноним" : name.trim(), null);
    }

    private CommentBO findComment(long id) {
        for (NewsBO news : newsStore.findAll()) {
            for (CommentBO comment : news.getComments()) {
                if (comment.getId() != null && comment.getId() == id) {
                    return comment;
                }
            }
        }
        return null;
    }

    private NewsBO findNewsOfComment(CommentBO comment) {
        for (NewsBO news : newsStore.findAll()) {
            if (hasComment(news, comment.getId())) {
                return news;
            }
        }
        return null;
    }

    private boolean hasComment(NewsBO news, long id) {
        for (CommentBO comment : news.getComments()) {
            if (comment.getId() != null && comment.getId() == id) {
                return true;
            }
        }
        return false;
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

    private void seedRoles() {
        addRole("GUEST", Arrays.asList("news.view"));
        addRole("USER", Arrays.asList("news.view", "news.comment", "news.favorite"));
        addRole("JOURNALIST", Arrays.asList("news.view", "news.create", "news.edit", "news.delete"));
        addRole("ADMIN", Arrays.asList("user.block", "user.role", "comment.moderate"));
    }

    private void addRole(String name, List<String> permissions) {
        RoleBO role = new RoleBO(roleIds.next(), name, permissions);
        roleStore.put(role.getId(), role);
    }

    private void seedUsers() {
        journalist = new JournalistBO(userIds.next(), "Мария Соколова", "maria@example.com");
        journalist.setPasswordHash("pass");
        journalist.setRating(4.8);
        journalist.addRole(findRoleByName("JOURNALIST"));
        userStore.put(journalist.getId(), journalist);

        UserBO reader = new UserBO(userIds.next(), "Иван Петров", "ivan@example.com");
        reader.setPasswordHash("pass");
        reader.addRole(findRoleByName("USER"));
        userStore.put(reader.getId(), reader);

        admin = new AdminBO(userIds.next(), "Алексей Админов", "admin@example.com");
        admin.setPasswordHash("admin");
        admin.addRole(findRoleByName("ADMIN"));
        userStore.put(admin.getId(), admin);
    }

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

    private void seedTags() {
        for (String name : new String[]{"технологии", "портал", "метро", "транспорт",
                "спорт", "плавание", "наука", "фестиваль", "экономика", "форум",
                "культура", "архитектура"}) {
            TagBO tag = new TagBO(tagIds.next(), name);
            tagStore.put(tag.getId(), tag);
        }
    }

    private void seedNews() {
        addNews("Запущена новая версия городского портала",
                "Портал стал быстрее и получил удобный поиск по новостям.",
                "Городской портал обновился: ускорена загрузка страниц, появился умный поиск "
                        + "и раздел с персональными подборками новостей.",
                "Москва", 1L, new String[]{"технологии", "портал"}, 42, daysAgo(0), true);

        addNews("Открытие новой станции метро",
                "Новая станция свяжет центр города с жилым районом.",
                "Сегодня открылась новая станция метро. Пассажирам доступны современные "
                        + "вестибюли, лифты и удобные пересадки на наземный транспорт.",
                "Москва", 2L, new String[]{"метро", "транспорт"}, 35, daysAgo(1), true);

        addNews("Чемпионат города по плаванию",
                "Определены победители в пяти дистанциях.",
                "На городском чемпионате по плаванию спортсмены установили три новых рекорда. "
                        + "Соревнования собрали более двухсот участников.",
                "Санкт-Петербург", 3L, new String[]{"спорт", "плавание"}, 18, daysAgo(3), true);

        addNews("Городской фестиваль науки",
                "Школьники и студенты представили свои исследования.",
                "На фестивале науки работали интерактивные площадки по робототехнике, "
                        + "химии и программированию. Лучшие проекты получили гранты.",
                "Казань", 4L, new String[]{"наука", "фестиваль"}, 27, daysAgo(5), true);

        addNews("Итоги экономического форума",
                "Обсудили поддержку малого бизнеса и инвестиции.",
                "На форуме представили программу льготного кредитования малого бизнеса "
                        + "и новые меры поддержки экспорта.",
                "Москва", 5L, new String[]{"экономика", "форум"}, 12, daysAgo(10), true);

        addNews("Ретроспектива городской архитектуры",
                "Выставка рассказывает, как менялся облик города.",
                "В музее открылась выставка архивных фотографий, макетов и чертежей, "
                        + "посвящённая истории городской архитектуры XX века.",
                "Москва", 6L, new String[]{"культура", "архитектура"}, 9, daysAgo(30), true);

        // Черновик - демонстрирует состояние Draft и метод publish.
        addNews("Черновик: грядущее обновление портала",
                "Материал в работе.",
                "Этот материал ещё не опубликован и не отображается читателям.",
                "Москва", 1L, new String[]{"технологии"}, 0, daysAgo(0), false);
    }

    private void addNews(String title, String annotation, String text, String region,
                         Long categoryId, String[] tags, int rating, Date createdAt, boolean published) {
        NewsBO news = journalist.createNews(new NewsData(title, text, annotation, region,
                Arrays.asList(categoryId), Arrays.asList(tags)));
        news.setId(newsIds.next());
        news.setCreatedAt(createdAt);
        news.setUpdatedAt(createdAt);
        CategoryBO category = categoryStore.find(categoryId);
        if (category != null) {
            news.addCategory(category);
        }
        linkTags(news, Arrays.asList(tags));
        news.setRating(rating);
        if (published) {
            news.publish();
        } else {
            news.setStatus(NewsStatus.DRAFT);
        }
        newsStore.put(news.getId(), news);
    }

    private void seedComments() {
        NewsBO news = newsStore.find(1L);
        if (news != null) {
            CommentBO comment = new CommentBO(commentIds.next(),
                    "Отличная новость, давно ждал обновления портала!",
                    daysAgo(0), findUserByEmail("ivan@example.com"));
            news.addComment(comment);
        }
    }
}
