package io.github.jiashunx.masker.json.server.engine;

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
import java.util.List;
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
                TbRest rest = tbRestService.queryByServerIdAndRestUrl(serverId, requestUrl);
                if (rest != null) {
                    content = rest.getRestBody();
                }
            }
            if (StringUtils.isEmpty(content)) {
                content = MRestSerializer.objectToJson(RestResult.failWithMessage(String.format("未配置当前请求URL[%s]的JSON响应报文，请配置后重试！", requestUrl)), true);
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

}
