# 📖 DaemonV Manual

# Requirements

## Linux

Install dependencies:

### Arch Linux

```bash
sudo pacman -S xdotool xprintidle libnotify
```

### Debian / Ubuntu

```bash
sudo apt install xdotool xprintidle libnotify-bin
```

Required:

* Java 17+
* `xdotool`
* `xprintidle`
* `notify-send`
* `pactl` or `wpctl`

---

## Windows

Required:

* Java 17+
* PowerShell

---

# Setup

Create `.env` in the project root:

```env
GROQ_API_KEY=gsk_your_key_here
```

Get a free API key:

https://console.groq.com

---

# Build

## Linux

```bash
mkdir -p out
javac -d out src/**/*.java
jar cfe DaemonV.jar Main -C out .
```

## Windows

```powershell
.\build.bat
```

---

# Running

## Production Mode

```bash
java -jar DaemonV.jar
```

---

## Debug Mode

```bash
java -jar DaemonV.jar --debug
```

Debug mode:

* triggers every 5 seconds
* bypasses silent windows
* runs diagnostics

---

# Running In Background

## Linux

```bash
nohup java -jar DaemonV.jar &
```

---

# Stop Daemon

## Linux

```bash
pkill -f DaemonV
```

---

# CLI Flags

| Flag                 | Description              |
| -------------------- | ------------------------ |
| `--menu`             | Open remote control menu |
| `--debug`            | Fast debug intervals     |
| `--ask`              | Force immediate trigger  |
| `--silent <minutes>` | Temporary silence        |

---

# Remote Control

DaemonV runs a TCP socket server on:

```text
localhost:9333
```

---

## Commands

### Status

```text
STATUS
```

### Force Trigger

```text
TRIGGER
```

### Temporary Silence

```text
SILENT 60
```

### Change Interval

```text
INTERVAL 120
```

### Shutdown

```text
EXIT
```

---

# systemd Autostart

Create:

```text
~/.config/systemd/user/daemonv.service
```

Contents:

```ini
[Unit]
Description=DaemonV

[Service]
ExecStart=/usr/bin/java -jar /path/to/DaemonV.jar
Restart=always

[Install]
WantedBy=default.target
```

Enable:

```bash
systemctl --user enable daemonv
systemctl --user start daemonv
```

---

# Troubleshooting

## No Notifications

Ensure:

* `notify-send` exists
* desktop notifications are enabled

Test manually:

```bash
notify-send "DaemonV" "test"
```

---

## Active Window Detection Broken

Ensure:

* you are using X11
* `xdotool` is installed

Wayland support is currently experimental.

---

## No AI Messages

Check:

* internet connection
* `.env` exists
* API key is valid

DaemonV automatically falls back to local messages if AI fails.

---

# Log Files

DaemonV stores logs in:

```text
dataset.csv
```

Fields:

* timestamp
* idle time
* active window
* state
* generated message

