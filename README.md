# 🧠 DaemonV

> An ambient machine-consciousness daemon for Linux and Windows that quietly observes your digital behavior and comments on it using AI.

DaemonV is a lightweight, context-aware background daemon written in Java.

It quietly runs in the background, checks the time, observes what you're doing, and occasionally reacts.

Sometimes it gives atmosphere.

Sometimes philosophy.

Sometimes it roasts you for wasting three hours on YouTube at 2 AM.

No GUI.
No bloat.
Pure Java.
Minimal runtime footprint.

---

# ![DaemonV in Action](assets/screenshot_main.jpeg)

*DaemonV monitoring an i3 workspace and delivering a contextual AI observation.*

---

# ✨ Features

* **AI-generated observations** using Groq + Llama 3.1
* **Context awareness**

  * active window
  * idle time
  * audio state
  * current time
* **4 behavioral states**

  * `FOCUSED`
  * `DISTRACTED`
  * `PASSIVE`
  * `IDLE`
* **Personality-driven responses**

  * Stoic
  * Sarcastic
  * Philosophical
  * Chill
* Native desktop notifications
* Scheduled and temporary silent modes
* Runtime control through a lightweight TCP server
* CLI control menu
* Local event logging
* Automatic CSV rotation
* Persistent notes
* AI-generated behavioral memory
* Memory validation before replacement
* Automatic memory upgrade scheduling
* PID lock to prevent ghost/multiple daemons
* Debug and diagnostic mode
* Linux + Windows support
* X11 + Hyprland active-window detection

---

# 🧠 How It Works

DaemonV periodically wakes up and observes the system.

```text
Time
  │
  ├── Idle time
  ├── Active window
  └── Audio state
          │
          ▼
   BehaviorEngine
          │
          ├── FOCUSED
          ├── DISTRACTED
          ├── PASSIVE
          └── IDLE
          │
          ▼
    MessageEngine
          │
          ▼
   AI Observation
          │
          ├── Desktop notification
          └── Local event log
```

The normal daemon checks its environment every **5 minutes** and has a default notification cooldown of **2 hours**.

It wakes up.

Checks.

Decides.

Sleeps again.

---

# 🧩 Behavior States

| State        | Meaning                                  | Personality   |
| ------------ | ---------------------------------------- | ------------- |
| `FOCUSED`    | Coding, terminal, IDE activity           | Stoic         |
| `DISTRACTED` | Social media, streaming, procrastination | Sarcastic     |
| `PASSIVE`    | Music, media, casual browsing            | Chill         |
| `IDLE`       | No interaction for 10+ minutes           | Philosophical |

The classification is handled locally by `BehaviorEngine`.

Examples:

> “The terminal glows like a furnace tonight.”

> “Another Reddit tab. Another hour dissolved.”

> “The speakers breathe softly in the background.”

> “Dust settles across silent circuitry.”

---

# 🧠 Behavioral Memory

DaemonV can maintain a small persistent memory of your usage patterns.

It stores:

```text
~/DaemonV/Memory/
├── memory.md
├── memory_draft.md
├── notes.txt
└── upgrade.log
```

### Notes

You can manually give DaemonV information to remember:

```text
NOTE I usually code late at night.
```

Notes are stored locally in:

```text
~/DaemonV/Memory/notes.txt
```

### Memory Upgrade

After enough logged events, DaemonV can summarize recent behavior into `memory.md`.

The memory system:

1. Reads recent events
2. Reads pinned notes
3. Reads existing memory
4. Generates a new summary with AI
5. Validates the generated summary
6. Replaces the old memory atomically if validation succeeds

Memory upgrades require at least **100 logged events** and are automatically scheduled on Friday, Saturday, or Sunday.

You can also trigger one manually from the CLI menu.

---

# 🛰 Remote Control

DaemonV exposes a local TCP control server on:

```text
localhost:9333
```

Available commands:

```text
STATUS
SILENT <minutes>
SCHEDULE <start> <end>
INTERVAL <seconds>
TRIGGER
NOTIFY <message>
NOTE <text>
UPGRADE
EXIT
```

The server is intended for local control and scripting.

---

# 🖥 CLI Menu

Run:

```bash
daemonv --menu
```

The menu provides:

```text
==== DaemonV Menu ====

1. Silence (minutes)
2. Set Interval (seconds)
3. Force Trigger
4. Status
5. Test Notify
6. Save a Note
7. Upgrade Memory
8. Quit DaemonV
9. Exit CLI
```

---

# 🌙 Silent Mode

DaemonV supports both temporary and scheduled silence.

### Temporary

```text
SILENT 60
```

Silences notifications for 60 minutes.

### Scheduled

Default:

```text
22:00 → 07:00
```

Can be changed remotely:

```text
SCHEDULE 23 8
```

Silent mode stops notifications while the daemon continues monitoring.

---

# 📊 Local Data

DaemonV stores its data under:

```text
~/DaemonV/
```

Current structure:

```text
~/DaemonV/
├── CSVs/
│   └── events.csv
└── Memory/
    ├── memory.md
    ├── memory_draft.md
    ├── notes.txt
    └── upgrade.log
```

