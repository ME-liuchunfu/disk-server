FROM eclipse-temurin:21.0.9_10-jdk-noble

# 维护者信息
LABEL maintainer="429829320@qq.com"

# 设置环境变量，确保中文不会乱码
# 设置时区为上海
ENV TZ=Asia/Shanghai

ENV DEBIAN_FRONTEND=noninteractive

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装必要的软件包和设置支持，包括中文语言包
RUN apt-get update && apt-get install -y \
    apt-utils \
    procps \
    fontconfig \
    fonts-dejavu \
    strace \
    libc6 \
    libstdc++6 \
    locales \
    language-pack-zh-hans \
    && rm -rf /var/lib/apt/lists/*

# 设置语言环境为中文
RUN echo "zh_CN.UTF-8 UTF-8" >> /etc/locale.gen && \
    locale-gen && \
    update-locale LANG=zh_CN.UTF-8

ENV LANG=zh_CN.UTF-8 \
    LC_ALL=zh_CN.UTF-8 \
    LANGUAGE=zh_CN.UTF-8

# 设置工作目录
WORKDIR /home/app

COPY ./docker-enpoint.sh /home/app/

# 容器启动时执行的命令
ENTRYPOINT ["sh", "/home/app/docker-enpoint.sh"]


