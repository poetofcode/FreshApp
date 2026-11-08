# FreshApp

Самостоятельно размещаемый читатель лент: бэкенд на Node.js/Express, который отдаёт JSON API
и небольшой веб-интерфейс, плюс клиент на Compose Multiplatform для десктопа и Android.

## Структура репозитория

| Путь                 | Описание                                                              |
|----------------------|-----------------------------------------------------------------------|
| `app/`               | Исходники бэкенда (Express, репозитории MongoDB, воркеры, шаблоны HBS)  |
| `main.js`            | Точка входа бэкенда                                                   |
| `config.json`        | Конфигурация бэкенда, не хранится в git (см. ниже)                     |
| `config_sample.json` | Эталонный образец формата конфигурации                                 |
| `docker-compose.yml` | Сервисы MongoDB и API для локальной разработки                         |
| `Dockerfile`         | Определение образа API                                                |
| `client/`            | Клиент на Compose Multiplatform (десктоп + Android)                    |

## Конфигурация

### Бэкенд: `config.json`

`config.json` лежит в корне репозитория и указан в `.gitignore`, поэтому в свежем клоне его
нет. Создайте его из образца:

```bash
cp config_sample.json config.json
```

`config_sample.json` всегда содержит правильный формат с фиктивными значениями — считайте его
эталоном и правьте его первым при изменении схемы конфигурации (сначала добавить поле в
образец, затем проставить реальное значение в `config.json`).

Файл содержит по одному блоку на окружение, `dev` и `prod`. Активный блок выбирается переменной
окружения `FreshApp_ENV` (если она не задана — `dev`; образ Docker выставляет `prod`):

```json
{
  "dev": {
    "port": 3000,
    "basePath": "/freshapp",
    "db": { "url": "mongodb://127.0.0.1:27017/", "name": "freshapp" },
    "fcmEnabled": false,
    "parser": { "path": "http://0.0.0.0/site2api/site/fresh/", "key": "xxxxx" }
  }
}
```

| Ключ                        | Назначение                                                                         |
|-----------------------------|------------------------------------------------------------------------------------|
| `port`                      | Порт, на котором API слушает внутри контейнера                                     |
| `basePath`                  | Префикс всех публичных маршрутов, например `/freshapp/api/v1/feed`, `/freshapp/front/login` |
| `db.url`, `db.name`         | Строка подключения к MongoDB и имя базы данных                                     |
| `fcmEnabled`                | Включает push-уведомления через Firebase Cloud Messaging                           |
| `parser.path`, `parser.key` | Адрес внешнего парсера контента и ключ доступа к нему                              |

В блоке `prod` MongoDB ожидается по адресу `mongo_db:27017` — это имя сервиса внутри сети
Compose; блок `dev` указывает на `127.0.0.1` для локально установленного mongod.

### Клиент: `client/local.properties`

Клиент не содержит адрес сервера в коде. Он читает свойство `SERVER_URL` из
`client/local.properties`, а при сборке значение превращается в `BuildConfig.SERVER_URL`
(см. блок `buildConfig { ... }` в `client/composeApp/build.gradle.kts`):

```properties
SERVER_URL=http://127.0.0.1:3010/freshapp
```

Требования к значению:

* в нём должен быть префикс `basePath` (`/freshapp`), потому что API смонтировано под ним;
* без завершающего слэша — клиент его нормализует, но чистое значение избавляет от двойных
  слэшей;
* значение компилируется в `BuildConfig`, поэтому после изменения нужно пересобрать клиент, а
  не просто перезапустить его.

`local.properties` привязан к конкретной машине и не предназначен для обмена между
разработчиками.

## Запуск бэкенда (Docker)

Поднять MongoDB и API:

```bash
docker compose up
```

Контейнер API слушает порт `3000`, а Compose публикует его на порту `3010` хоста
(`3010:3000` в `docker-compose.yml`). При `basePath` равном `/freshapp` локальные точки входа:

* `http://127.0.0.1:3010/freshapp/front/login` — веб-интерфейс
* `http://127.0.0.1:3010/freshapp/api/v1/...` — JSON API

Каталог `app/` примонтирован в контейнер, поэтому правки исходников подхватываются `nodemon`
без пересборки образа.

Чтобы пересоздать контейнеры и пересобрать образ после изменения кода:

```bash
docker compose up --build --abort-on-container-exit --remove-orphans
```

Что делают ключи:

* `--build` пересобирает образ. Docker при этом переиспользует закэшированные слои, поэтому
  пересобираются только слои, у которых изменились входные данные; в этом проекте это обычно
  слой с копированием кода приложения, а слой с `npm install` остаётся в кэше.
* `--abort-on-container-exit` останавливает весь стек, как только завершается любой контейнер —
  так foreground-сессия разработки не остаётся висеть.
* `--remove-orphans` удаляет контейнеры, чьих сервисов больше нет в compose-файле.

Обычный `docker compose up` используйте, когда изменилось только состояние во время работы,
а вариант с `--build` — когда изменились исходники, `package.json` или `Dockerfile`.

## Запуск клиента (десктоп)

```bash
cd client
./gradlew :composeApp:run
```

Добавьте `--rerun-tasks`, чтобы проигнорировать проверки актуальности Gradle и принудительно
пересобрать проект перед запуском:

```bash
./gradlew :composeApp:run --rerun-tasks
```

Требуется JDK 17 или новее — Android Gradle Plugin не запускается на более старых JDK.

Сборка дистрибутива:

```bash
./gradlew :composeApp:createDistributable
```

Известная проблема с именованием задач Gradle/Compose:
https://stackoverflow.com/questions/77429595/cannot-locate-tasks-that-match-composeappcompilejava-as-task-compilejava-i
и https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md

## Список задач

* Подключить ImageLoader: https://github.com/qdsfdhvh/compose-imageloader (другие библиотеки: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html#remote-files)
* Добавить скроллбары на десктопе: https://foso.github.io/Jetpack-Compose-Playground/desktop/general/scrollbar/

## Ссылки

* Kotlin Multiplatform: https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html
* KMP Libraries: https://github.com/AAkira/Kotlin-Multiplatform-Libraries
* Compose Multiplatform WebView: https://github.com/KevinnZou/compose-webview-multiplatform
