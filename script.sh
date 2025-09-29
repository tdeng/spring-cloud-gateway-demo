#!/bin/bash
set -e  # exit on error

# Root directory (adjust if needed)
ROOT_DIR="$(pwd)"

# List of subdirectories
SUBDIRS=("gateway" "user-service" "byte-service")

# Command to run in each subdirectory
COMMAND="mvn package && cf push --no-start"

for dir in "${SUBDIRS[@]}"; do
  echo ">>> Entering $dir"
  pushd "$ROOT_DIR/$dir" > /dev/null
  
  # Run your command
  eval "$COMMAND"

  echo "<<< Leaving $dir" 
  popd > /dev/null
done

cf add-network-policy gateway user-service --protocol tcp --port 8080
cf add-network-policy gateway byte-service --protocol tcp --port 8080
cf start gateway
cf start user-service
cf start byte-service