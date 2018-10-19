# sob

Smack Operator Bot, a Smack based XMPP bot for system operators.

_Project is under development._


### 使用指南：

1. 下载 openfire 并在本机启动（数据库使用内置，服务器域名设置为 localhost）；
1. 创建三个用户： admin@localhost, user1@localhost, bot@localhost；
1. 将 bot 的用户名和密码，以及管理员的用户名配置到 application.properties 里面；
1. 启动本项目，用聊天工具以 admin@localhost 或 user1@localhost 的身份登录，并向 bot@localhost 发送消息。

### 目前的开发进度：

已经在我的生产环境上部署了，当某个业务发生变更时，我就会收到消息。接下来是调整业务方面，哪些消息要推送提醒，哪些不要，这些都跟 sob 项目没什么关系了。sub 项目进入开发的低谷阶段。

### 将来会开发的功能：

* 管理员将能够进行权限方面的配置，允许哪些用户收到哪些消息推送；
* 普通用户能够对消息推送设置过滤条件，只看到自己想看到的推送信息；
* 满足上面两个场景的推送消息，是一个什么样的格式，还在设想当中。
