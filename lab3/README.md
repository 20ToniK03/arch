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

## Тестирование через curl (Windows)

Сервер должен быть запущен: `java -jar target/news-service-lab3.jar`.

В PowerShell и cmd используй именно **`curl.exe`** (в PowerShell голый `curl` — это
алиас на `Invoke-WebRequest`, он не подходит). Кириллицу в параметрах подставляй
в URL-кодировке (метро = `%D0%BC%D0%B5%D1%82%D1%80%D0%BE`, Москва =
`%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0`).

### Читатель — GET (можно просто открыть в браузере)

```powershell
curl.exe "http://localhost:8093/reader/news/all"
curl.exe "http://localhost:8093/reader/news/1"
curl.exe "http://localhost:8093/reader/news/last_week"
curl.exe "http://localhost:8093/reader/news/search?keywords=%D0%BC%D0%B5%D1%82%D1%80%D0%BE"
curl.exe "http://localhost:8093/reader/news/region?region=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0"
curl.exe "http://localhost:8093/reader/categories"
curl.exe "http://localhost:8093/reader/favorites?userId=2"
```

### Регистрация и вход — POST

```powershell
curl.exe -X POST "http://localhost:8093/reader/auth/register?name=Petr&email=petr@example.com&password=pass"
curl.exe -X POST "http://localhost:8093/reader/auth/login?login=maria@example.com&password=pass"
curl.exe -X POST "http://localhost:8093/reader/auth/login?login=maria@example.com&password=wrong"
```

### Избранное — POST / DELETE

```powershell
curl.exe -X POST   "http://localhost:8093/reader/news/1/bookmark?userId=2"
curl.exe -X DELETE "http://localhost:8093/reader/news/1/bookmark?userId=2"
```

### Комментарии — POST / PUT / DELETE

```powershell
curl.exe -X POST "http://localhost:8093/reader/comments" -H "Content-Type: application/json" -d '{"newsId":1,"author":"Ivan","text":"Nice!"}'
curl.exe -X PUT  "http://localhost:8093/reader/comments/1" -H "Content-Type: application/json" -d '{"text":"Fixed"}'
curl.exe -X DELETE "http://localhost:8093/reader/comments/1"
```

### Журналист — новости

```powershell
curl.exe -X POST "http://localhost:8093/creator/news" -H "Content-Type: application/json" -d '{"title":"Test","text":"Body","categoryId":1,"region":"Moscow","tags":["test"]}'
curl.exe -X PUT    "http://localhost:8093/creator/news/1" -H "Content-Type: application/json" -d '{"title":"New title"}'
curl.exe -X DELETE "http://localhost:8093/creator/news/1"
curl.exe -X POST "http://localhost:8093/creator/news/7/publish"
curl.exe -X POST "http://localhost:8093/creator/news/7/unpublish"
curl.exe -X POST "http://localhost:8093/creator/news/6/archive"
curl.exe -X POST "http://localhost:8093/creator/news/1/tags?tag=java"
curl.exe -X POST "http://localhost:8093/creator/news/1/media?url=https://example.com/a.jpg"
```

### Администратор

```powershell
curl.exe "http://localhost:8093/admin/users"
curl.exe "http://localhost:8093/admin/roles"
curl.exe -X POST "http://localhost:8093/admin/users/2/block"
curl.exe -X PUT  "http://localhost:8093/admin/users/2/role?role=ADMIN"
curl.exe -X POST "http://localhost:8093/admin/comments/1/moderate?action=approve"
```

> В **cmd** одинарные кавычки для JSON не работают — используй двойные с
> экранированием: `curl.exe -X POST ... -d "{\"title\":\"Test\",\"categoryId\":1}"`.
