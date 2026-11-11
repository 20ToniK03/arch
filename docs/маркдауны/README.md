# Документация по проекту «Сервис новостей»

Эта документация описывает устройство проекта `karpushev` - трёх лабораторных
работ, реализующих REST-сервис новостей на **Java 8 + RESTEasy + Undertow**.

Содержание:

1. [Общая архитектура](#1-общая-архитектура)
2. [Как работы связаны между собой](#2-как-работы-связаны-между-собой)
3. [Модуль `view` - представление](#3-модуль-view--представление)
4. [Модуль `controller` - бизнес-логика](#4-модуль-controller--бизнес-логика)
5. [Модуль `model` - данные](#5-модуль-model--данные)
6. [Слой DTO](#6-слой-dto)
7. [Паттерны проектирования](#7-паттерны-проектирования)
8. [Разбор ключевых участков кода](#8-разбор-ключевых-участков-кода)
9. [Справочник API](#9-справочник-api)
10. [Запуск и проверка](#10-запуск-и-проверка)

---

## 1. Общая архитектура

Проект следует трёхслойной схеме **View → Controller → Model**, где каждый слой
знает только о «своём соседе», а не обо всей системе сразу:

```
                HTTP-запрос / ответ (JSON)
                          │
        ┌─────────────────▼──────────────────┐
        │              VIEW                  │
        │  WebAppServer, ReaderAPI,          │
        │  CreatorAPI, SingletonAPI, DTO     │
        └─────────────────┬──────────────────┘
                          │  вызовы фасада, DTO
        ┌─────────────────▼──────────────────┐
        │            CONTROLLER              │
        │  FacadeController, NewsBO,         │
        │  CategoryBO, CommentBO             │
        └─────────────────┬──────────────────┘
                          │  хранение/чтение сущностей
        ┌─────────────────▼──────────────────┐
        │              MODEL                 │
        │  InMemoryStore<T>, IdSequence      │
        └────────────────────────────────────┘
```

Правило, которое соблюдается во всех работах:

> **Слой `view` не трогает данные напрямую и не знает о бизнес-объектах.**
> Он работает только с DTO и фасадом. В свою очередь `controller` не знает
> ничего об HTTP, а `model` вообще не знает ни о BO, ни о DTO.

Это позволяет, например, заменить хранилище с «в памяти» на настоящую БД, не
меняя ни одного контроллера API.

---

## 2. Как работы связаны между собой

Каждая лабораторная - это отдельный Maven-проект, который «вырастает» из
предыдущего:

| Работа | Что добавляется | Ключевое изменение |
|--------|-----------------|--------------------|
| `lab1` | `view` + текстовые заглушки | появляется REST API и Singleton |
| `lab2` | `view/api/dto` + Jackson | ответы становятся JSON-объектами |
| `lab3` | `controller` + `model` | бизнес-объекты, фасад, статика, UI |

Один и тот же набор адресов сохраняется, поэтому запросы из `lab1/requests.http`
продолжают работать в `lab2` и `lab3` (с точностью до порта и типа ответа).

| Работа | Порт | Базовый адрес |
|--------|------|----------------|
| `lab1` | 8091 | http://localhost:8091 |
| `lab2` | 8092 | http://localhost:8092 |
| `lab3` | 8093 | http://localhost:8093 |

---

## 3. Модуль `view` - представление

Отвечает за приём HTTP-запросов, вызов бизнес-логики и формирование ответа.

### 3.1 Структура

| Класс / пакет | Назначение |
|---------------|------------|
| `view.WebAppServer` | точка входа: поднимает Undertow и деплоит JAX-RS-приложение |
| `view.api.SingletonAPI` | JAX-RS `Application`; регистрирует API, Jackson, CORS |
| `view.api.ReaderAPI` | методы читателя/гостя/пользователя (`/reader`) |
| `view.api.CreatorAPI` | методы журналиста (`/creator`) |
| `view.api.AdminAPI` | методы администратора (`/admin`) |
| `view.api.StaticPageAPI` | отдаёт главную HTML-страницу (только `lab3`) |
| `view.api.dto.*` | классы данных для обмена с клиентом |

### 3.2 `WebAppServer`

Сервер создаётся в два шага: сначала настраивается Undertow, затем в него
разворачивается приложение.

```java
UndertowJaxrsServer webServer = new UndertowJaxrsServer();
webServer.start(Undertow.builder().addHttpListener(PORT, HOST));
webServer.deploy(SingletonAPI.class);
```

В `lab3` перед стартом добавляется раздача статики из каталога ресурсов
`static/`:

```java
ResourceHandler staticFiles = new ResourceHandler(
        new ClassPathResourceManager(WebAppServer.class.getClassLoader(), "static/"));
webServer.addResourcePrefixPath("/static", staticFiles);
```

Благодаря этому файл `src/main/resources/static/index.html` доступен по адресу
`/static/index.html`, а главная страница - по `/` (её отдаёт `StaticPageAPI`).

### 3.3 `SingletonAPI`

Класс наследует `javax.ws.rs.core.Application` и переопределяет
`getSingletons()`. Именно этот метод сообщает RESTEasy: «создай эти объекты один
раз и переиспользуй для всех запросов» - это и есть паттерн «Одиночка» на
уровне JAX-RS.

```java
public class SingletonAPI extends Application {
    private final Set<Object> singletons = new LinkedHashSet<Object>();

    public SingletonAPI() {
        singletons.add(new ReaderAPI());
        singletons.add(new CreatorAPI());
        singletons.add(new JacksonJsonProvider());   // DTO <-> JSON (lab2, lab3)
        // ... CORS-фильтр ...
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
```

> Важно: если бы использовался `getClasses()`, контейнер создавал бы новый
> экземпляр контроллера на каждый запрос. В `lab3` это критично, потому что
> `ReaderAPI` и `CreatorAPI` должны видеть **одни и те же данные**.

### 3.4 API-контроллеры

`ReaderAPI` и `CreatorAPI` - это тонкие контроллеры. В `lab3` они не содержат
логики, а лишь:

1. проверяют входные параметры;
2. вызывают метод `FacadeController`;
3. упаковывают результат в HTTP-ответ (200, 201, 400, 404).

Пример (фрагмент `ReaderAPI`):

```java
@GET
@Path("/news/{id}")
public Response getNewsById(@PathParam("id") Long id) {
    if (id == null || id <= 0) {
        return error(Response.Status.BAD_REQUEST, "Идентификатор должен быть положительным");
    }
    NewsItemDTO news = facade.getNewsById(id);
    if (news == null) {
        return error(Response.Status.NOT_FOUND, "Новость с id = " + id + " не найдена");
    }
    return Response.ok(news).build();
}
```

---

## 4. Модуль `controller` - бизнес-логика

Здесь живут бизнес-объекты и фасад, который связывает их с DTO.

### 4.1 Бизнес-объекты (BO)

BO - это классы предметной области из UML-диаграммы классов. В отличие от DTO,
они содержат не только данные, но и поведение.

| Класс | Соответствие UML | Поля / поведение |
|-------|------------------|------------------|
| `interfaces.IAuthorizable` | `IAuthorizable` | `login()`, `logout()`, `isAuthorized()` |
| `interfaces.ICommentable` | `ICommentable` | `addComment()`, `removeComment()`, `getComments()` |
| `interfaces.IPublishable` | `IPublishable` | `publish()`, `unpublish()`, `archive()`, `isPublished()` |
| `person.PersonBO` (abstract) | `Person` | `id`, `name`, `email`, `passwordHash` |
| `person.GuestBO` | `Guest` | `register(RegistrationData)` |
| `person.UserBO` | `User` | `addToFavorites()`, `removeFromFavorites()`, роли, избранное |
| `person.JournalistBO` | `Journalist` | `createNews()`, `editNews()`, `deleteNews()`, `rating` |
| `person.AdminBO` | `Admin` | `blockUser()`, `assignRole()`, `moderateComment()` |
| `news.NewsBO` | `News` | реализует `IPublishable`, `ICommentable`; статус `NewsStatus`, категории, теги, комментарии |
| `news.NewsStatus` | - (состояния) | `DRAFT / PUBLISHED / EDITED / ARCHIVED / DELETED` |
| `comment.CommentBO` | `Comment` | `edit()`, автор, `approved` |
| `category.CategoryBO` | `Category` | `matchesKeyword()` |
| `tag.TagBO` | `Tag` | - |
| `favorite.FavoriteBO` | `Favorite` | `addedAt`, ссылка на новость |
| `role.RoleBO` | `Role` | `permissions` |

Пример бизнес-метода `NewsBO.matchesKeyword`:

```java
public boolean matchesKeyword(String keyword) {
    String needle = keyword.toLowerCase(Locale.ROOT);
    if (containsIgnoreCase(title, needle)
            || containsIgnoreCase(annotation, needle)
            || containsIgnoreCase(text, needle)) {
        return true;
    }
    for (TagBO tag : tags) {
        if (containsIgnoreCase(tag.getName(), needle)) {
            return true;
        }
    }
    return false;
}
```

### 4.2 `FacadeController`

Фасад - единственная точка, через которую слой `view` общается с предметной
областью. Он:

- скрывает BO и хранилище от API;
- принимает DTO запросов, выполняет операции и возвращает DTO ответов;
- преобразует BO в DTO (методы `toNewsItemDTO`, `toNewsTitleDTO`, `toCategoryDTO`,
  `toCommentDTO`).

Фасад тоже реализован как «Одиночка» - оба API-контроллера получают один и тот
же экземпляр через `getInstance()`:

```java
public final class FacadeController {
    private static final FacadeController INSTANCE = new FacadeController();

    private FacadeController() { /* заполнение тестовыми данными */ }

    public static FacadeController getInstance() {
        return INSTANCE;
    }
}
```

#### Алгоритм поиска по ключевым словам

`searchNews(List<String> keywords)` объединяет два источника совпадений:

1. текст новости - заголовок, аннотация, текст, теги (`NewsBO.matchesKeyword`);
2. категория новости - название и описание (`CategoryBO.matchesKeyword`).

```java
Set<NewsBO> found = new LinkedHashSet<NewsBO>();   // исключает дубликаты
for (NewsBO news : newsStore.findAll()) {
    for (String keyword : keywords) {
        if (news.matchesKeyword(keyword)
                || (news.getCategory() != null && news.getCategory().matchesKeyword(keyword))) {
            found.add(news);
            break;
        }
    }
}
return toTitleList(new ArrayList<NewsBO>(found));
```

`LinkedHashSet` гарантирует, что новость, найденная и по тексту, и по категории,
попадёт в ответ ровно один раз.

---

## 5. Модуль `model` - данные

В `lab1` и `lab2` модуль `model` - это заготовка (`package-info.java`). В `lab3`
он наполняется двумя классами общего назначения.

| Класс | Назначение |
|-------|------------|
| `model.InMemoryStore<T>` | хранилище «в памяти», имитирующее БД |
| `model.IdSequence` | генератор уникальных идентификаторов |

`InMemoryStore` - обобщённый класс (`<T>`), который **ничего не знает** о
новостях или категориях. Поэтому модель не зависит ни от контроллера, ни от
представления:

```java
public class InMemoryStore<T> {
    private final Map<Long, T> data = new LinkedHashMap<Long, T>();

    public synchronized void put(Long id, T item) { ... }
    public synchronized T find(Long id) { ... }
    public synchronized List<T> findAll() { ... }
    public synchronized boolean remove(Long id) { ... }
}
```

Все методы синхронизированы: Undertow обрабатывает запросы параллельно, и к
хранилищу могут обращаться несколько потоков одновременно.

---

## 6. Слой DTO

DTO (`Data Transfer Object`) - плоские классы без логики, которыми сервер
обменивается с клиентом. Они находятся в `view/api/dto`.

### 6.1 Каталог DTO

| DTO | Тип | Назначение |
|-----|-----|------------|
| `NewsItemDTO` | ответ | полная новость (текст, теги, медиа, рейтинг, `status`) |
| `NewsTitleDTO` | ответ | элемент списка (заголовок, категория, дата) |
| `NewsListDTO` | ответ | обёртка списка: `items` + `total` |
| `CategoryDTO` | ответ | категория |
| `CategoryListDTO` | ответ | список категорий: `categories` + `total` |
| `CommentDTO` | ответ | комментарий |
| `ActionStatusDTO` | ответ | результат действия: `status` + `message` |
| `UserDTO` | ответ | пользователь (id, имя, email, заблокирован, роли) |
| `UserListDTO` | ответ | список пользователей: `users` + `total` |
| `RoleDTO` | ответ | роль (id, имя, permissions) |
| `RoleListDTO` | ответ | список ролей: `roles` + `total` |
| `CreateNewsRequestDTO` | запрос | создание новости |
| `UpdateNewsRequestDTO` | запрос | изменение новости |
| `CreateCommentRequestDTO` | запрос | добавление комментария |
| `UpdateCommentRequestDTO` | запрос | изменение комментария |

### 6.2 Почему ответы обёрнуты

Списки возвращаются не «голым» массивом `[...]`, а объектом:

```json
{ "items": [ { "id": 1, "title": "...", "category": "...", "publishedAt": "..." } ],
  "total": 3 }
```

Это позволяет добавить пагинацию, не ломая клиентов.

### 6.3 Чем DTO отличается от BO

| Критерий | BO | DTO |
|----------|----|-----|
| Где находится | `controller` | `view/api/dto` |
| Содержит логику | да (`matchesKeyword`, `addTag`) | нет, только поля |
| Кто видит | только сервер | клиент (через JSON) |
| Зависимости | `CategoryBO` внутри `NewsBO` | только строки/числа/списки |

Пример: клиенту не раскрывается внутренняя структура `NewsBO` (объект
`CategoryBO` целиком, служебные данные). В DTO вместо этого передаётся строка
`category` - имя категории:

```java
private NewsItemDTO toNewsItemDTO(NewsBO news) {
    return new NewsItemDTO(
        news.getId(),
        news.getTitle(),
        ...
        news.getCategory() == null ? null : news.getCategory().getName(),  // BO -> строка
        ...
    );
}
```

---

## 7. Паттерны проектирования

| Паттерн | Где применяется | Зачем |
|---------|-----------------|-------|
| **Singleton (Одиночка)** | `SingletonAPI` (уровень JAX-RS), `FacadeController` (уровень логики) | один набор API и одни данные на всё приложение |
| **Facade (Фасад)** | `FacadeController` | прячет BO и хранилище от представления |
| **DTO** | `view/api/dto` | изолирует внутреннюю модель от внешнего контракта |
| **MVC (упрощённо)** | `view` / `controller` / `model` | разделение ответственности по слоям |

---

## 8. Разбор ключевых участков кода

### 8.1 Регистрация Jackson (без него JSON не работает)

RESTEasy 3.15 не находит `MessageBodyWriter` для DTO автоматически. Поэтому
провайдер регистрируется явно в `SingletonAPI`:

```java
singletons.add(new JacksonJsonProvider());
```

Без этой строки возврат DTO из метода приводит к ошибке 500:
`Could not find MessageBodyWriter for response object ... of media type: application/json`.

### 8.2 Формат даты

Даты в BO хранятся как `LocalDateTime`, а в DTO передаются строкой. Форматирование
скрыто в фасаде:

```java
private static final DateTimeFormatter DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

private static String format(LocalDateTime dateTime) {
    return dateTime == null ? null : dateTime.format(DATE_TIME_FORMAT);
}
```

### 8.3 Фильтр «новости за неделю»

```java
LocalDateTime from = LocalDateTime.now().minusDays(7);
for (NewsBO news : newsStore.findAll()) {
    if (news.getPublishedAt() != null && !news.getPublishedAt().isBefore(from)) {
        result.add(news);
    }
}
```

### 8.4 Ошибки - тоже DTO

Ошибочные ответы возвращаются в том же JSON-формате, что и данные, чтобы клиенту
не приходилось разбирать два разных формата:

```json
{ "status": "ERROR", "message": "Новость с id = 999 не найдена" }
```

### 8.5 Клиентский интерфейс (`lab3`)

`static/index.html` - одностраничное приложение на чистом JavaScript (без
внешних библиотек). Оно:

- загружает ленту из `GET /reader/news/all`;
- ищет через `GET /reader/news/search`;
- создаёт новость через `POST /creator/news`;
- добавляет комментарий через `POST /reader/comments`.

Взаимодействие идёт через одну функцию-обёртку:

```js
function api(path, options) {
    var request = options || {};
    request.headers = request.headers || {};
    if (request.body && !request.headers["Content-Type"]) {
        request.headers["Content-Type"] = "application/json; charset=utf-8";
    }
    return fetch(path, request).then(function (response) {
        return response.text().then(function (text) {
            var data = text ? JSON.parse(text) : null;
            if (!response.ok) {
                throw new Error(data && data.message ? data.message : "HTTP " + response.status);
            }
            return data;
        });
    });
}
```

---

## 9. Справочник API

### 9.1 ReaderAPI (`/reader`)

| Метод | Путь | Ответ | Описание |
|-------|------|-------|----------|
| GET | `/news/all` | `NewsListDTO` | все новости |
| GET | `/news/{id}` | `NewsItemDTO` | новость по id |
| GET | `/news/last_week` | `NewsListDTO` | новости за 7 дней |
| GET | `/news/search?keywords=` | `NewsListDTO` | поиск по словам |
| GET | `/news/region?region=` | `NewsListDTO` | новости региона |
| GET | `/categories` | `CategoryListDTO` | категории |
| POST | `/auth/register?name=&email=&password=` | `UserDTO` | регистрация |
| POST | `/auth/login?login=&password=` | `ActionStatusDTO` | вход |
| POST | `/news/{id}/bookmark?userId=` | `ActionStatusDTO` | добавить в избранное |
| DELETE | `/news/{id}/bookmark?userId=` | `ActionStatusDTO` | убрать из избранного |
| GET | `/favorites?userId=` | `NewsListDTO` | избранное пользователя |
| POST | `/comments` | `CommentDTO` | добавить комментарий |
| PUT | `/comments/{id}` | `CommentDTO` | изменить комментарий |
| DELETE | `/comments/{id}` | `ActionStatusDTO` | удалить комментарий |

### 9.2 CreatorAPI (`/creator`)

| Метод | Путь | Ответ | Описание |
|-------|------|-------|----------|
| POST | `/news` | `NewsItemDTO` | создать новость |
| PUT | `/news/{id}` | `NewsItemDTO` | изменить новость |
| DELETE | `/news/{id}` | `ActionStatusDTO` | удалить новость |
| POST | `/news/{id}/publish` | `NewsItemDTO` | опубликовать |
| POST | `/news/{id}/unpublish` | `NewsItemDTO` | снять с публикации |
| POST | `/news/{id}/archive` | `NewsItemDTO` | перевести в архив |
| POST | `/news/{id}/tags?tag=` | `ActionStatusDTO` | добавить тег |
| POST | `/news/{id}/media?url=` | `ActionStatusDTO` | прикрепить медиа |

### 9.3 AdminAPI (`/admin`)

| Метод | Путь | Ответ | Описание |
|-------|------|-------|----------|
| GET | `/users` | `UserListDTO` | пользователи для модерации |
| GET | `/roles` | `RoleListDTO` | роли |
| POST | `/users/{id}/block` | `ActionStatusDTO` | заблокировать пользователя |
| PUT | `/users/{id}/role?role=` | `ActionStatusDTO` | назначить роль |
| POST | `/comments/{id}/moderate?action=` | `ActionStatusDTO` | модерировать комментарий |

### 9.4 Служебные адреса (`lab3`)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/` | главная страница (HTML) |
| GET | `/static/...` | статические файлы |

---

## 10. Запуск и проверка

```bash
# любая из работ:
cd lab3
mvn clean package
java -jar target/news-service-lab3.jar
```

Или в IntelliJ IDEA открыть `src/main/java/view/WebAppServer.java` и нажать ▶.

Проверка в браузере (GET-методы) и через `requests.http` (все методы, включая
POST/PUT/DELETE с JSON-телом).

Клиентский интерфейс третьей работы: **http://localhost:8093/**
