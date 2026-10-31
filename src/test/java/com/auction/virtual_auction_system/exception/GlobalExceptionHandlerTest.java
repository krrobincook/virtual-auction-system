package com.auction.virtual_auction_system.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFoundToTheStandard404Response() {
        var response = handler.handleNotFound(
                new ResourceNotFoundException("Auction not found with id: 10"),
                request("/api/auctions/10"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(404);
            assertThat(error.getError()).isEqualTo("Not Found");
            assertThat(error.getMessage())
                    .isEqualTo("Auction not found with id: 10");
            assertThat(error.getPath()).isEqualTo("/api/auctions/10");
        });
    }

    @Test
    void mapsDuplicateResourceToConflict() {
        var response = handler.handleConflict(
                new DuplicateResourceException("Email already registered"),
                request("/api/auth/register"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage())
                .isEqualTo("Email already registered");
    }

    @Test
    void mapsAuthenticationFailureWithoutLeakingCredentials() {
        var response = handler.handleAuthentication(
                new BadCredentialsException("internal authentication detail"),
                request("/api/auth/login"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage())
                .isEqualTo("Invalid email or password");
    }

    @Test
    void returnsValidationErrorsByField() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "title", "Title is required"),
                new FieldError("request", "startingPrice",
                        "Starting price must be greater than 0")
        ));

        Method method = GlobalExceptionHandlerTest.class
                .getDeclaredMethod("validationTarget", String.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        new org.springframework.core.MethodParameter(method, 0),
                        bindingResult);

        var response = handler.handleValidation(
                exception,
                request("/api/auctions"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage())
                .isEqualTo("Validation failed");
        assertThat(response.getBody().getValidationErrors())
                .containsEntry("title", "Title is required")
                .containsEntry("startingPrice",
                        "Starting price must be greater than 0");
    }

    @Test
    void hidesUnexpectedExceptionDetails() {
        var response = handler.handleUnexpected(
                new RuntimeException("database password=secret"),
                request("/api/auctions"));

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage())
                .isEqualTo("An unexpected error occurred");
    }

    private static HttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI(path);
        return request;
    }

    @SuppressWarnings("unused")
    private void validationTarget(String value) {
    }
}
