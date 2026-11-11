# Лабораторная работа №3 - бизнес-объекты по UML, паттерн «Фасад», AdminAPI

**Тема:** полная предметная модель из UML-диаграммы классов, `FacadeController`,
поиск по ключевым словам, роль администратора, раздача статики и клиентский
веб-интерфейс.

## Что реализовано по UML

### Иерархия пользователей (`controller/person`, `controller/interfaces`)

| UML | Класс | Ключевые методы |
|-----|-------|-----------------|
| `IAuthorizable` | `interfaces/IAuthorizable` | `login`, `logout`, `isAuthorized` |
| `ICommentable` | `interfaces/ICommentable` | `addComment`, `removeComment`, `getComments` |
| `IPublishable` | `interfaces/IPublishable` | `publish`, `unpublish`, `archive`, `isPublished` |
| `Person` (abstract) | `person/PersonBO` | `getId`, `getName`, `getEmail`, `login` |
| `Guest` | `person/GuestBO` | `register(RegistrationData)` |
| `User` | `person/UserBO` | `addToFavorites`, `removeFromFavorites` |
| `Journalist` | `person/JournalistBO` | `createNews`, `editNews`, `deleteNews` |
| `Admin` | `person/AdminBO` | `blockUser`, `assignRole`, `moderateComment` |

### Сущности контента

| UML | Класс | Связи (из UML) |
|-----|-------|----------------|
| `News` | `news/NewsBO` (реализует `IPublishable`, `ICommentable`) | `News *-- Comment`, `News 0..* -- 1..* Category`, `News 0..* -- 0..* Tag` |
| `NewsStatus` | `news/NewsStatus` (enum) | `DRAFT / PUBLISHED / EDITED / ARCHIVED / DELETED` |
| `Comment` | `comment/CommentBO` | `User 1 -- 0..* Comment` |
| `Category` | `category/CategoryBO` | `Category o-- 0..* News` |
| `Tag` | `tag/TagBO` | - |
| `Favorite` | `favorite/FavoriteBO` | `User 1 -- 0..* Favorite` |
| `Role` | `role/RoleBO` | `User 0..* -- 0..* Role` |

### Жизненный цикл новости (диаграмма состояний)

`DRAFT → PUBLISHED → EDITED → PUBLISHED → DELETED` (+ `ARCHIVED` из диаграммы
деятельности). Реализован через `IPublishable` (`publish`/`unpublish`/`archive`)
и endpoints `/creator/news/{id}/publish`, `/unpublish`, `/archive`.

### Алгоритм создания новости (диаграмма коммуникаций)

Фасад выполняет `validate → generateId → create News → linkCategories → save`
(создание - через `JournalistBO.createNews`, связь категорий/тегов и сохранение -
в фасаде, как «Сервис контента»).

## Архитектура

```
lab3/src/main/java/
├── model/
│   ├── InMemoryStore.java      # хранилище «в памяти»
│   └── IdSequence.java         # генератор id
├── controller/
│   ├── FacadeController.java   # фасад (DTO <-> BO)
│   ├── interfaces/             # IAuthorizable, ICommentable, IPublishable
│   ├── person/                 # PersonBO, GuestBO, UserBO, JournalistBO, AdminBO, RegistrationData
│   ├── news/                   # NewsBO, NewsStatus, NewsData
│   ├── comment/CommentBO.java
│   ├── category/CategoryBO.java
│   ├── tag/TagBO.java
│   ├── favorite/FavoriteBO.java
│   └── role/RoleBO.java
└── view/
    ├── WebAppServer.java       # localhost:8093 + раздача /static
    └── api/
        ├── ReaderAPI.java      # Гость/Пользователь (/reader)
        ├── CreatorAPI.java     # Журналист (/creator)
        ├── AdminAPI.java       # Администратор (/admin)
        ├── StaticPageAPI.java
        ├── SingletonAPI.java
        └── dto/...
```

## API по ролям

* **ReaderAPI** (`/reader`): новости, поиск, категории, регистрация/вход,
  избранное (добавить/убрать/список), комментарии.
* **CreatorAPI** (`/creator`): создание/изменение/удаление новости,
  публикация/снятие/архив, теги, медиа.
* **AdminAPI** (`/admin`): список пользователей и ролей, блокировка,
  назначение роли, модерация комментариев.

## Запуск

```bash
cd lab3
mvn clean package
java -jar target/news-service-lab3.jar
```

* Клиентский интерфейс: **http://localhost:8093/**
* Статика: http://localhost:8093/static/index.html

## Проверка

Все запросы продублированы в `requests.http`. Тестовые учётные записи:

| Роль | Логин | Пароль |
|------|-------|--------|
| Журналист | maria@example.com | pass |
| Пользователь | ivan@example.com | pass |
| Администратор | admin@example.com | admin |
