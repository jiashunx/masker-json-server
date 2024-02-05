# 使用JDK11启动
FROM openjdk:11-jre-slim

# 声明运行时提供服务的端口
EXPOSE 18080

# 拷贝fatjar
COPY json-server.jar /app/json-server/json-server.jar

# 容器启动入口
ENTRYPOINT ["java","-server","-Xms64m","-Xmx64m","-XX:MetaspaceSize=64M","-XX:MaxMetaspaceSize=64M","-Xlog:gc*,gc+ref=debug,gc+age=trace,gc+heap=debug:file=/app/json-server/logs/gc%p%t.log:tags,uptime,time:filecount=10,filesize=10m","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/app/json-server/logs/heapdump.hprof","-Dlog.path=/app/json-server/logs","-Dlog.root.level=info","-jar","/app/json-server/json-server.jar","-p","18080","-w","/app/json-server/workspace"]
