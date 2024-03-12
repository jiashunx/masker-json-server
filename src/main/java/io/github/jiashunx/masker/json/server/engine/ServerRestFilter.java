package io.github.jiashunx.masker.json.server.engine;

import com.googlecode.aviator.AviatorEvaluator;
import io.github.jiashunx.masker.json.server.context.RestContext;
import io.github.jiashunx.masker.json.server.model.RestResult;
import io.github.jiashunx.masker.json.server.model.TbRest;
import io.github.jiashunx.masker.json.server.service.TbRestService;
import io.github.jiashunx.masker.rest.framework.MRestRequest;
import io.github.jiashunx.masker.rest.framework.MRestResponse;
import io.github.jiashunx.masker.rest.framework.filter.MRestFilter;
import io.github.jiashunx.masker.rest.framework.filter.MRestFilterChain;
import io.github.jiashunx.masker.rest.framework.serialize.MRestSerializer;
import io.github.jiashunx.masker.rest.framework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author jiashunx
 */
public class ServerRestFilter implements MRestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ServerRestFilter.class);

    private final String serverId;
    private final RestContext restContext;

    public ServerRestFilter(String serverId, RestContext restContext) {
        this.serverId = Objects.requireNonNull(serverId);
        this.restContext = Objects.requireNonNull(restContext);
    }

    @Override
    public void doFilter(MRestRequest request, MRestResponse response, MRestFilterChain filterChain) {
        String requestUrl = request.getUrl();
        TbRestService tbRestService = restContext.getTbRestService();
        List<TbRest> restList = tbRestService.queryByServerIdAndRestUrl(serverId, requestUrl);
        TbRest targetRest = null;
        if (restList != null && !restList.isEmpty()) {
            TbRest defaultRest = null;
            for (TbRest rest: restList) {
                // 获取默认无aviator路由表达式的配置
                if (StringUtils.isBlank(rest.getExpression())) {
                    defaultRest = rest;
                    continue;
                }
                // aviator路由表达式匹配处理（忽略报错直至匹配成功）
                Map<String, Object> env = null;
                try {
                    env = buildAviatorEnv(request, rest.getExpression());
                    Boolean result = (Boolean) AviatorEvaluator.compile(rest.getExpression()).execute(env);
                    if (result) {
                        targetRest = rest;
                        break;
                    }
                } catch (Throwable throwable) {
                    logger.error("路由表达式处理异常，Rest接口实例ID：{}，Rest接口URL：{}，aviator表达式：{}，aviator表达式传递env参数：{}", rest.getRestId(), rest.getRestUrl(), rest.getExpression(), env, throwable);
                }
            }
            // 若存在默认接口配置则返回相应响应内容
            if (targetRest == null && defaultRest != null) {
                targetRest = defaultRest;
            }
        }
        // 未配置目标url则返回固定报错
        if (targetRest == null) {
            String content = MRestSerializer.objectToJson(RestResult.failWithMessage(String.format("当前Server处理Context[%s]未配置请求URL[%s]的JSON响应报文，请配置后重试！", request.getContextPath(), requestUrl)), true);
            response.writeJSON(content.getBytes(StandardCharsets.UTF_8));
            return;
        }
        // 返回配置的json响应
        if (!targetRest.isProxyEnabled()) {
            response.writeJSON(targetRest.getRestBody().getBytes(StandardCharsets.UTF_8));
            return;
        }
        // 代理请求
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(5))
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .cookieHandler(new CookieManager())
                    .build();
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .method(request.getMethod().name(), HttpRequest.BodyPublishers.ofByteArray(request.getBodyBytes()))
                    .uri(URI.create(targetRest.getProxyServer() + request.getOriginUrl()));
            request.getHeaderKeys().forEach(k -> {
                httpRequestBuilder.header(k, request.getHeader(k));
            });
            HttpRequest httpRequest = httpRequestBuilder.build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException(String.format("代理请求响应异常，响应码：%d，响应信息：%s", httpResponse.statusCode(), httpResponse.body()));
            }
            httpResponse.headers().map().forEach((k, vl) -> {
                vl.forEach(v -> {
                    response.setHeader(k, v);
                });
            });
            response.writeString(httpResponse.body());
        } catch (Throwable throwable) {
            response.writeJSON(MRestSerializer.objectToJsonBytes(RestResult.failWithMessage(StringUtils.getExceptionStackTrace(throwable)), true));
        }
    }

    private Map<String, Object> buildAviatorEnv(MRestRequest request, String expression) {
        Map<String, Object> env = new HashMap<>();
        env.put("method", request.getMethod().name().toLowerCase());
        if (expression.contains("headers.")) {
            env.put("headers", getRequestHeaders(request));
        }
        if (expression.contains("params.")) {
            env.put("params", getRequestParams(request));
        }
        if (expression.contains("body.")) {
            env.put("body", getRequestBody(request));
        }
        return env;
    }

    private Map<String, Object> getRequestHeaders(MRestRequest request) {
        Map<String, Object> headers = new HashMap<>();
        for (String k: request.getHeaderKeys()) {
            headers.put(k, request.getHeader(k));
        }
        return headers;
    }

    private Map<String, Object> getRequestParams(MRestRequest request) {
        Map<String, Object> params = new HashMap<>();
        request.getParameters().forEach(params::put);
        return params;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getRequestBody(MRestRequest request) {
        Map<String, Object> body = new HashMap<>();
        if (request.getBodyBytes() != null && request.getBodyBytes().length > 0) {
            body = MRestSerializer.jsonToObj(request.getBodyBytes(), Map.class);
        }
        return body;
    }

}
