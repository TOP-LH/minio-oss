#!/bin/sh
# author lihao3
# ./wison-oss.sh start 启动
# ./wison-oss.sh stop 停止
# ./wison-oss.sh restart 重启
# ./wison-oss.sh status 状态
SERVER_NAME=wison-wpos
APP_NAME=wpos-server.jar
APP_HOME=/www/server/wison-wpos
LOG_PATH=$APP_HOME/logs
JVM_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_PATH -Xms1g -Xmx1g"

if [ "$1" = "" ]; then
  echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
  exit 1
fi

if [ "$APP_NAME" = "" ]; then
  echo -e "\033[0;31m 未输入应用名 \033[0m"
  exit 1
fi

# shellcheck disable=SC2112
function start() {
  PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | awk '{print $2}')

  if [ x"$PID" != x"" ]; then
    echo "$APP_NAME is running..."
  else
    export DISPLAY=:0
    nohup java $JVM_OPTS -jar $APP_HOME/$APP_NAME > /dev/null 2>&1 &
    echo "Start $APP_NAME success..."
  fi
}

# shellcheck disable=SC2112
function stop() {
  echo "Stop $APP_NAME"

  PID=""
  query() {
    PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | awk '{print $2}')
  }

  query
  if [ x"$PID" != x"" ]; then
    kill -9 $PID
    echo "$APP_NAME (pid:$PID) exiting..."
    while [ x"$PID" != x"" ]; do
      sleep 1
      query
    done
    echo "$APP_NAME exited."
  else
    echo "$APP_NAME already stopped."
  fi
}

# shellcheck disable=SC2112
function restart() {
  stop
  sleep 2
  start
}

# shellcheck disable=SC2112
function status() {
  PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | wc -l)
  if [ $PID != 0 ]; then
    echo "$APP_NAME is running..."
  else
    echo "$APP_NAME is not running..."
  fi
}

case $1 in
start)
  start
  ;;
stop)
  stop
  ;;
restart)
  restart
  ;;
status)
  status
  ;;
*) ;;

esac
