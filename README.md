# masker-json-server
Web应用：基于masker-rest实现的简易json server（供前端开发使用，用来mock后端接口及报文）

- 技术组件
   - 使用 [layui][0] 实现前端
   - 使用 [masker-rest][1] 实现后端web服务，简化可执行jar包体积
   - 使用 [sdk-sqlite3][2] 实现后端存储，无需搭建数据库环境

- 主要功能：
   - 配置化启动web服务
   - 配置服务rest接口

- 运行环境：JDK11

- 启动参数

```text
# 指定服务端口（默认8080）
-p 18080
```

- JVM参数

```text
-Xms64m
-Xmx64m
-XX:MetaspaceSize=64M
-XX:MaxMetaspaceSize=64M
-XX:+DisableAttachMechanism
```

[0]: https://layui.dev
[1]: https://github.com/jiashunx/masker-rest
[2]: https://github.com/jiashunx/sdk-sqlite3
