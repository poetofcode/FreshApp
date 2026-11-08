# FreshApp

Self-hosted feed reader: a Node.js/Express backend that exposes a JSON API and serves a small
web UI, plus a Compose Multiplatform client for desktop and Android.

## Repository layout

| Path                 | Description                                                         |
|----------------------|---------------------------------------------------------------------|
| `app/`               | Backend sources (Express, MongoDB repositories, workers, HBS views) |
| `main.js`            | Backend entry point                                                 |
| `config.json`        | Backend configuration, not tracked by git (see below)               |
| `config_sample.json` | Reference copy of the configuration format                          |
| `docker-compose.yml` | MongoDB and API services for local development                      |
| `Dockerfile`         | API image definition                                                |
| `client/`            | Compose Multiplatform client (desktop + Android)                    |

## Configuration

### Backend: `config.json`

`config.json` lives in the repository root and is listed in `.gitignore`, so a fresh clone does
not have it. Create it from the sample:

```bash
cp config_sample.json config.json
```

`config_sample.json` always holds the correct format with placeholder values — treat it as the
reference and keep it in sync whenever the configuration schema changes (add a field there
first, then fill in the real value in `config.json`).

The file contains one block per environment, `dev` and `prod`. The active block is chosen by the
`FreshApp_ENV` environment variable (`dev` when unset; the Docker image sets `prod`):

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

| Key                         | Meaning                                                                              |
|-----------------------------|--------------------------------------------------------------------------------------|
| `port`                      | Port the API listens on inside the container                                         |
| `basePath`                  | Prefix for every public route, e.g. `/freshapp/api/v1/feed`, `/freshapp/front/login`  |
| `db.url`, `db.name`         | MongoDB connection string and database name                                          |
| `fcmEnabled`                | Toggles Firebase Cloud Messaging push notifications                                  |
| `parser.path`, `parser.key` | Upstream content parser endpoint and its access key                                  |

MongoDB is expected at `mongo_db:27017` in the `prod` block, because that is the service name
inside the Compose network; the `dev` block points at `127.0.0.1` for a locally installed mongod.

### Client: `client/local.properties`

The client does not hardcode the server address. It reads the `SERVER_URL` property from
`client/local.properties`, and the build turns it into `BuildConfig.SERVER_URL`
(see `buildConfig { ... }` in `client/composeApp/build.gradle.kts`):

```properties
SERVER_URL=http://127.0.0.1:3010/freshapp
```

Requirements for this value:

* it must include the `basePath` prefix (`/freshapp`), because the API is mounted under it;
* no trailing slash — the client normalises it, but keeping the value clean avoids double slashes;
* it is compiled into `BuildConfig`, so a change requires rebuilding the client, not just
  restarting it.

`local.properties` is machine-specific and is not meant to be shared between developers.

## Running the backend (Docker)

Start MongoDB and the API:

```bash
docker compose up
```

The API container listens on port `3000`, and Compose publishes it on host port `3010`
(`3010:3000` in `docker-compose.yml`). With `basePath` set to `/freshapp`, the local entry
points are:

* `http://127.0.0.1:3010/freshapp/front/login` — web UI
* `http://127.0.0.1:3010/freshapp/api/v1/...` — JSON API

`app/` is bind-mounted into the container, so source edits are picked up by `nodemon` without
rebuilding the image.

To recreate containers and rebuild the image after code changes:

```bash
docker compose up --build --abort-on-container-exit --remove-orphans
```

What the flags do:

* `--build` rebuilds the image. Docker still reuses cached layers, so only the layers whose
  inputs changed are rebuilt; for this project that normally means the layer that copies the
  application code, while the `npm install` layer stays cached.
* `--abort-on-container-exit` stops the whole stack as soon as any container exits, which keeps
  a foreground dev session from hanging around.
* `--remove-orphans` removes containers whose service no longer exists in the compose file.

Use the plain `docker compose up` when nothing but runtime state changed, and the `--build`
variant when sources, `package.json`, or the `Dockerfile` changed.

## Running the client (desktop)

```bash
cd client
./gradlew :composeApp:run
```

Add `--rerun-tasks` to ignore Gradle's up-to-date checks and force a rebuild before launching:

```bash
./gradlew :composeApp:run --rerun-tasks
```

JDK 17 or newer is required — the Android Gradle Plugin refuses to run on older JDKs.

Build a distributable:

```bash
./gradlew :composeApp:createDistributable
```

Known Gradle/Compose task-naming issue, see
https://stackoverflow.com/questions/77429595/cannot-locate-tasks-that-match-composeappcompilejava-as-task-compilejava-i
and https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md

## TODO List

* Apply ImageLoader lib: https://github.com/qdsfdhvh/compose-imageloader (other libs: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html#remote-files)
* Add scrollbars on desktop: https://foso.github.io/Jetpack-Compose-Playground/desktop/general/scrollbar/

## Links

* Kotlin Multiplatform: https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html
* KMP Libraries: https://github.com/AAkira/Kotlin-Multiplatform-Libraries
* Compose Multiplatform WebView: https://github.com/KevinnZou/compose-webview-multiplatform
