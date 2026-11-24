package com.foodrescue.common.exception;

/** Single business/HTTP exception type. Deliberately framework-free; mapped to HTTP by GlobalExceptionHandler. */
public class ApiException extends RuntimeException {
    private final int status;
    private final String code;

    public ApiException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int getStatus() { return status; }
    public String getCode() { return code; }

    public static ApiException badRequest(String code, String message) { return new ApiException(400, code, message); }
    public static ApiException unauthorized(String code, String message) { return new ApiException(401, code, message); }
    public static ApiException forbidden(String message) { return new ApiException(403, "FORBIDDEN", message); }
    public static ApiException notFound(String what) { return new ApiException(404, "NOT_FOUND", what + " not found"); }
    public static ApiException conflict(String code, String message) { return new ApiException(409, code, message); }
}
