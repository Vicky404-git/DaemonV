# 📝 Example Output

When DaemonV triggers, it logs an event to the console and attempts to send a native desktop notification (Windows Toast or Linux `notify-send`).

### Standard Run (Debug Mode)
Here is an example of the console output when running `java -cp out Debug --ask` with a valid Groq API key:

```text
DaemonV v0.2
Mode: Background Observer
AI: Enabled (Groq)
--------------------------------
Manual trigger mode enabled.
[Daemon] Control Server listening on port 9333
[2026-03-11T18:18:28.746] As the last rays of daylight fade beyond the horizon, the evening's silence is woven from the threads of time itself.
[2026-03-11T18:18:34.038] Shadows creep across the room, a gentle reminder that even in stillness, time continues its insistent creep.
[2026-03-11T18:18:39.183] As the last rays of sunlight fade into the evening, the room's gentle silence is woven from the threads of forgotten hours.
```
## Fallback Mode (No Internet / No API Key)
If the .env file is missing, the API key is invalid, or the connection drops, DaemonV automatically switches to its internal weighted fallback system:

```Plaintext
DaemonV v0.2
Mode: Background Observer
AI: Disabled (Mock)
--------------------------------
[Daemon] Control Server listening on port 9333
[2026-03-11T20:45:12.112] Time is a flat circle, and I am watching the loop.
[2026-03-11T20:50:12.112] Another interval has passed into the void.
```
Night Window (Late Night Fallback)
If it is between midnight and 5:00 AM, the fallback messages shift to match the late hour:

```Plaintext
[2026-03-12T02:15:00.000] The world is asleep. Your screen is the only sun.
[2026-03-12T03:15:00.000] The background noise of the universe is louder at this hour.
```