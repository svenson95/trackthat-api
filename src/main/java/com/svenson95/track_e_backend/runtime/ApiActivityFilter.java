package com.svenson95.track_e_backend.runtime;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiActivityFilter extends OncePerRequestFilter {

  private static final String HEALTH_ENDPOINT = "/api/health";

  private final ApiActivityTracker activityTracker;

  public ApiActivityFilter(ApiActivityTracker activityTracker) {
    this.activityTracker = activityTracker;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    activityTracker.requestStarted();

    try {
      filterChain.doFilter(request, response);
    } finally {
      activityTracker.requestFinished();
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    return !path.startsWith("/api/") || HEALTH_ENDPOINT.equals(path);
  }
}
