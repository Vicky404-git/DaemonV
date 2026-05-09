#!/bin/bash

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

# Start daemon only if not already running
if ! ss -tuln | grep -q ":9333 "; then
  nohup java -jar DaemonV.jar --daemon >daemon.log 2>&1 &
  echo "DaemonV started."
else
  echo "DaemonV already running."
fi
