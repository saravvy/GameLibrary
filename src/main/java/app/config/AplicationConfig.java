package app.config;

import app.controllers.SecurityController;
import app.exceptions.ApiException;
import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AplicationConfig {

    private final Routes routes;
    private static final Logger logger =
            LoggerFactory.getLogger(AplicationConfig.class);

    private final SecurityController securityController;

    public AplicationConfig(Routes routes, SecurityController securityController){
        this.routes = routes;
        this.securityController = securityController;
    }



    public void startServer(int port){

        Javalin.create(config -> {
            config.router.contextPath = "/api";
            config.routes.apiBuilder(routes.getRoutes());
            config.bundledPlugins.enableRouteOverview("/routes");
       config.routes.exception(ValidationException.class, AplicationConfig::validationExceptionHandler);
         config.routes.exception(ApiException.class, AplicationConfig::apiExceptionHandler);
         config.routes.exception(Exception.class, AplicationConfig::exceptionHandler);
            config.routes.beforeMatched(ctx -> securityController.authenticate(ctx));
            config.routes.beforeMatched(ctx -> securityController.authorize(ctx));
        }).start(port);

    }

    private static void apiExceptionHandler(ApiException e, Context ctx){

        logger.error(e.getMessage());
        int statusCode = e.getStatusCode();
        ctx.status(statusCode);
        ctx.json(e.getMessage());


    }
    private static void exceptionHandler(Exception e, Context ctx){

        logger.error("Validation failed", e);
        ctx.status(500).json(e.getMessage());


    }
    private static void validationExceptionHandler(ValidationException e, Context ctx){

        logger.error(e.getMessage());
        ctx.status(400).json(e.getMessage());


    }

}
