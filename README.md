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
* **Message Generation Logic:** Implemented in `MessageEngine.java`, which uses the Groq API to generate messages based on the current hour and idle duration

### Silent Modes

* **Temporary Silent Mode:** Can be enabled for a specified duration using the `enableSilent` method in `Daemon.java`
* **Scheduled Silent Window:** Configurable start and end hours (default 22:00 → 07:00) using the `setSchedule` method in `Daemon.java`

### Control and Monitoring

* **Lightweight CLI Control Server:** Accessible on Port 9333, implemented in `Remote.java`
* **Debug Mode:** Fast cycle for testing and development, configurable using the `setDebugMode` method in `Daemon.java`
* **Force Trigger:** Can be used to force a notification trigger using the `forceTrigger` method in `Daemon.java`

### System Monitoring

* **Idle Detection:** Tracks system idle time, implemented in `SystemMonitor.java`
* **Active Window Detection:** Identifies the currently active window, implemented in `SystemMonitor.java`
* **Event Logging:** Logs events and notifications, implemented in `EventLogger.java`

## Technical Details

### Daemon Class

The `Daemon` class is the core of the application. It has the following properties:

* `silentEnabled`: A flag indicating whether silent mode is enabled
* `silentUntilEpoch`: The epoch time until which silent mode is enabled
* `silentLogged`: A flag indicating whether silent mode has been logged
* `lastTriggerEpoch`: The epoch time of the last trigger
* `scheduleStartHour` and `scheduleEndHour`: The start and end hours of the scheduled silent window
* `cooldownMillis` and `checkSleepMillis`: The cooldown and check sleep intervals
* `ignoreSilentWindow`: A flag indicating whether to ignore the silent window
* `ai`: An instance of the `MessageEngine` class
* `running`: A flag indicating whether the daemon is running

The `Daemon` class has the following methods:

* `setDebugMode`: Sets the debug mode interval
* `enableSilent`: Enables silent mode for a specified duration
* `setSchedule`: Sets the scheduled silent window
* `setInterval`: Sets the notification interval
* `forceTrigger`: Forces a notification trigger
* `isSilentNow`: Checks if silent mode is enabled
* `start`: Starts the daemon
* `stop`: Stops the daemon

### Remote Class

The `Remote` class implements a lightweight CLI control server that listens on Port 9333. It has the following methods:

* `startServer`: Starts the control server
* `startMenu`: Starts the control menu
* `send`: Sends a command to the control server

### MessageEngine Class

The `MessageEngine` class generates context-aware messages based on the current time and idle duration. It uses the Groq (Llama 3.1) API to generate messages.

### SystemMonitor Class

The `SystemMonitor` class tracks system idle time, identifies the currently active window, and logs events and notifications.

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

* **API Key:** Required for Groq (Llama 3.1) API access, loaded from environment variables using `Env.java`
* **Silent Window:** Configurable start and end hours using the `setSchedule` method in `Daemon.java`
* **Interval:** Configurable interval for notifications using the `setInterval` method in `Daemon.java`
* **Debug Interval:** Configurable interval for debug mode using the `setDebugMode` method in `Daemon.java`

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
