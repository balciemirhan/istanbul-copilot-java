package com.istanbulmetre.copilot.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Spring-managed request-scoped bean to hold tool execution results
 * during the lifecycle of a single HTTP Request thread.
 */
@Component
@RequestScope
@Data
public class CopilotRequestContext {
    private Map<String, Object> chartData;
    private List<Map<String, Object>> dbResults;
}