Events contain:

* timestamp
* hour
* idle duration
* active window
* silent state
* trigger reason
* generated message

The event CSV automatically rotates when it reaches approximately **5 MB**.

---

# 🔔 Notifications

### Linux

Uses:

```bash
notify-send
```

### Windows

Uses PowerShell's native notification facilities.

DaemonV does not require a GUI of its own.

---

# 🖥 Platform Support

## Linux

Currently supports:

* X11
* i3
* KDE X11
* XFCE
* Hyprland / Hyprland-based Wayland setups

Linux system monitoring can use:

```text
xprintidle
xdotool
pactl
wpctl
hyprctl
```

## Windows

Uses:

* PowerShell
* Java AWT mouse information
* Windows process inspection

Wayland support outside the currently implemented Hyprland detection remains limited.

---

# 🚀 Installation

## Requirements

### Linux

* Java 17+
* `xprintidle`
* `xdotool`
* `notify-send`
* `pactl` or `wpctl`

For Hyprland:

* `hyprctl`

### Windows

* Java 17+
* PowerShell

---

# 🔑 AI Setup

DaemonV uses Groq for AI-generated observations.

Create:

```text
~/.daemonv/.env
```

Add:

```env
GROQ_API_KEY=gsk_your_key_here
```

Get an API key from:

https://console.groq.com

If the API key is unavailable or the request fails, DaemonV falls back to local messages.

---

# 🏃 Running

## Installed command

```bash
daemonv
```

## JAR

```bash
java -jar DaemonV.jar
```

## Debug

```bash
daemonv --debug
```

or:

```bash
java -jar DaemonV.jar --debug
```

---

# 🧪 CLI Flags

| Flag        | Description                               |
| ----------- | ----------------------------------------- |
| `--menu`    | Open the remote control menu              |
| `--debug`   | Run in foreground with 5-second intervals |
| `--ask`     | Force a one-time trigger                  |
| `--version` | Print DaemonV version                     |
| `--help`    | Print command usage                       |

Current version:

```text
DaemonV v1.0.0
```

---

# 🔧 Debug & Diagnostics

Run:

```bash
java -cp out Debug
```

The diagnostic suite checks:

1. Environment / API key
2. System sensors
3. Notifications and dataset
4. Groq AI engine
5. Memory context
6. Note logger
7. MemoryManager availability

Example:

```text
==== DaemonV Debug & Diagnostic Suite ====

[1] Checking .env file... OK
[2] Checking Sensors... OK
[3] Testing Output & Dataset... OK
[4] Testing AI Context Engine (Groq)... OK
[5] Checking Memory Context... OK
[6] Testing Note Logger... OK
[7] Testing MemoryManager... SKIP
```

---

# ⚙️ Background Service

DaemonV can run as a systemd user service.

Example:

```ini
[Unit]
Description=DaemonV Observer
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/java -Xmx32m -Xms16m -jar %h/.daemonv/daemonv.jar
Restart=on-failure
RestartSec=5

[Install]
WantedBy=default.target
```

The daemon also uses a PID lock to prevent accidental duplicate instances.

If a stale lock exists:

```bash
rm ~/.daemonv.lock
```

---

# 📁 Project Structure

```text
DaemonV/
├── src/
│   ├── cli/
│   │   └── Remote.java
│   ├── core/
│   │   ├── Daemon.java
│   │   └── Env.java
│   ├── engine/
│   │   ├── BehaviorEngine.java
│   │   ├── MessageEngine.java
│   │   └── MemoryManager.java
│   ├── logging/
│   │   └── EventLogger.java
│   └── monitor/
│       └── SystemMonitor.java
│
├── DaemonV.jar
├── start.sh
├── daemonv.service
├── README.md
├── MANUAL.md
└── EXAMPLE.md
```

---

# 🛣 Roadmap

## v1.0.0

* [x] Context-aware AI observations
* [x] Behavioral classification
* [x] Native notifications
* [x] Silent scheduling
* [x] Remote control
* [x] Event logging
* [x] CSV rotation
* [x] PID lock
* [x] Cross-platform support
* [x] Hyprland window detection
* [x] Persistent notes
* [x] AI behavioral memory
* [x] Memory validation
* [x] Debug diagnostics

## Next

* [ ] More native Wayland support
* [ ] Better process detection
* [ ] Local AI via Ollama
* [ ] Offline-first AI mode
* [ ] Improved memory retrieval
* [ ] Plugin system
* [ ] Adaptive personalities

---

# ⚠ What DaemonV Is NOT

DaemonV is not:

* a productivity app
* a virtual assistant
* a Jarvis clone
* an automation framework
* a task manager
* a dashboard

It does not try to run your life.

It observes.

It reacts.

Then it goes back to sleep.

---

# 🌌 Philosophy

Most software tries to make you more productive.

DaemonV doesn't.

It simply exists alongside you.

Sometimes it notices that you're focused.

Sometimes it notices that you're wasting time.

Sometimes it says something strangely profound at 3 AM.

And sometimes it says nothing at all.

A small machine consciousness living quietly inside your operating system.

