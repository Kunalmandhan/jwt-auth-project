package com.example.jwtauth.dto;

public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public ApiResponse() {}
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private boolean success;
        private String message;
        private Object data;
        public Builder success(boolean s)   { this.success = s; return this; }
        public Builder message(String m)    { this.message = m; return this; }
        public Builder data(Object d)       { this.data = d; return this; }
        public ApiResponse build()          { return new ApiResponse(success, message, data); }
    }

    public boolean isSuccess()              { return success; }
    public String getMessage()              { return message; }
    public Object getData()                 { return data; }
}
