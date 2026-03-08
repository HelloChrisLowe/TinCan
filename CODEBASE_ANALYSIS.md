# TinCan Codebase Analysis & Improvement Suggestions

## Project Overview

TinCan is a LibGDX-based mobile game written in Kotlin, targeting Android (primary) and Desktop. Players tap on tin cans to score points, with increasing difficulty via a spawner system. The codebase is compact (~29 Kotlin files) with a clean game-object architecture.

---

## 1. Resource/Memory Management (High Priority)

### Textures are never disposed
`GameObject.setTexture()` creates a new `Texture` on every call but never disposes the old one. In LibGDX, `Texture` is a native resource that must be explicitly disposed or it will leak GPU memory.

**Affected files:**
- `GameObject.kt:67-68` — `setTexture(String)` creates `Texture` objects that are never freed
- `GameBackground.kt:10-11` — static `Texture` fields are never disposed
- `MenuButton.kt:27` — `whitePixelTexture` is created per-button instance, never disposed
- `Logo.kt`, `TinCanGame.kt` — similar issues

**Suggestion:** Implement a centralized `AssetManager` or texture cache. Dispose textures in `TinCanGame.dispose()`. For `MenuButton`, share a single white-pixel texture across all instances rather than loading one per button.

### Audio resources are never disposed
`Audio.kt` loads `Sound` and `Music` objects in `init()` but never calls `dispose()` on them.

**Suggestion:** Add a `dispose()` method to `Audio` and call it from `TinCanGame.dispose()`.

### SpriteBatch and BitmapFont disposal
`TinCanGame.dispose()` disposes `batch` but not `textFont`. The `BitmapFont` should also be disposed.

---

## 2. Architecture & Design (Medium-High Priority)

### Global mutable state via `object` singletons
`Director`, `Audio`, `GameBackground`, and `GameRandom` are all `object` singletons with mutable state. This creates tight coupling and makes the game difficult to test or reset cleanly.

- `Director.gameObjects` is a public `CopyOnWriteArrayList` directly mutated from many classes (`Can.kt`, `Spawner.kt`, `SettingsManager.kt`, `GameObject.deleteSelf()`)
- `GameBackground.blindTimer` and `shakeTimer` are set from `Can.kt`, `Boom.kt`, and `GameOver.kt`

**Suggestion:** Consider dependency injection or at minimum, reduce direct mutations. Have game objects return "events" or "commands" rather than directly modifying global state. For example, `Can.destroy()` could return a list of new objects to spawn instead of directly modifying `Director.gameObjects`.

### `TinCanGame.storedData` and `textFont` are `lateinit` statics
These `companion object` fields create hidden global dependencies. Every UI class reaches into `TinCanGame.storedData` and `TinCanGame.textFont`.

**Suggestion:** Pass these as constructor parameters or via a simple service locator to make dependencies explicit.

### `Can` implements `Pool.Poolable` but is never pooled
`Can` implements `Pool.Poolable` and has a `reset()` method, but `Spawner` creates `Can()` with `new` every time. The pooling interface is unused.

**Suggestion:** Either implement actual object pooling via `Pool<Can>` (would improve GC pressure on Android) or remove the `Poolable` interface to avoid confusion.

---

## 3. Game Loop & Timing (Medium Priority)

### Custom frame timing in `TinCanGame.update()` is fragile
The game uses `System.currentTimeMillis()` for manual frame pacing (lines 72-80), but LibGDX already provides `Gdx.graphics.deltaTime` for frame-independent updates. The current approach can skip or double-process frames unpredictably.

**Suggestion:** Use `Gdx.graphics.deltaTime` for time-based movement instead of dividing by a constant `FPS`. This makes the game run smoothly on devices with varying frame rates. The `@Suppress("NOTHING_TO_INLINE")` annotation on the `update()` method is also unnecessary — the Kotlin compiler handles this fine without annotation.

### Physics uses fixed FPS divisor instead of delta time
`GameObject.update()` divides velocities by `TinCanGame.FPS.toFloat()`. This means the game only runs correctly at exactly 60 FPS.

**Suggestion:** Pass `deltaTime` to `update()` methods for frame-rate-independent physics.

