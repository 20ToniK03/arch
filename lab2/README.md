# Лабораторная работа №2 - DTO и JSON-ответы

**Тема:** шаблон DTO (Data Transfer Object). Замена текстовых ответов на JSON-структуры.

## Что сделано

1. В каталоге `view/api` создан пакет `dto`.
2. Для каждого метода API описаны DTO-классы с полями:
   * `NewsItemDTO` - полная новость (id, заголовок, аннотация, текст, категория, автор, дата, регион, теги, медиа, рейтинг);
   * `NewsTitleDTO` - элемент списка новостей;
   * `NewsListDTO` - обёртка списка (`items` + `total`);
   * `CategoryDTO`, `CategoryListDTO` - категория и список категорий;
   * `CommentDTO` - комментарий;
   * `ActionStatusDTO` - результат действия (создание/удаление/закладка и т.п.);
   * `UserDTO`, `UserListDTO` - пользователь и список (для админа);
   * `RoleDTO`, `RoleListDTO` - роль и список ролей.
3. Добавлены DTO запросов: `CreateNewsRequestDTO`, `UpdateNewsRequestDTO`,
   `CreateCommentRequestDTO`, `UpdateCommentRequestDTO`, `ReplyCommentRequestDTO`.
4. Добавлен `AdminAPI` (`/admin`) - администратор из UML (блокировка, роли, модерация).
4. В каждый метод вместо текста возвращается экземпляр DTO с тестовыми данными.
5. Подключён `resteasy-jackson2-provider`; в `SingletonAPI` зарегистрирован
   `JacksonJsonProvider`, поэтому ответы автоматически сериализуются в JSON.

> Важно: провайдер Jackson зарегистрирован явно. Если этого не сделать,
> RESTEasy не найдёт `MessageBodyWriter` для DTO и вернёт ошибку 500.

## Структура

```
lab2/
├── pom.xml
├── requests.http
└── src/main/java/
    ├── controller/package-info.java
    ├── model/package-info.java
    └── view/
        ├── WebAppServer.java            # localhost:8092
        └── api/
            ├── ReaderAPI.java           # GET + комментарии
            ├── CreatorAPI.java          # создание/изменение/удаление
            ├── AdminAPI.java            # администрирование (/admin)
            ├── SingletonAPI.java        # Application + Jackson + CORS
            └── dto/
                ├── NewsItemDTO.java
                ├── NewsTitleDTO.java
                ├── NewsListDTO.java
                ├── CategoryDTO.java
                ├── CategoryListDTO.java
                ├── CommentDTO.java
                ├── ActionStatusDTO.java
                ├── UserDTO.java
                ├── UserListDTO.java
                ├── RoleDTO.java
                ├── RoleListDTO.java
                ├── CreateNewsRequestDTO.java
                ├── UpdateNewsRequestDTO.java
                ├── CreateCommentRequestDTO.java
                ├── UpdateCommentRequestDTO.java
                └── ReplyCommentRequestDTO.java
```

## Запуск

```bash
cd lab2
mvn clean package
java -jar target/news-service-lab2.jar
```

Или запустить `view/WebAppServer.java` в IntelliJ IDEA. Сервер слушает **http://localhost:8092**.

## Пример ответа

`GET /reader/news/1`:

```json
{
  "id": 1,
  "title": "Запущена новая версия городского портала",
  "annotation": "Портал стал быстрее и получил удобный поиск по новостям.",
  "text": "Городской портал обновился: ...",
  "category": "Технологии",
  "author": "Редакция",
  "publishedAt": "2026-11-09 10:00",
  "region": "Москва",
  "tags": ["технологии", "портал", "обновление"],
  "media": [],
  "rating": 42
}
```

## Проверка

* GET-методы открываются в браузере (см. `README` и `requests.http`).
* POST/PUT/DELETE проверяются через `requests.http` в IntelliJ IDEA.
