# Copyright 2022 vivo Communication Technology Co., Ltd.

# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

#    http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


FROM openjdk:8u342

MAINTAINER moonbox
EXPOSE 8080
LABEL version="1.0.1" description="moonbox springboot镜像" by="moonbox"

COPY moon-box-web /moonbox/
COPY moonbox-agent.tar /moonbox/moonbox-agent.tar
COPY sandbox-agent.tar /moonbox/sandbox-agent.tar
COPY moonbox-agent.tar /root/moonbox-agent-download/moonbox-agent-1.tar
COPY sandbox-agent.tar /root/moonbox-agent-download/sandbox-agent-1.tar

RUN sed -i s@/deb.debian.org/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN apt-get clean
RUN apt-get update

RUN apt-get install dos2unix
RUN apt-get install lsof

RUN apt-get install -y openssh-server
RUN echo "PermitRootLogin yes" >> /etc/ssh/sshd_config
RUN echo "root:123456" | chpasswd

CMD /etc/init.d/ssh start; cd /bin; rm sh; ln -s bash sh; java -jar /moonbox/moon-box-web
