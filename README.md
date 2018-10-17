# sob

Smack Operator Bot, a Smack based XMPP bot for system operators.

_Project is under development._


使用指南：

1. 下载 openfire 并在本机启动（数据库使用内置，服务器域名设置为 localhost）；
1. 创建三个用户： admin@localhost, user1@localhost, bot@localhost；
1. 将 bot 的用户名和密码，以及管理员的用户名配置到 application.properties 里面；
1. 启动本项目，用聊天工具以 admin@localhost 或 user1@localhost 的身份登录，并向 bot@localhost 发送消息。