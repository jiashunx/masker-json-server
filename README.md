
### masker-json-server

Web应用：基于 [masker-rest][1] 实现的简易json server（供前端开发使用，用来mock后端接口及报文）

- 主要功能

   - 配置启动http server，可在运行时动态启停http服务（监听不同端口）

   - 为http server动态配置响应json格式数据的接口

   - 支持根据接口参数（method、headers、params、body）来通过aviator表达式匹配返回不同json格式数据（例如：分页查询接口，根据页码返回不同页数据）

      - method：http method（例如：get、post、put、delete等）

      - headers：请求头（从http请求头获取请求header）

         - 注意：headers参数对象下的元素为key-value键值对，value值类型为字符串

      - params：请求接口URL参数

         - 注意：params参数对象下的元素为key-value键值对，value值类型为字符串

      - body：请求body对象（http请求body，格式：json）

         - 注意：body对象下的元素为key-value键值对，value值类型为json数据的各类值类型

      - 路由表达式的结果应为布尔值，含义为路由表达式通过则匹配返回相应接口报文

      - 路由表达式样例：method == "post" && headers.key1 == "value1" && params.key2 == "value2" && body.key3 == "value3" && body.key5 == 5

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

- Docker部署文档

   - 构建可执行jar包（输出服务包名：json-server.jar）

   - Docker宿主机创建目录

   ```text
   # 构建&日志&数据存储目录
   mkdir -p /app/docker/json-server/{build,logs,workspace}
   ```

   - 拷贝 [Dockerfile](./Dockerfile) 及可执行jar包，上传至安装Docker的主机，存放目录：<b>/app/docker/json-server/build</b>

      - 备注：从容器访问宿主机，使用默认桥接模式访问主机（ip addr show docker0，可查看主机上Docker IP，默认：172.17.0.1）

   - 执行镜像构建命令：<b>docker build -t json-server:${version} ./</b>

      - 镜像名称：json-server

      - 镜像版本：${version}

      - Dockerfile文件名称：参数<b>-f Dockerfile</b>，若不指定"-f"参数默认找"Dockerfile"

      - Dockerfile文件存放目录：<b>./</b>

      - 镜像构建完成后可查看镜像：<b>docker images</b>

   - 至此，Docker镜像已构建完成，以下为部署配置+启动步骤

   - 启动json-server容器命令

   ```text
   docker run -itd \
   -v /app/docker/json-server/logs:/app/json-server/logs \
   -v /app/docker/json-server/workspace:/app/json-server/workspace \
   --net=host \
   --restart=always \
   --name json-server \
   json-server:${version}
   
   # json-server服务运行时涉及http服务的动态启停，在运行时需容器暴露的服务端口会动态变化
   # 因此不使用"-p "参数来进行端口映射，而是使用"--net=host"来与宿主机共享网络
   # Dockerfile中启动入口指定了json-server的服务端口18080
   ```

   - 访问json-server服务：http://宿主机IP:18080

[0]: https://layui.dev
[1]: https://github.com/jiashunx/masker-rest
[2]: https://github.com/jiashunx/sdk-sqlite3
