# Лабораторная работа №3 - бизнес-объекты и паттерн «Фасад»

**Тема:** бизнес-объекты (BO), `FacadeController`, поиск по ключевым словам.

Версия **минимум** - строго по тексту задания, без добавлений (без админки,
ролей, авторизации и иерархии пользователей).

## Что реализовано

1. В модуле `controller` описаны бизнес-объекты:
   * `controller.news.NewsBO` - новость (заголовок, аннотация, текст, категория,
     автор, регион, дата, теги, рейтинг) с методом `matchesKeyword`;
   * `controller.category.CategoryBO` - категория с методом `matchesKeyword`;
   * `controller.comment.CommentBO` - комментарий.
2. Описан `controller.FacadeController`:
   * скрывает BO и хранилище от API;
   * принимает DTO запросов и возвращает DTO ответов;
   * реализует поиск новостей по ключевым словам с объединением результатов
     из текста новостей и названий категорий;
   * выполняет CRUD новостей и комментариев.
3. `ReaderAPI` и `CreatorAPI` работают только через фасад.
4. В модуле `model` - хранилище `InMemoryStore<T>` и генератор `IdSequence`.

## Структура

```
lab3/src/main/java/
├── model/
│   ├── InMemoryStore.java
│   └── IdSequence.java
├── controller/
│   ├── FacadeController.java
│   ├── news/NewsBO.java
│   ├── category/CategoryBO.java
│   └── comment/CommentBO.java
└── view/
    ├── WebAppServer.java       # localhost:8093
    └── api/
        ├── ReaderAPI.java      # /reader
        ├── CreatorAPI.java     # /creator
        ├── StaticPageAPI.java  # главная страница
        ├── SingletonAPI.java
        └── dto/...
```

## API

### ReaderAPI (`/reader`)

| Метод | Путь | Ответ |
|-------|------|-------|
| GET | `/news/all` | `NewsListDTO` |
| GET | `/news/{id}` | `NewsItemDTO` |
| GET | `/news/last_week` | `NewsListDTO` |
| GET | `/news/search?keywords=` | `NewsListDTO` |
| GET | `/news/region?region=` | `NewsListDTO` |
| GET | `/categories` | `CategoryListDTO` |
| POST | `/comments` | `CommentDTO` |
| PUT | `/comments/{id}` | `CommentDTO` |
| DELETE | `/comments/{id}` | `ActionStatusDTO` |

### CreatorAPI (`/creator`)

| Метод | Путь | Ответ |
|-------|------|-------|
| POST | `/news` | `NewsItemDTO` |
| PUT | `/news/{id}` | `NewsItemDTO` |
| DELETE | `/news/{id}` | `ActionStatusDTO` |

## Запуск

```bash
cd lab3
mvn clean package
java -jar target/news-service-lab3.jar
```

* Клиентский интерфейс: **http://localhost:8093/**
* Статика: http://localhost:8093/static/index.html

## Проверка

GET-методы открываются в браузере, POST/PUT/DELETE - через `requests.http`
(IntelliJ IDEA) или curl (примеры ниже).

### Тестирование через curl (Windows)

Сервер должен быть запущен. Используй `curl.exe` (в PowerShell голый `curl` -
это алиас на `Invoke-WebRequest`). Кириллицу в параметрах подставляй в
URL-кодировке (метро = `%D0%BC%D0%B5%D1%82%D1%80%D0%BE`).

```powershell
# GET - можно просто открыть в браузере
curl.exe "http://localhost:8093/reader/news/all"
curl.exe "http://localhost:8093/reader/news/1"
curl.exe "http://localhost:8093/reader/news/last_week"
curl.exe "http://localhost:8093/reader/news/search?keywords=%D0%BC%D0%B5%D1%82%D1%80%D0%BE"
curl.exe "http://localhost:8093/reader/news/region?region=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0"
curl.exe "http://localhost:8093/reader/categories"

# Комментарии
curl.exe -X POST "http://localhost:8093/reader/comments" -H "Content-Type: application/json" -d '{"newsId":1,"author":"Ivan","text":"Nice!"}'
curl.exe -X PUT  "http://localhost:8093/reader/comments/1" -H "Content-Type: application/json" -d '{"text":"Fixed"}'
curl.exe -X DELETE "http://localhost:8093/reader/comments/1"

# Новости (создатель)
curl.exe -X POST "http://localhost:8093/creator/news" -H "Content-Type: application/json" -d '{"title":"Test","text":"Body","categoryId":1}'
curl.exe -X PUT    "http://localhost:8093/creator/news/1" -H "Content-Type: application/json" -d '{"title":"New title"}'
curl.exe -X DELETE "http://localhost:8093/creator/news/1"
```

> В **cmd** одинарные кавычки для JSON не работают - используй двойные с
> экранированием: `curl.exe -X POST ... -d "{\"title\":\"Test\",\"categoryId\":1}"`.
