# DercoVisuals

[![Build](https://github.com/andreyromanov681-boop/DercoVisuals/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/andreyromanov681-boop/DercoVisuals/actions/workflows/ci.yml?query=branch%3Amain)

**DercoVisuals** — бесплатный клиентский мод для **Minecraft Java 1.21.11 + Fabric**. Требуется **Java 21**. Проект не использует телеметрию, рекламу, подписки или обязательную авторизацию.

> **Актуальная версия находится в `main`.** Ветки `main` и `pulse-foundation` синхронизированы и указывают на один и тот же проверенный код. Старое состояние `main`, существовавшее до синхронизации 22 ноября 2026 года, сохранено в ветке `main-before-pulse-foundation-2026-11-22`.

## Текущее состояние

Последняя подтверждённая сборка: **CI #121**, commit `e9117c9c9962c57b89fab1e68f915ade55ad9c16`.

- Java 21 / Gradle build — успешно.
- `tools/verify_derco_jar.py` — успешно.
- Проверка JAR и загрузка CI-артефакта — успешно.
- Запуск Minecraft-клиента вручную в рамках этой проверки не выполнялся.

[Открыть последнюю успешную сборку #121](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/45009314410) · [Все сборки ветки main](https://github.com/andreyromanov681-boop/DercoVisuals/actions?query=branch%3Amain)

## ClickGUI

Верхняя навигация содержит ровно три вкладки:

1. **Модули** — модульная сетка и категории.
2. **Друзья** — локальный список игроков.
3. **Настройки** — секции «Метки» и «Конфигурации».

Категории внутри вкладки «Модули» идут в порядке:

| Порядок | Категория |
|---:|---|
| 1 | PvP |
| 2 | Visuals |
| 3 | Utility |
| 4 | HUD |
| 5 | Helper |
| 6 | Other |

Функции вкладок «Метки» и «Конфигурации» сохранены как внутренние разделы «Настроек».

## Друзья и NoFriendDamage

- Список друзей хранится локально в `config/dercovisuals/friends.txt`.
- Имена проверяются и дедуплицируются без учёта регистра.
- При первом запуске без `friends.txt` выполняется однократная миграция старой `ConfigState.FriendHistoryState`.
- **NoFriendDamage** всегда активен; вместо переключателя показывается локализованный бейдж «Всегда включён» / “Always enabled”.
- Клиентский фильтр отменяет атаки, инициированные этим клиентом, по игрокам из локального списка. Это не серверная защита от всех источников урона.

## Установка последней сборки

Самая свежая проверенная сборка доступна в артефактах GitHub Actions:

[Скачать артефакт CI #120](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/45006063164)

Внутри ZIP находятся runtime JAR и sources JAR. Для установки в Minecraft используйте `DercoVisuals-1.0.0.jar` и поместите его в папку `.minecraft/mods` вместе с Fabric API.

> Раздел GitHub Releases с тегом `latest` пока не синхронизирован с этой веткой. До отдельной публикации используйте артефакт CI #120, а не старый Release.

## Сборка из исходников

Требования: JDK 21, Gradle Wrapper и доступ к интернету для первой загрузки зависимостей.

Linux/macOS:

```bash
./gradlew build
python3 tools/verify_derco_jar.py
```

Windows PowerShell:

```powershell
.\gradlew.bat build
python tools\verify_derco_jar.py
```

CI выполняет генерацию из `pulse-src.zip`, затем локальные генераторные слои, Java 21 build и обязательную проверку итогового JAR.

## Правила разработки

- Рабочая ветка: `pulse-foundation`.
- Не редактировать вручную сгенерированные файлы в `src/main/java`; источник — `tools/prepare_pulse_foundation.py` и последующие слои в `tools/`.
- Сохранять существующую функциональность; не добавлять телеметрию.
- Не изменять `main` и `pulse-src.zip` в рамках текущей линии разработки.
- После изменений обновлять `docs/AI_NOTES.md`, собирать на Java 21 и запускать `tools/verify_derco_jar.py`.
- Успешная CI-сборка не равна проверке запуска внутри Minecraft; указывать эти результаты отдельно.

## Документация

- [AI handoff / история изменений](docs/AI_NOTES.md)
- [Правила проекта для AI и разработчиков](AGENTS.md)
- [Аудит исходной основы](docs/PULSE_SOURCE_AUDIT.md)
- [История CI-сборок](https://github.com/andreyromanov681-boop/DercoVisuals/actions?query=branch%3Apulse-foundation)
