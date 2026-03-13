# 🧠 DaemonV

A quiet process that lives in the background
and occasionally reminds you that time is moving.

DaemonV is a small Java daemon that runs continuously and speaks rarely.

It does not optimize your life.
It does not track streaks.
It does not care about productivity.

It just watches the clock
and sometimes says something.

### What It Is

A background observer.

* Time-aware
* Silence-aware
* Interval-based
* Minimal
* Console-driven

No GUI.
No heavy frameworks.
No bloated dependencies.

### What It Does

* Runs continuously in the background
* **AI-Generated Observations:** Uses Groq (Llama 3.1) to generate context-aware thoughts based on time and idle duration
* **OS Notifications:** Triggers native desktop toasts (Windows PowerShell / Linux `notify-send`)
* **Debug Mode:** Fast cycle for testing and development
* **Production Mode:** Long interval for normal operation
* **Temporary Silent Mode:** Mutes notifications for a specified duration
* **Scheduled Silent Window:** Default 22:00 → 07:00, configurable
* **Lightweight CLI Control Server:** Listens on Port 9333
* **Local Logging:** Activity logs are stored locally

It wakes up.
Checks time.
Decides.
Sleeps again.

Single main loop.
Control thread for runtime commands.
No overengineering.

## Features

### AI-Generated Observations

* Utilizes Groq (Llama 3.1) API for context-aware message generation
* Messages are based on current time and idle duration

### Silent Modes

* **Temporary Silent Mode:** Can be enabled for a specified duration
* **Scheduled Silent Window:** Configurable start and end hours (default 22:00 → 07:00)

### Control and Monitoring

* **Lightweight CLI Control Server:** Accessible on Port 9333
* **Debug Mode:** Fast cycle for testing and development

### System Monitoring

* **Idle Detection:** Tracks system idle time
* **Active Window Detection:** Identifies the currently active window

## Build & Run
See USER_MANUAL.md for full setup instructions, including API key configuration.

### Build:

```Bash
.\build.bat
```
### Production:

```Bash
java -cp out Main
```

## Configuration

* **API Key:** Required for Groq (Llama 3.1) API access
* **Silent Window:** Configurable start and end hours
* **Interval:** Configurable interval for notifications
* **Debug Interval:** Configurable interval for debug mode

## Roadmap
- ~~v0.2 → AI-based message generation~~ (Done)
- ~~v0.3 → OS notifications~~ (Done)
- v0.4 → Real idle detection (Native OS hooks)
- v0.5 → Local model option (Ollama integration)

The daemon will evolve slowly.

### Disclaimer
It is intentionally minimal.

If you are looking for dashboards, analytics, metrics, or gamification —
this is not that.

This is just a background process
that knows what time it is.
