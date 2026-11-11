# Лабораторная работа №1 - REST-сервис на RESTEasy + Undertow

**Тема:** паттерн «Одиночка» (Singleton) и описание REST API.

## Что сделано

1. Создан Maven-проект с тремя модулями-пакетами: `view`, `model`, `controller`.
2. Подключены зависимости RESTEasy (`resteasy-jaxrs`, `resteasy-undertow`).
3. В пакете `view.api` описаны:
   * `ReaderAPI` - методы читателя (`@Path("/reader")`);
   * `CreatorAPI` - методы создателя/редактора (`@Path("/creator")`);
   * `AdminAPI` - методы администратора (`@Path("/admin")`);
   * `SingletonAPI` - JAX-RS `Application`, который отдаёт **ровно по одному экземпляру**
     каждого API (паттерн «Одиночка»).
4. В `view.WebAppServer` поднят встроенный веб-сервер Undertow и развёрнуто приложение.
5. Все методы возвращают текстовые заглушки - по заданию на этом этапе бизнес-логики ещё нет.

## Структура

```
lab1/
├── pom.xml
├── requests.http                  # готовые запросы для проверки API
└── src/main/java/
    ├── controller/package-info.java   # заготовка модуля контроллера
    ├── model/package-info.java        # заготовка модуля модели
    └── view/
        ├── WebAppServer.java          # запуск Undertow (localhost:8091)
        └── api/
            ├── ReaderAPI.java
            ├── CreatorAPI.java
            ├── AdminAPI.java
            └── SingletonAPI.java      # Application + Singleton
```

## Запуск

```bash
cd lab1
mvn clean package
java -jar target/news-service-lab1.jar
```

Либо в IntelliJ IDEA: открыть `src/main/java/view/WebAppServer.java` и нажать ▶ у `main`.

Сервер слушает **http://localhost:8091**.

## Проверка в браузере (GET)

| Метод | URL |
|-------|-----|
| Все новости | http://localhost:8091/reader/news/all |
| Новость по id | http://localhost:8091/reader/news/1 |
| Новости за неделю | http://localhost:8091/reader/news/last_week |
| Поиск | http://localhost:8091/reader/news/search?keywords=java |
| Новости региона | http://localhost:8091/reader/news/region?region=Москва |
| Категории | http://localhost:8091/reader/categories |

Методы `POST`/`PUT`/`DELETE` проверяются через `requests.http` (IntelliJ IDEA показывает
кнопку запуска рядом с каждым запросом) или через curl.

## Пояснение по Singleton

`SingletonAPI` наследует `javax.ws.rs.core.Application` и переопределяет
`getSingletons()`. Именно этот метод сообщает RESTEasy: «вот готовые объекты,
создай их один раз и переиспользуй». Если бы мы вернули классы через
`getClasses()`, контейнер создавал бы новый экземпляр контроллера на каждый запрос.
