#!/bin/bash

# Navigate to your project directory so it can find .env and dataset.csv
cd ~/Desktop/Projects/DaemonV

# Check if port 9333 is already in use
if ! ss -tuln | grep -q ":9333 "; then
  # Run Java invisibly in the background using nohup
  nohup java -Xmx32m -Xms16m -cp out Main >/dev/null 2>&1 &
fi
