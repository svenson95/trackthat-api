package com.svenson95.trackthat_api.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("API activity filter")
class ApiActivityFilterTest {

  @Mock
  private ApiActivityTracker activityTracker;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private ApiActivityFilter filter;

  @BeforeEach
  void setUp() {
    filter = new ApiActivityFilter(activityTracker);
  }

  @Test
  @DisplayName("tracks API requests")
  void tracksApiRequest() throws ServletException, IOException {
    // The method doFilterInternal(HttpServletRequest, HttpServletResponse,
    // FilterChain) from the type ApiActivityFilter is not visible
    filter.doFilterInternal(request, response, filterChain);

    InOrder inOrder = inOrder(activityTracker, filterChain);

    inOrder.verify(activityTracker).requestStarted();
    inOrder.verify(filterChain).doFilter(request, response);
    inOrder.verify(activityTracker).requestFinished();
  }

  @Test
  @DisplayName("finishes tracking when the filter chain throws")
  void finishesTrackingWhenFilterChainThrows() throws Exception {
    ServletException exception = new ServletException("Test");

    doThrow(exception).when(filterChain).doFilter(request, response);

    // The method doFilterInternal(HttpServletRequest, HttpServletResponse,
    // FilterChain) from the type ApiActivityFilter is not visible
    assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
        .isSameAs(exception);

    verify(activityTracker).requestStarted();
    verify(activityTracker).requestFinished();
  }

  @Test
  @DisplayName("skips the health endpoint")
  void skipsHealthEndpoint() {
    when(request.getRequestURI()).thenReturn("/health");

    assertThat(filter.shouldNotFilter(request)).isTrue();
  }

  @Test
  @DisplayName("skips non-API endpoints")
  void skipsNonApiEndpoint() {
    when(request.getRequestURI()).thenReturn("/");

    assertThat(filter.shouldNotFilter(request)).isTrue();
  }

  @Test
  @DisplayName("tracks API endpoints")
  void tracksApiEndpoint() {
    when(request.getRequestURI()).thenReturn("/workouts");

    assertThat(filter.shouldNotFilter(request)).isFalse();
  }
}
