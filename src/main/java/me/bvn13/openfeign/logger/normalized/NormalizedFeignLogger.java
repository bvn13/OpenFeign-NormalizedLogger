package me.bvn13.openfeign.logger.normalized;

import feign.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OpenFeign Logger
 * combines request and response part into single log entry:
 * <pre>
 * {@code
 *
 * normalized feign request (HERE-IS-CLASS-AND-METHOD): [
 *
 * ] has response [
 *
 * ]
 * }
 * </pre>
 */
public class NormalizedFeignLogger extends feign.Logger {

    private static final Logger log = LoggerFactory.getLogger(NormalizedFeignLogger.class);

    private final ThreadLocal<Map<String, String>> methodName;
    private final ThreadLocal<Map<String, List<String>>> logsRequest;
    private final ThreadLocal<Map<String, List<String>>> logsResponse;
    private final ThreadLocal<Map<String, Boolean>> isResponse;

    public NormalizedFeignLogger() {
        methodName = new ThreadLocal<>();
        isResponse = new ThreadLocal<>();
        logsRequest = new ThreadLocal<>();
        logsResponse = new ThreadLocal<>();
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        init();
        super.logRequest(configKey, logLevel, request);
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        if (format.startsWith("--->") && !format.startsWith("---> END")) {
            // the very beginning
            clean(configKey);
        }
        if (!isResponse.get().getOrDefault(configKey, false)) {
            log(logsRequest, configKey, format, args);
        } else {
            log(logsResponse, configKey, format, args);
            if (format.startsWith("<--- END")) {
                showLogs(configKey);
            }
        }
        if (format.startsWith("---> END")) {
            isResponse.get().put(configKey, true);
        }
    }

    private void init() {
        if (isResponse.get() == null) {
            isResponse.set(new ConcurrentHashMap<>());
        }
        if (methodName.get() == null) {
            methodName.set(new ConcurrentHashMap<>());
        }
        if (logsRequest.get() == null) {
            logsRequest.set(new ConcurrentHashMap<>());
        }
        if (logsResponse.get() == null) {
            logsResponse.set(new ConcurrentHashMap<>());
        }
    }

    private void clean(String configKey) {
        isResponse.get().put(configKey, false);
        methodName.get().put(configKey, methodTag(configKey));
        logsRequest.get().put(configKey, new LinkedList<>());
        logsResponse.get().put(configKey, new LinkedList<>());
    }

    private void log(ThreadLocal<Map<String, List<String>>> container, String configKey, String format, Object... args) {
        extractList(container, configKey)
                .add(String.format(format, args));
    }

    private void showLogs(String configKey) {
        log.info("Normalized feign request " + methodName.get() + ": [\n" +
                collectionToDelimitedString(logsRequest.get().getOrDefault(configKey, Collections.emptyList()), "\n") +
                "\n] has response [\n" +
                collectionToDelimitedString(logsResponse.get().getOrDefault(configKey, Collections.emptyList()), "\n") +
                "\n]");
    }

    private List<String> extractList(ThreadLocal<Map<String, List<String>>> container, String configKey) {
        return container.get().get(configKey);
    }

    private String collectionToDelimitedString(Collection<String> collection, String delimeter) {
        final StringBuilder sb = new StringBuilder();
        final Iterator<String> iter = collection.iterator();
        int i = 0;
        while (iter.hasNext()) {
            if (i++ > 0) {
                sb.append(delimeter);
            }
            sb.append(iter.next());
        }
        return sb.toString();
    }
}
