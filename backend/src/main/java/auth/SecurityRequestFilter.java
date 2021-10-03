package auth;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityRequestFilter implements ContainerRequestFilter {

    @Inject
    SecurityContext securityContext;

    @Context
    private ResourceInfo resourceInfo;

    private static final Logger LOGGER = Logger.getLogger(SecurityRequestFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Get authentication header from the URL's query string
        String jwt = requestContext.getUriInfo().getQueryParameters().getFirst("jwt");
        if (jwt == null || jwt.isEmpty()) {
            LOGGER.warning(
                    String.format("Missing JWT token in query: %s", requestContext.getUriInfo().getQueryParameters())
            );
            abortWithUnauthorized(requestContext);
            return;
        }

        try {

            // Validate token
            if (!JWTHandler.validate(jwt)) {
                LOGGER.warning(String.format("JWT invalid: %s", jwt));
                abortWithUnauthorized(requestContext);
                return;
            }

            // Update security context with user email
            String email = JWTHandler.getSubject(jwt);
            securityContext.setPrincipalUsername(email);
            securityContext.setContext(requestContext);
            requestContext.setSecurityContext(securityContext);

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
