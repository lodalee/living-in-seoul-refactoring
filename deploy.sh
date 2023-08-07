#!/bin/bash
 echo "> now ing app pid find!"
 CURRENT_PID=$(pgrep -f gist-competition)
 echo "$CURRENT_PID"
 if [ -z $CURRENT_PID ]; then
         echo "> no ing app."
 else
         echo "> kill -9 $CURRENT_PID"
         kill -9 $CURRENT_PID
         sleep 3
 fi
 echo "> new app deploy"

 cd /home/ubuntu/deploy
 JAR_NAME=$(ls | grep 'gist-competition' | tail -n 1)
 echo "> JAR Name: $JAR_NAME"
 
 # 현재 폴더에 nohup폴더 만들기 -p는 폴더가 없으면 만들고 있으면 안 만들고
 mkdir -p ./nohup  

 # nohup java -jar -Duser.timezone=Asia/Seoul $JAR_NAME &
 nohup java -jar -Duser.timezone=Asia/Seoul $JAR_NAME 1>nohup/stdout.txt 2>nohup/stderr.txt &
