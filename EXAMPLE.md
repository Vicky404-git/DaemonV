# 📝 Example Output

# Startup

```text
DaemonV v0.3 (Context-Aware)
AI: Enabled (Groq)
[Daemon] Control Server listening on port 9333
```

---

# 🧠 Focused

```text
[2026-05-07T01:42:11]
The terminal hums softly beneath another unfinished idea.
```

```text
[2026-05-07T02:18:04]
Neovim glows like a lighthouse in the dark apartment.
```

```text
[2026-05-07T03:12:44]
The compiler fears what you're about to commit.
```

```text
[2026-05-07T04:22:10]
Even the terminal looks tired.
```

---

# 📱 Distracted

```text
[2026-05-07T21:12:55]
Another Reddit tab. Another hour erased quietly.
```

```text
[2026-05-07T21:44:17]
You opened YouTube “for one video.”
```

```text
[2026-05-07T22:08:49]
The algorithm keeps feeding the void.
```

```text
[2026-05-07T23:50:00]
Three monitors. Seventeen tabs. Zero progress.
```

---

# 🎧 Passive

```text
[2026-05-07T18:02:30]
Music leaks into the room like distant weather.
```

```text
[2026-05-07T18:18:44]
The speakers breathe softly in the background.
```

---

# 🌑 Idle

```text
[2026-05-07T03:55:00]
The machine waits patiently in an empty room.
```

```text
[2026-05-07T04:10:27]
Dust settles across silent circuitry.
```

---

# ⚠ Fallback Mode

When:

* no internet exists
* API fails
* `.env` is missing

DaemonV switches to local fallback messages.

```text
[2026-05-07T01:00:00]
The world is asleep. Your screen is the only sun.
```

```text
[2026-05-07T19:15:00]
Time is a flat circle, and I am watching the loop.
```

---

# 🌙 Silent Mode

```text
[2026-05-07T22:00:00]
Entering Silent Mode
```

During silent mode:

* notifications stop
* daemon still runs
* monitoring continues

---

# 🛰 Remote Commands

## Force Trigger

```text
TRIGGER
→ Trigger queued.
```

---

## Silence For 60 Minutes

```text
SILENT 60
→ Silenced.
```

---

## Manual Notification

```text
NOTIFY wake up.
→ Notification sent.
```

---

# 🧪 Debug Diagnostics

```text
==== DaemonV Debug & Diagnostic Suite ====

[1] Checking .env file... OK
[2] Checking Sensors... OK
[3] Testing Output & Dataset... OK
[4] Testing AI Context Engine (Groq)... OK
```

---

# 📊 Dataset Example

```csv
timestamp,hour,idle,window,silent,reason,message

2026-05-07T21:12:55,21,0,"reddit",false,DISTRACTED,"Another tab opened."

2026-05-07T03:55:00,3,48,"Desktop",false,IDLE,"Dust settles across silent circuitry."
```

