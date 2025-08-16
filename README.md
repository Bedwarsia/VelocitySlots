# VelocitySlots

VelocitySlots is a simple Velocity plugin that **fakes the maximum player slots** in your server’s MOTD.

* Supports dynamic slot adjustment based on online players with an offset.
* Can also be set to a static number or an “unlimited” mode.
* Lightweight and requires **no other plugin dependencies**.

---

## Features

* **FAKE mode**: Show `online players + offset` as max slots.
* **STATIC mode**: Always show a fixed maximum slot number.
* **UNLIMITED mode**: Show a very high number to appear as “unlimited slots.”
* Configurable via `config.yml`.

---

## Installation

1. Place the `VelocitySlots.jar` in your `plugins/` folder.
2. Start Velocity — a default `config.yml` will be generated automatically.
3. Stop Velocity and edit `plugins/VelocitySlots/config.yml` as needed.
4. Restart Velocity to apply changes.

---

## Configuration (`config.yml`)

```yaml
# VelocitySlots configuration

# Mode options: FAKE, STATIC, UNLIMITED
mode: FAKE

# Used in FAKE mode: displayed max = online + offset
offset: 10

# Used in STATIC mode: fixed max slots
static_slots: 60

# Used in UNLIMITED mode: very high number of slots
unlimited_slots: 1000
```

---

## Example

If you have **50 players online** and `offset: 10` in FAKE mode:

```
MOTD: 50/60
```

---

## Building from source

1. Clone the repository:

```
git clone <repo-url>
```

2. Build with Gradle:

```
./gradlew build
```

3. The output JAR will be in `build/libs/`.

---

## License

[GPL v3 License](LICENSE)