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

### What It Does (v0.2)

* Runs continuously in the background
* **AI-Generated Observations:** Uses Groq (Llama 3.1) to generate context-aware thoughts based on time and idle duration
* **OS Notifications:** Triggers native desktop toasts (Windows PowerShell / Linux `notify-send`)
* Debug mode (fast cycle) & Production mode (long interval)
* Temporary silent mode & Scheduled silent window (default 22:00 → 07:00)
* Lightweight CLI control server (Port 9333)
* Logs activity locally

It wakes up.
Checks time.
Decides.
Sleeps again.


Single main loop.
Control thread for runtime commands.
No overengineering.

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
### Why This Exists
Sometimes I don’t want an app.

I want a presence.

Something small.
Something local.
Something that runs quietly
and occasionally breaks the silence.

Not motivation.
Not advice.
Just a signal.

### Roadmap
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