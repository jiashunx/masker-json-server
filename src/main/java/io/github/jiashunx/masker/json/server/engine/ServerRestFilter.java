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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        String content = "";
        try {
            TbRestService tbRestService = restContext.getTbRestService();
            List<String> restUrlList = tbRestService.queryByServerId(serverId).stream().map(TbRest::getRestUrl).collect(Collectors.toList());
            if (restUrlList.contains(requestUrl)) {
                List<TbRest> restList = tbRestService.queryByServerIdAndRestUrl(serverId, requestUrl);
                if (restList != null && !restList.isEmpty()) {
                    TbRest defaultRest = null;
                    for (TbRest rest: restList) {
                        if (StringUtils.isBlank(rest.getExpression())) {
                            defaultRest = rest;
                            continue;
                        }
                        Map<String, Object> env = null;
                        try {
                            env = buildAviatorEnv(request, rest.getExpression());
                            Boolean result = (Boolean) AviatorEvaluator.compile(rest.getExpression()).execute(env);
                            if (result) {
                                content = rest.getRestBody();
                                break;
                            }
                        } catch (Throwable throwable) {
                            logger.error("路由表达式处理异常，Rest接口实例ID：{}，Rest接口URL：{}，aviator表达式：{}，aviator表达式传递env参数：{}", rest.getRestId(), rest.getRestUrl(), rest.getExpression(), env, throwable);
                        }
                    }
                    if (defaultRest != null) {
                        content = defaultRest.getRestBody();
                    }
                }
            }
            if (StringUtils.isEmpty(content)) {
                content = MRestSerializer.objectToJson(RestResult.failWithMessage(String.format("当前Server处理Context[%s]未配置请求URL[%s]的JSON响应报文，请配置后重试！", request.getContextPath(), requestUrl)), true);
            }
        } catch (Throwable throwable) {
            logger.error("URL[{}]匹配处理异常", requestUrl, throwable);
        }
        if (StringUtils.isNotEmpty(content)) {
            response.writeJSON(content.getBytes(StandardCharsets.UTF_8));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Map<String, Object> buildAviatorEnv(MRestRequest request, String expression) {
        Map<String, Object> env = new HashMap<>();
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
