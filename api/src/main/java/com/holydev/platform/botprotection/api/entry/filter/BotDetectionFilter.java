package com.holydev.platform.botprotection.api.entry.filter;

import com.holydev.platform.botprotection.application.usecase.AnalyzeRequestUseCase;
import com.holydev.platform.botprotection.domain.model.RequestContext;
import com.holydev.platform.botprotection.domain.decision.DetectionDecision;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BotDetectionFilter extends OncePerRequestFilter {

    private final AnalyzeRequestUseCase analyzeRequestUseCase;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. extract request → build context
        RequestContext context = RequestContext.builder()
                .ip(extractIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .path(request.getRequestURI())
                .method(request.getMethod())
                .headers(extractHeaders(request))
                .timestamp(System.currentTimeMillis())
                .build();

        // 2. call use case (stub)
        DetectionDecision decision = analyzeRequestUseCase.analyze(context);

        System.out.println("IP=" + context.getIp() + " Decision=" + decision);

        // 3. apply decision (hiện tại chỉ ALLOW)
        if (decision == DetectionDecision.BLOCK) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Blocked by bot protection");
            return;
        }

        if (decision == DetectionDecision.CHALLENGE) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Challenge required");
            return;
        }

        // ALLOW
        filterChain.doFilter(request, response);
    }

    private String extractIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        h -> h,
                        request::getHeader
                ));
    }
}
