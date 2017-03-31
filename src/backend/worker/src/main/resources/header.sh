#!/bin/bash


ANSI_RED="\033[31;1m"
ANSI_GREEN="\033[32;1m"
ANSI_RESET="\033[0m"
ANSI_CLEAR="\033[0K"

SIMPLECI_TEST_RESULT=
SIMPLECI_CMD=

function simpleci_cmd() {
  local assert output display timing cmd result

  cmd=$1
  SIMPLECI_CMD=$cmd
  shift

  while true; do
    case "$1" in
      --assert)  assert=true; shift ;;
      --echo)    output=true; shift ;;
      --timing)  timing=true; shift ;;
      *) break ;;
    esac
  done

  if [[ -n "$timing" ]]; then
    simpleci_time_start
  fi

  if [[ -n "$output" ]]; then
    echo "\$ ${cmd}"
  fi

  eval "$cmd"
  result=$?

  if [[ -n "$timing" ]]; then
    simpleci_time_finish
  fi

  if [[ -n "$assert" ]]; then
    simpleci_assert $result
  fi

  return $result
}

function simpleci_time_start() {
  simpleci_timer_id=$(printf %08x $(( RANDOM * RANDOM )))
  simpleci_start_time=$(simpleci_nanoseconds)
  echo "[simpleci_time:start:$simpleci_timer_id]"
}

function simpleci_time_finish() {
  local result=$?
  simpleci_end_time=$(simpleci_nanoseconds)
  local duration=$(($simpleci_end_time-$simpleci_start_time))
  echo "[simpleci_time:end:$simpleci_timer_id:start=$simpleci_start_time,finish=$simpleci_end_time,duration=$duration]"
  return $result
}

function simpleci_nanoseconds() {
  local cmd="date"
  local format="+%s%N"
  local os=$(uname)

  if hash gdate > /dev/null 2>&1; then
    cmd="gdate" # use gdate if available
  fi

  $cmd -u $format
}

function simpleci_assert() {
  local result=${1:-$?}
  if [ $result -ne 0 ]; then
    echo -e "\n${ANSI_RED}The command \"$SIMPLECI_CMD\" failed and exited with $result.${ANSI_RESET}\n\nYour build has been stopped."
    simpleci_terminate 2
  fi
}


function simpleci_result() {
  local result=$1
  export SIMPLECI_TEST_RESULT=$(( ${SIMPLECI_TEST_RESULT:-0} | $(($result != 0)) ))
  if [ $result -eq 0 ]; then
    echo -e "\n${ANSI_GREEN}The command \"${SIMPLECI_CMD}\" exited with $result.${ANSI_RESET}"
  else
    echo -e "\n${ANSI_RED}The command \"${SIMPLECI_CMD}\" exited with $result.${ANSI_RESET}"
  fi
}

function simpleci_terminate() {
  pkill -9 -P $$ >/dev/null 2>&1|| true
  exit $1
}