---

## 4. Potential Bugs (Medium Priority)

### Concurrent modification in `Director.endGame()`
`Director.endGame()` (line 96-101) iterates `gameObjects` and calls `gameObjects.remove()` inside the loop. While `CopyOnWriteArrayList` makes this technically safe, it's inefficient (each `remove()` copies the entire array) and the pattern is error-prone.

**Suggestion:** Collect items to remove, then remove them after the loop, or use `removeAll`.

### `isTouched()` hit detection is oversized
`GameObject.isTouched()` checks a region of `2 * sprite.width + 2 * TOUCH_BUFFER` wide and `2 * sprite.height + 2 * TOUCH_BUFFER` tall (centered on the sprite). This means the touchable area extends one full sprite-width beyond the sprite's actual bounds in each direction, which is likely larger than intended.

**Suggestion:** The check should probably use `sprite.width / 2` and `sprite.height / 2` instead of the full width/height, since it's measuring distance from center.

### `GameBackground` shake only produces negative offsets
`drawShakingBackground()` uses `GameRandom.nextFloat(-8f, -1f)` for both X and Y, so the background only ever shakes down-left. A proper shake should go in all directions.

**Suggestion:** Use `GameRandom.nextFloat(-8f, 8f)` for a realistic screen-shake effect.

### Debug `println` statements left in production code
`TinCanGame.kt:48-49` has `println` calls logging touch coordinates on every tap. These will show up in logcat and waste I/O.

**Suggestion:** Remove them or gate behind a debug flag.

---

## 5. Code Quality & Kotlin Idioms (Low-Medium Priority)

### `addSound()` can use `getOrPut`
```kotlin
// Current (Audio.kt:102-104)
val list = soundBank[tag] ?: mutableListOf()
list.add(sound)
soundBank[tag] = list

// Improved
soundBank.getOrPut(tag) { mutableListOf() }.add(sound)
```

### `Debris` texture selection has an unreachable `else` branch
`Debris.kt:13` has `else -> throw RuntimeException("Bad random value")` after `nextInt(3)` which only returns 0, 1, or 2. The else is dead code.

**Suggestion:** Use an array: `val textures = arrayOf("gib0.png", "gib1.png", "gib2.png")` and index directly.

### `Can.updateImage()` magic numbers
The damage-to-image mapping uses a `when` block with magic numbers. Consider using an array or making `lethal` and image names data-driven.

### `Logo` has dead copyright code
`Logo.kt` creates a label with empty string `""` and a comment "Disable for now." This is dead code that still allocates objects.

### `MenuButton.touch()` has duplicated volume logic
The four volume branches in `MenuButton.kt:62-91` repeat the same pattern. This could be extracted into a helper.

---

## 6. Build & Configuration (Low Priority)

### Desktop module uses legacy LWJGL backend
The desktop project depends on `gdx-backend-lwjgl` (LWJGL 2). LibGDX recommends `gdx-backend-lwjgl3` for modern systems.

### `DesktopStoredData` is a no-op stub
All methods return defaults and store nothing. If the desktop target is meant to be functional, it should use `Preferences` (LibGDX's cross-platform storage) or Java's `Preferences` API.

### No unit tests
There are zero test files in the project. Core logic like `GameRandom`, `Director.increaseScore()`, and `Spawner` phase transitions are easily testable.

**Suggestion:** Add a `core/test` source set and write unit tests for non-LibGDX logic.

---

## 7. Summary of Priorities

| Priority | Issue | Impact |
|----------|-------|--------|
| **High** | Texture/audio resource leaks | Memory leaks, potential OOM on Android |
| **High** | Debug println in production | I/O waste on every touch |
| **Medium** | No delta-time usage | Broken gameplay on non-60fps devices |
| **Medium** | Oversized touch hitbox | Gameplay feel / unintended taps |
| **Medium** | Shake only goes down-left | Visual bug |
| **Medium** | Unused Pool.Poolable on Can | Confusing/misleading code |
| **Low** | No tests | Maintainability |
| **Low** | Global mutable singletons | Testability, coupling |
| **Low** | Kotlin idiom improvements | Code cleanliness |
