package io.rapa.backendcrossing.common.constants;

public class EndPoints {
    public static final String[] POST_ANONYMOUS = {
            "/api/v1/auth/login", "/api/v1/users/register", "/api/v1/auth/google/**"
    };
    public static final String[] POST_PERMITALL ={
            "/api/v1/auth/refresh"
    };
    public static final String[] POST_AUTHENTICATED = {
            "/api/v1/auth/logout", "/api/v1/users/me/friends/requests/**",
            "/api/v1/users/me/inventory/pickup", "/api/v1/users/me/profile/**",
            "/api/v1/users/me/wallet/**", "/api/v1/users/me/npcs/**"

    };
    public static final String[] GET_ANONYMOUS ={
            "/api/v1/npcs/**", "/api/v1/auth/google/**"
    };
    public static final String[] GET_PERMITALL ={
            "/api/v1/items/**"
    };
    public static final String[] GET_AUTHENTICATED = {
            "/api/v1/users/me/**", "/api/v1/users/me/friends/**", "/api/v1/users/me/inventory"
    };
    public static final String[] PATCH_AUTHENTICATED = {
            "/api/v1/users/me/friends/requests/**"
    };
    public static final String[] DELETE_AUTHENTICATED = {
            "/api/v1/users/me/friends/**", "/api/v1/users/me/inventory/**",

    };
}
