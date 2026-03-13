# 📖 DaemonV User Manual

## 1. Setup & Configuration

DaemonV uses Groq Cloud's Llama 3.1 model to generate dynamic, context-aware messages. To enable this, you need to provide an API key.

### Creating the `.env` file
1. Get a free API key from [GroqCloud](https://console.groq.com/).
2. In the root directory of the repository (next to `build.bat`), create a file named exactly `.env`.
3. Add the following line to the file:
   ```env
   GROQ_API_KEY=gsk_your_api_key_here
   ```

*(Note: If the API key is missing or the internet is disconnected, DaemonV will gracefully fall back to a local pool of static observer messages.)*

### Compiling
DaemonV requires no external build tools like Maven or Gradle.

- Windows: Run .\build.bat (or .\build\build.bat depending on your folder structure).

- Linux (Arch): mkdir -p out && javac -d out src/**/*.java

## 2. Running the Daemon
DaemonV has two main entry points: Main (Production) and Debug (Testing).

### Production Mode
Runs the daemon with a long cooldown (default 4 hours) and respects the night-time silent window (configurable).

```Bash
java -cp out Main
```
### Debug Mode
Runs the daemon with a fast cycle (default 5 seconds) and ignores the night-time silent window. Useful for testing notifications and API connections.

```Bash
java -cp out Debug
```
### Command Line Flags
You can append these flags when starting either mode:

- --ask: Forces the daemon to trigger an observation immediately upon startup, regardless of the cooldown timer.

- --silent <minutes>: Starts the daemon in a temporary silent mode for the specified duration.

- --interval <seconds>: Overrides the default cooldown interval.

- --check <millis>: Overrides the internal thread sleep duration.

- --debug: Enables debug mode with a fast cycle.

**Example:** Start production, force an immediate trigger, and then go silent for an hour:

```Bash
java -cp out Main --ask --silent 60
```

## 3. Configuring the Daemon

### Setting the Cooldown Interval
The cooldown interval can be set using the `--interval` flag or by calling the `setInterval` method.

### Setting the Night-time Scheduled Silent Window
The night-time scheduled silent window can be set using the `--schedule` flag or by calling the `setSchedule` method.

### Enabling Silent Mode
Silent mode can be enabled using the `--silent` flag or by calling the `enableSilent` method.

## 4. Remote Control Menu
Because DaemonV is a faceless background process, you control it using a lightweight socket server (running on port 9333).

- Ensure the daemon is running in one terminal window (or completely in the background).
- Open a new, separate terminal window.
- Run the menu interface:

```Bash
java -cp out Remote
```
From this menu, you can dynamically:
- Check if the daemon is currently in a silent window.
- Force a manual trigger (sends a notification immediately).
- Change the trigger interval on the fly without restarting.
- Update the night-time scheduled silent window.

## 5. Event Logging
DaemonV logs events to a file named `dataset.csv`. The log file contains information about each notification, including the message, idle time, active window, silent status, and reason.

## 6. System Monitoring
DaemonV monitors system activity, including idle time and active window. The `SystemMonitor` class provides methods to get the idle time and active window.

## 7. Message Generation
DaemonV uses the `MessageEngine` class to generate dynamic messages. The message engine uses the Groq Cloud API to generate context-aware messages. If the API key is missing or the internet is disconnected, DaemonV falls back to a local pool of static observer messages.
