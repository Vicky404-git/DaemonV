# 📖 DaemonV Manual

## 1. Requirements

### Linux

Install:

* Java 17+
* `notify-send`
* `xdotool`
* `pactl` or `wpctl`

### Windows

Install:

* Java 17+
* PowerShell enabled

---

# 2. Setup

## Create `.env`

Inside the project root:

```env
GROQ_API_KEY=gsk_your_api_key_here
```

Get a free API key from:

[GroqCloud](https://console.groq.com?utm_source=chatgpt.com)

If the API key is missing, DaemonV switches to fallback mode automatically.

---

# 3. Building

## Linux

```bash
mkdir -p out
javac -d out src/**/*.java
```

## Windows

```powershell
.\build.bat
```

---

# 4. Running

## Production Mode

Long intervals + silent windows enabled.

```bash
java -cp out Main
```

---

## Debug Mode

Fast testing mode.

```bash
java -cp out Debug
```

Debug mode:

* triggers every 5 seconds
* ignores silent windows
* runs diagnostics

---

# 5. Command Line Flags

## `--ask`

Immediately forces a trigger.

```bash
java -cp out Main --ask
```

---

## `--silent <minutes>`

Starts in temporary silent mode.

```bash
java -cp out Main --silent 60
```

---

## `--debug`

Enables debug timing.

```bash
java -cp out Main --debug
```

---

## `--menu`

Opens remote CLI menu.

```bash
java -cp out Main --menu
```

---

# 6. Remote Control System

DaemonV runs a socket control server on:

```text
localhost:9333
```

You can interact with it from:

* another terminal
* scripts
* external tools

---

## Available Commands

### Status

```text
STATUS
```

---

### Temporary Silence

```text
SILENT 30
```

---

### Change Trigger Interval

```text
INTERVAL 120
```

---

### Force Trigger

```text
TRIGGER
```

---

### Send Manual Notification

```text
NOTIFY wake up.
```

---

### Shutdown Daemon

```text
EXIT
```

---

# 7. Behavior States

DaemonV classifies behavior automatically.

| State        | Meaning                          |
| ------------ | -------------------------------- |
| `FOCUSED`    | Coding / terminal work           |
| `DISTRACTED` | Doomscrolling / passive browsing |
| `PASSIVE`    | Music or passive media           |
| `IDLE`       | User absent                      |

These states affect:

* AI personality
* tone
* generated observations

---

# 8. Logging

All events are stored in:

```text
dataset.csv
```

Stored fields:

* timestamp
* idle duration
* active window
* silent status
* detected state
* generated message

---

# 9. Linux Notes

DaemonV currently works best on:

* X11
* i3
* KDE X11
* XFCE

Wayland support is experimental.

---

# 10. Philosophy

DaemonV is not an assistant.

It does not help you.

It observes you.

Quietly.

