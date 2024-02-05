
### masker-json-server

Web应用：基于 [masker-rest][1] 实现的简易json server（供前端开发使用，用来mock后端接口及报文）

- 主要功能

   - 配置启动http server，可在运行时动态启停http服务（监听不同端口）

   - 为http server动态配置响应json格式数据的接口

- 技术组件

   - 使用 [layui][0] 实现前端控制台

   - 使用 [masker-rest][1] 实现后端接口服务，动态启停http服务也依赖于此组件

   - 使用 [sdk-sqlite3][2] 实现存储，启动时初始化表结构到本地SQLite3数据库，无需额外部署数据库

- 运行配置

   - 运行环境：JDK11+

   - 启动参数

   ```text
   # 指定服务端口（不指定则默认8080）
   -p 18080
   --port 18080
   # 指定工作目录（不指定则默认当前程序运行目录）
   -w /app/json-server
   --workspace /app/json-server
   # 示例启动命令
   java -jar masker-json-server.jar -p 18080 -w /app/json-server
   ```

   - JVM参数（参考）

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
