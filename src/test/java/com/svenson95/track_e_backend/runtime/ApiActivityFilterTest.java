package com.svenson95.track_e_backend.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ApiActivityFilterTest {

  @Mock private ApiActivityTracker activityTracker;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  private ApiActivityFilter filter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    filter = new ApiActivityFilter(activityTracker);
  }

  @Test
  void shouldTrackApiRequest() throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);

    InOrder inOrder = inOrder(activityTracker, filterChain);

    inOrder.verify(activityTracker).requestStarted();
    inOrder.verify(filterChain).doFilter(request, response);
    inOrder.verify(activityTracker).requestFinished();
  }

  @Test
  void shouldFinishTrackingWhenFilterChainThrows() throws Exception {
    ServletException exception = new ServletException("Test");

    org.mockito.Mockito.doThrow(exception).when(filterChain).doFilter(request, response);

    assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
        .isSameAs(exception);

    verify(activityTracker).requestStarted();
    verify(activityTracker).requestFinished();
  }

  @Test
  void shouldSkipHealthEndpoint() {
    when(request.getRequestURI()).thenReturn("/api/health");

    assertThat(filter.shouldNotFilter(request)).isTrue();
  }

  @Test
  void shouldSkipNonApiEndpoint() {
    when(request.getRequestURI()).thenReturn("/");

    assertThat(filter.shouldNotFilter(request)).isTrue();
  }

  @Test
  void shouldTrackApiEndpoint() {
    when(request.getRequestURI()).thenReturn("/api/workouts");

    assertThat(filter.shouldNotFilter(request)).isFalse();
  }
}
