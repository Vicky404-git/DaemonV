#!/bin/bash

# Navigate to the correct directory so it finds .env and dataset.csv
cd /home/vicky-404/Desktop/Projects/DaemonV

# Check if port 9333 is already in use (Daemon already running)
if ! ss -tuln | grep -q ":9333 "; then
  # Run Java in the background, ignoring terminal hangups
  nohup java -cp out Main >/dev/null 2>&1 &
fi
