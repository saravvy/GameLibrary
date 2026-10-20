package app.config;

import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.validation.ValidationException;

public class AplicationConfig {

    private final Routes routes;

    public AplicationConfig(Routes routes){
        this.routes = routes;

    }



    public void startServer(int port){

        Javalin.create(config -> {
            config.router.contextPath = "/api";
            config.routes.apiBuilder(routes.getRoutes());
            config.bundledPlugins.enableRouteOverview("/routes");
        //    config.routes.exception(ValidationException.class, ApplicationConfig::validationExceptionHandler);
          //  config.routes.exception(ApiException.class, ApplicationConfig::apiExceptionHandler);
            //config.routes.exception(Exception.class, ApplicationConfig::exceptionHandler);
        }).start(port);

    }
}
