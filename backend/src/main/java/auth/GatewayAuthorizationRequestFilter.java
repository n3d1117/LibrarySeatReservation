package auth;

import model.Role;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@GatewayAuthorizationRequired
@Provider
@Priority(Priorities.AUTHORIZATION)
public class GatewayAuthorizationRequestFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(GatewayAuthorizationRequestFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Skip gateway token check for users with ADMIN privileges
        if (requestContext.getSecurityContext().isUserInRole(Role.ADMIN.toString()))
            return;

        // Get gateway token from the URL's query parameter
        String token = requestContext.getUriInfo().getQueryParameters().getFirst("gateway_token");
        if (token == null || token.isEmpty()) {
            LOGGER.warning(
                    String.format("Missing Gateway token in query: %s", requestContext.getUriInfo().getQueryParameters())
            );
            abortWithUnauthorized(requestContext);
            return;
        }

        try {
            // Validate token using key shared with Gateway
            if (!JWTHandler.validateWithGateway(token))
                abortWithUnauthorized(requestContext);

        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("Unauthorized")
                        .type("text/plain")
                        .build()
        );
    }
}
