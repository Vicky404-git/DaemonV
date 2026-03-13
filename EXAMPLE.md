# 📝 Example Output

When DaemonV triggers, it logs an event to the console and attempts to send a native desktop notification (Windows Toast or Linux `notify-send`).

### Standard Run (Debug Mode)
Here is an example of the console output when running `java -cp out Debug --ask` with a valid Groq API key:

DaemonV 
Mode: Background Observer
AI: Enabled (Groq)
--------------------------------
Manual trigger mode enabled.
[Daemon] Control Server listening on port 9333
[2026-03-11T18:18:28.746] As the last rays of daylight fade beyond the horizon, the evening's silence is woven from the threads of time itself.
[2026-03-11T18:18:34.038] Shadows creep across the room, a gentle reminder that even in stillness, time continues its insistent creep.
[2026-03-11T18:18:39.183] As the last rays of sunlight fade into the evening, the room's gentle silence is woven from the threads of forgotten hours.

## Fallback Mode (No Internet / No API Key)
If the .env file is missing, the API key is invalid, or the connection drops, DaemonV automatically switches to its internal weighted fallback system:

DaemonV 
Mode: Background Observer
AI: Disabled (Mock)
--------------------------------
[Daemon] Control Server listening on port 9333
[2026-03-11T20:45:12.112] Time is a flat circle, and I am watching the loop.
[2026-03-11T20:50:12.112] Another interval has passed into the void.

## Night Window (Late Night Fallback)
If it is between midnight and 5:00 AM, the fallback messages shift to match the late hour:

[2026-03-12T02:15:00.000] The world is asleep. Your screen is the only sun.
[2026-03-12T03:15:00.000] The background noise of the universe is louder at this hour.

## Silent Mode
DaemonV can be placed in silent mode for a specified duration. During this time, no notifications will be triggered. The silent mode can be enabled for a specified number of minutes using the `enableSilent` function.

## Customizable Schedule
The daemon's trigger schedule can be customized. By default, it is set to trigger between 10 PM and 7 AM. The schedule can be updated using the `setSchedule` function.

## Forced Trigger
The daemon can be forced to trigger immediately using the `forceTrigger` function.

## System Monitor
The system monitor tracks the system's idle time and active window. The idle time can be obtained using the `getIdleMinutes` function and the active window can be obtained using the `getActiveWindow` function.

## Event Logging
All events are logged to a dataset file named "dataset.csv". The events are logged using the `notifyAndLog` function.

## Debug Mode
In debug mode, the daemon can be run with a custom interval and check period using the `setDebugMode` function.

## Environment Variables
The daemon uses environment variables stored in a .env file. The GROQ_API_KEY variable is required for AI-powered notifications. The environment variables can be loaded using the `load` function and accessed using the `get` function.

## Remote Control
The daemon has a remote control server that listens on port 9333. The server can be started using the `startServer` function.

## Diagnostics
The daemon can run diagnostics using the `runDiagnostics` function. 

## Menu
The daemon has a menu that can be started using the `startMenu` function. 

## Sending Commands
The daemon can send commands using the `send` function. 

## Daemon Controls
The daemon has several controls that can be used to customize its behavior. These include:

- `setInterval`: sets the interval between triggers
- `setSchedule`: sets the schedule for triggers
- `forceTrigger`: forces the daemon to trigger immediately
- `enableSilent`: enables silent mode for a specified duration
- `setDebugMode`: sets the debug mode interval and check period

## Functions
The daemon has several functions that can be used to interact with it. These include:

- `isSilentNow`: checks if the daemon is currently in silent mode
- `getIdleMinutes`: gets the system's idle time
- `getActiveWindow`: gets the active window
- `notifyAndLog`: logs an event to the dataset file
- `generate`: generates a notification message using the AI engine
- `fallback`: generates a fallback notification message
