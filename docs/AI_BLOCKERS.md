# DercoVisuals — блокеры сборки и проверки

## 1. Компиляция в лаунчере невозможна: игра доступна только в intermediary-именах

`project_build` (Java 21) на инстансах `fabric-1.21.11-2-80d4` и `pulse-1.21.11-beta`
даёт одинаковый результат:

```
ERROR in src/ai/derco/DercoAssets.java (at line 3)
    import net.minecraft.client.MinecraftClient;
    The import net.minecraft.client.MinecraftClient cannot be resolved
ERROR ... net.minecraft.client.gui cannot be resolved
ERROR ... net.minecraft.util.Identifier cannot be resolved
ERROR ... net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents cannot be resolved
```

Доказательство, что это НЕ ошибки нашего кода: пробник `pulsehud/src/main/java/com/enizer/pulsehud/Probe.java`,
написанный в intermediary-именах (`net.minecraft.class_310`, `class_332`, `class_327`, `class_742`,
`class_2338`, `ctx.method_51433(...)`), собирается успешно (`pulsehud/build/ai/pulsehud.jar`, 3 класса).

Причина: у лаунчера на диске лежит игровой jar с intermediary-именами (то, что грузится в рантайме),
а yarn-mapped (named) jar появляется только после `gradlew`-прогона loom, которому нужна сеть
(`maven.fabricmc.net`, `libraries.minecraft.net`, mappings `1.21.11+build.3`). Сети и доступа к
GitHub/Maven в этой среде нет, кэша `~/.gradle` с зависимостями loom тоже нет.
Fabric API в classpath сборки также не попадает — его классы (не remapped по пакетам) не разрешаются.

Итог: `helper` можно проверить только `gradlew build` на машине с сетью. Ошибки компиляции
yarn-кода отсюда получить нельзя.

## 2. Что нужно, чтобы собрать helper оффлайн (для передачи наружу)

- JDK 21 (`JAVA_HOME`), Gradle wrapper проекта (`gradlew.bat`);
- прогретый `~/.gradle/caches` после хотя бы одного онлайн-прогона:
  - `net.fabricmc:fabric-loom:1.14.10` и её транзитивные зависимости;
  - `net.fabricmc:yarn:1.21.11+build.3` (v2 + merged v2 jar);
  - `net.fabricmc:intermediary:1.21.11`;
  - `net.fabricmc:fabric-loader:0.18.1`;
  - `net.fabricmc.fabric-api:fabric-api:0.141.6+1.21.11`;
  - Minecraft client/server jars 1.21.11 и `version.json`/assets index;
  - библиотеки Minecraft (LWJGL 3, `org.slf4j`, `com.google.guava`, `net.java.dev.jna` и т.д.);
- плагины: `fabric-loom`, `java-library`, `maven-publish` (уже в `build.gradle`).

Оффлайн-прогон: `gradlew build --offline`. Если чего-то не хватает, Gradle назовёт координаты —
их и нужно доложить в кэш.

## 3. API, которые нужно подтвердить первым же `gradlew build`

Код написан по докам Fabric/1.21.x, но не компилировался ни разу. Проверить в первую очередь:

1. `HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, Identifier, HudElementRenderer)`
   — пакет `net.fabricmc.fabric.api.client.rendering.v1.hud`, API Fabric 1.21.6+.
2. `KeyBinding.Category.create(Identifier)` — новая категория биндов (1.21.9+); старый вариант
   `new KeyBinding(..., "key.categories.dercovisuals")` не использовать.
3. `DrawContext.drawTexture(Identifier, int, int, float, float, int, int, int, int)` — 9 аргументов,
   u/v как float (`DercoAssets.draw`).
4. `ItemCooldownManager.getGroup(ItemStack)` в `HudCache.updateCooldowns` — самое сомнительное место:
   в 1.21.2+ менеджер перешёл на группы кулдаунов и имя метода/тип могли отличаться.
   Если не компилируется — убрать `getGroup` и дедуплицировать по `stack.getItem()`.
5. `StatusEffectInstance.getTranslationKey()`, `HungerManager.getSaturationLevel()`,
   `DrawContext.drawStackOverlay(TextRenderer, ItemStack, int, int)`, `getEquippedStack`,
   `ItemStack.isDamageable()/getDamage()/getMaxDamage()`.

## 4. Что нельзя сделать инструментами лаунчера

- Скопировать 113 PNG из `helper/DercoVisuals-assets-113-PNG/` в
  `src/main/resources/assets/dercovisuals/textures/gui/`: инструменты пишут только текст
  (`workspace_write`, `write_instance_file`), бинарники не пишутся. Копирование — вручную
  или скриптом при сборке.
- Запустить `gradlew`, `git` — командной оболочки у помощника нет.
- Открыть `pulse-src.zip` / `tools/prepare_pulse_foundation.py` как источник истины: файлов нет
  в рабочей папке (в `helper/tools/` только `launch.ps1`, `verify_derco_jar.py`, `assets/`).

## 5. Открытые задачи

- `gradlew build` (сеть) → починка ошибок по списку из §3.
- `en_us.json`/`ru_ru.json`: ключи биндов есть, но заголовки/описания модулей в GUI пока
  захардкожены по-английски — перевести на `Text.translatable("module.dercovisuals.<id>")`.
- Скопировать PNG, затем включить `DercoAssets.draw` в `MenuScreen` (сейчас рисуется
  процедурный фон; PNG опциональны и проверяются через `logoPresent()`/`iconPresent()`).
- `docs/AI_NOTES.md` — дополнять при каждом прогоне сборки (файл большой, писать туда
  аккуратно, не перезаписывая целиком).
