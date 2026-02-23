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
No frameworks.  
No dependencies.  
No cloud.  

v0.1 does not include AI yet.  
That comes later.  

### What It Does (v0.1)

* Runs continuously  
* Debug mode (fast cycle)  
* Production mode (long interval)  
* Temporary silent mode  
* Scheduled silent window (default 22:00 → 07:00)  
* Manual trigger  
* CLI control menu  
* Lightweight control server  
* Logs activity  

It wakes up.  
Checks time.  
Decides.  
Sleeps again.  

### Structure

```text
src/
 ├── core/
 │    MainLoop.java
 │    Scheduler.java
 │    ControlServer.java
 │
 ├── engine/
 │    DecisionEngine.java
 │    MessageEngine.java
 │
 ├── monitor/
 │    IdleDetector.java
 │
 ├── logging/
 │    EventLogger.java
 │
 ├── cli/
 │    Menu.java
 │
 ├── Debug.java
 └── Main.java

 ```
 Single main loop.
Control thread for runtime commands.
No overengineering.

### Build & Run
### Build:
``` PowerShell
.\build.bat
```
### Production:
``` PowerShell
java -cp out Main
```
### Debug (fast interval):
``` PowerShell
java -cp out Debug
```
### Open the Remote Control Menu:
```PowerShell
java -cp out Main --menu
```
### Start with a Manual trigger:
```PowerShell
java -cp out Main --ask
```
### Start Silent for 5 minutes:
```PowerShell
java -cp out Main --silent 5
```
### Silent Modes
There are two kinds of silence:
1. Temporary silence: (X minutes)
2. Scheduled silence: (night window)

Even background observers need boundaries.

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
- v0.2 → AI-based message generation (rare triggers only)
- v0.3 → OS notifications
- v0.4 → Real idle detection
- v0.5 → Local model option

The daemon will evolve slowly.

No rush.

### Disclaimer
It is intentionally minimal.

If you are looking for dashboards, analytics, metrics, or gamification —

this is not that.

This is just a background process

that knows what time it is.