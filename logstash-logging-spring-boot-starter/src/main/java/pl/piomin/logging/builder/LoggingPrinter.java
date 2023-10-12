package pl.piomin.logging.builder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import pl.piomin.logging.wrapper.SpringRequestWrapper;
import pl.piomin.logging.wrapper.SpringResponseWrapper;

import java.io.IOException;

import static net.logstash.logback.argument.StructuredArguments.value;

public class LoggingPrinter {
    private LoggingPrinter(boolean logHeaders, boolean ignorePayload, Logger logger) {
        this.logHeaders = logHeaders;
        this.ignorePayload = ignorePayload;
        this.logger = logger;
    }


    private boolean logHeaders;
    private boolean ignorePayload;

    private Logger logger;

    public void printRequest(SpringRequestWrapper wrappedRequest) throws IOException {
        if (logHeaders)
            if(ignorePayload)
                logger.info("Request: method={}, uri={}, headers={}, audit={}", wrappedRequest.getMethod(),
                        wrappedRequest.getRequestURI(), wrappedRequest.getAllHeaders(), value("audit", true));
            else
                logger.info("Request: method={}, uri={}, payload={}, headers={}, audit={}", wrappedRequest.getMethod(),
                        wrappedRequest.getRequestURI(), IOUtils.toString(wrappedRequest.getInputStream(),
                                wrappedRequest.getCharacterEncoding()), wrappedRequest.getAllHeaders(), value("audit", true));
        else
        if (ignorePayload)
            logger.info("Request: method={}, uri={}, audit={}", wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(), value("audit", true));
        else
            logger.info("Request: method={}, uri={}, payload={}, audit={}", wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(), IOUtils.toString(wrappedRequest.getInputStream(),
                            wrappedRequest.getCharacterEncoding()), value("audit", true));
    }
    public void printResponse(SpringResponseWrapper wrappedResponse,long duration,int overriddenStatus) {
        if (logHeaders)
            if (ignorePayload)
                logger.info("Response({} ms): status={}, payload={}, headers={}, audit={}", value("X-Response-Time", duration),
                        value("X-Response-Status", overriddenStatus), IOUtils.toString(wrappedResponse.getContentAsByteArray(),
                                wrappedResponse.getCharacterEncoding()), wrappedResponse.getAllHeaders(), value("audit", true));
            else
                logger.info("Response({} ms): status={}, headers={}, audit={}", value("X-Response-Time", duration),
                        value("X-Response-Status", overriddenStatus), wrappedResponse.getAllHeaders(), value("audit", true));
        else
        if (ignorePayload)
            logger.info("Response({} ms): status={}, audit={}", value("X-Response-Time", duration),
                    value("X-Response-Status", overriddenStatus), value("audit", true));
        else
            logger.info("Response({} ms): status={}, payload={}, audit={}", value("X-Response-Time", duration),
                    value("X-Response-Status", overriddenStatus),
                    IOUtils.toString(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding()), value("audit", true));
    }
    public static class Builder {
        private  boolean logHeaders;
        private  boolean ignorePayload;
        private  Logger logger;
        public  Builder showLogHeader(boolean showHeader) {
            this.logHeaders = showHeader;
            return this;
        }
        public  Builder ignorePayload(boolean ignorePayload) {
            this.ignorePayload = ignorePayload;
            return this;
        }

        public LoggingPrinter build(Logger logger) {
            return new LoggingPrinter(logHeaders,ignorePayload,logger);
        }
    }
}
