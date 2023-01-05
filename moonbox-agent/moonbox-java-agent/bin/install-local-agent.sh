#!/usr/bin/env bash

PROJECT_HOME_DIR=../../../
LOCAL_AGENT_DIR=${PROJECT_HOME_DIR}/local-agent

SANDBOX_INSTALL_HOME=${HOME}
SANDBOX_INSTALL_LOCAL=${SANDBOX_INSTALL_HOME}/sandbox

REPEATER_TARGET_DIR=../target/.sandbox-module

# exit shell with err_code
# $1 : err_code
# $2 : err_msg
exit_on_err()
{
    [[ ! -z "${2}" ]] && echo "${2}" 1>&2
    exit ${1}
}

# install local sandbox
[ -d ${SANDBOX_INSTALL_HOME}/sandbox ] && rm -rf ${SANDBOX_INSTALL_HOME}/sandbox
tar -xzf ${LOCAL_AGENT_DIR}/sandbox-stable-bin.tar -C ${SANDBOX_INSTALL_HOME}/
chmod +x ${SANDBOX_INSTALL_LOCAL}/bin/sandbox.sh

# package repeater
mvn clean package -Dmaven.test.skip=true -f ../pom.xml || exit_on_err 1 "package repeater failed."
mkdir -p ${REPEATER_TARGET_DIR}/plugins
mkdir -p ${REPEATER_TARGET_DIR}/cfg
cp -r ../cfg/ ${REPEATER_TARGET_DIR} \
    && cp ../moonbox-module/target/moonbox-module-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/ \
    && cp ../moonbox-plugins/*/*-plugin/target/*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/ \
    && cp -r ../bin/ ${REPEATER_TARGET_DIR}

# install repeater
mkdir -p  ~/.sandbox-module/
cp -rf ../target/.sandbox-module/* ~/.sandbox-module/

# output zip file moobox-agent.zip
cd ../target/.sandbox-module
tar -zcf  monbox-agent.tar ../