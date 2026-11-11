# Сервис новостей - лабораторные работы №1-3

Автор: **Карпушев А. А.** (папка `karpushev`).

Три самостоятельных Maven-проекта, последовательно развивающих один и тот же
сервис новостей: от текстовых заглушек REST API до бизнес-объектов, фасада и
клиентского веб-интерфейса.

| Работа | Папка | Порт | Тема | Ключевые файлы |
|--------|-------|------|------|----------------|
| №1 | [`lab1`](lab1) | 8091 | REST API, паттерн «Одиночка» | `view/api/ReaderAPI.java`, `view/api/CreatorAPI.java`, `view/api/AdminAPI.java`, `view/api/SingletonAPI.java` |
| №2 | [`lab2`](lab2) | 8092 | DTO и JSON-ответы | `view/api/dto/*`, контроллеры (Reader/Creator/Admin), возвращающие DTO |
| №3 | [`lab3`](lab3) | 8093 | Бизнес-объекты по UML, «Фасад», AdminAPI, статика | `controller/**` (Person/User/Journalist/Admin, News, Comment, Category, Tag, Favorite, Role), `controller/FacadeController.java`, `static/index.html` |

## Быстрый запуск

Нужны JDK 8+ и Maven. Проекты собираются в исполняемый jar (maven-shade-plugin).

```bash
cd lab1 && mvn clean package && java -jar target/news-service-lab1.jar
cd lab2 && mvn clean package && java -jar target/news-service-lab2.jar
cd lab3 && mvn clean package && java -jar target/news-service-lab3.jar
```

Либо открыть проект в IntelliJ IDEA и запустить `view/WebAppServer.java`
(в каждом проекте есть свой `main`).

Порты специально разные, поэтому все три лабораторные можно держать запущенными
одновременно:

* ЛР1 - http://localhost:8091/reader/news/all
* ЛР2 - http://localhost:8092/reader/news/all
* ЛР3 - http://localhost:8093/ (клиентский интерфейс)

## Итоговое решение

Собрано собственное решение, которое берёт лучшее из всех вариантов и не
повторяет их ошибок:

1. **Три отдельных проекта** - каждую работу можно сдать и запустить независимо.
2. **Слои `view` / `controller` / `model`** присутствуют во всех работах:
   * `view` - JAX-RS-контроллеры, DTO, сервер, статика;
   * `controller` - бизнес-объекты и `FacadeController` (ЛР3);
   * `model` - хранилище `InMemoryStore<T>` и `IdSequence` (ЛР3), не зависящее
     от BO/DTO.
3. **DTO на каждый метод** (ЛР2), включая DTO запросов и DTO ошибок.
4. **Явная регистрация `JacksonJsonProvider`** - иначе RESTEasy 3.15 не находит
   `MessageBodyWriter` для DTO и отвечает 500.
5. **Фасад-«Одиночка»** - `ReaderAPI` и `CreatorAPI` работают с одним набором
   данных; преобразование BO -> DTO инкапсулировано в фасаде.
6. **Поиск по ключевым словам** объединяет совпадения по заголовку, аннотации,
   тексту, тегам и по названию/описанию категории; дубликаты исключаются
   `LinkedHashSet`.
7. **Корректные HTTP-коды**: 200/201, 400 при неверных данных, 404 для
   отсутствующих сущностей (а не исключения).
8. **Java 8-совместимый код** (нет `var`, `List.of`, `isBlank`, `Files.readString`),
   хотя проект собирается и проверен также на современном JDK.
9. **Раздача статики Undertow** (`ClassPathResourceManager` + `ResourceHandler`)
   и полноценный клиентский интерфейс без внешних библиотек.
10. **Готовые файлы `requests.http`** в каждой работе и README с описанием
    проверки.

## Синхронизация с UML-диаграммами

После сверки с диаграммами из `docs/` (Use-Case, классы, состояния,
коммуникации, компоненты, развёртывание) в работы добавлено:

* **AdminAPI** во все три работы - актёр «Администратор», use case
  «Администрирование», класс `Admin` (`blockUser`/`assignRole`/`moderateComment`);
* **полная предметная модель в ЛР3**: интерфейсы `IAuthorizable`,
  `ICommentable`, `IPublishable`; иерархия `Person → Guest/User →
  Journalist/Admin`; сущности `News` (со статусами `DRAFT/PUBLISHED/EDITED/
  ARCHIVED/DELETED`), `Comment`, `Category`, `Tag`, `Favorite`, `Role`;
* **статусы новости** и endpoints `/creator/news/{id}/publish`, `/unpublish`, `/archive`;
* **регистрация/вход** (`/reader/auth/register`, `/reader/auth/login`) -
  use case «Регистрация / Авторизация».

Соответствие классов кода классам UML описано в [`lab3/README.md`](lab3/README.md).

## Проверенные сценарии

* ЛР1: все GET-методы (200), POST/PUT/DELETE (200), запуск на Java 8 и JDK 26.
* ЛР2: GET возвращают JSON-структуры; POST/PUT принимают JSON-тело и возвращают
  DTO; ошибки - 400 с `ActionStatusDTO`.
* ЛР3: лента, новость по id, новости за неделю, поиск по тексту и по категории,
  фильтр по региону, категории, закладки, комментарии (создание/изменение/ответ/
  удаление), создание/изменение/удаление новости, теги, медиа;
  статика `/static/index.html` и главная страница `/`.
