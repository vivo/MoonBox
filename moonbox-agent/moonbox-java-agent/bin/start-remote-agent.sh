#!/usr/bin/env bash

typeset SANDBOX_HOME=~/sandbox

typeset MODULE_HOME=~/.sandbox-module

# exit shell with err_code
# $1 : err_code
# $2 : err_msg
exit_on_err()
{
    [[ ! -z "${2}" ]] && echo "${2}" 1>&2
    exit ${1}
}

main(){
     app_name=$1
     task_run_config=$2
     echo `pwd`
     user=`ps aux  | grep $app_name | grep java | awk '{ print $1 }'`
     pid=`ps aux  | grep $app_name | grep java | awk '{ print $2 }'`
     echo "======                 ${app_name} PID is ${pid}  user is ${user}              ======";
     if [ ! $pid ]; then
       exit_on_err 1 "$app_name process pid not found,please check process start or jvm opts contains $app_name"
     fi
     run_user=`whoami`
     if [ "$user" == "$run_user" ]
     then
        echo "======                 ${app_name} run_user equals process start user     ======";
        sh  $SANDBOX_HOME/bin/sandbox.sh -p ${pid} -P 8820 -T $task_run_config
     else
        exit_on_err 1 "====== error for ${app_name} run_user $run_user not equals process start user $user   ======";
     fi
}

main $1 $2