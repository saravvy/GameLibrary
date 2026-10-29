package app.routes;

import io.javalin.security.RouteRole;

public enum AppRouteRole implements RouteRole {
    ANYONE,
    USER,
    ADMIN
}