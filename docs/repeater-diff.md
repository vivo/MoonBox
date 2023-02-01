# **MoonBox和Jvm-Sandbox-Repeater对比差异点**

Moonbox（月光宝盒）是[JVM-Sandbox](https://github.com/alibaba/jvm-sandbox-repeater)生态下的，基于[jvm-sandbox-repeater](https://github.com/alibaba/jvm-sandbox-repeater)开发的一款流量回放平台产品，和jvm-sandbox-repeater差异如下:

#### 差异对比

| 功能点      | jvm-sandbox-repeater         | moonbox(月光宝盒)                                    |
| ------------- | ----------------- | --------------------------------------- |
| 部署方式         | 前后端不分离，支持本地部署                | 前后端分离，支持本地和远程部署                          |
| 插件数量 | 数量较少 | 数量多，基本覆盖常见中间件                 |
| agent稳定性  | 有部分bug待修复                | 经过vivo内部大量线上系统使用考验 |
| 数据储存  | 基于本地和mysql储存                | 使用ES储存，支持较大规模储存 |
| 平台易用性  | 功能较少                | 基于任务、接口维度管理录制、回放数据，拥有各种配置功能，易用性好 |
| 功能完备性  | 更多功能待支持                 | 支持agent文件管理、任务管理、Mock管理、回放对比配置等各项能力 |