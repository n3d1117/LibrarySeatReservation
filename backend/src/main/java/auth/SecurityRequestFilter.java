package auth;

import dao.UserDao;

import javax.annotation.Priority;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityRequestFilter implements ContainerRequestFilter {

    @Inject
    UserDao userDao;

    @Inject
    SecurityContext securityContext;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        RolesAllowed classRolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        RolesAllowed methodRolesAllowed = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
        if (methodRolesAllowed == null && classRolesAllowed == null)
            return;

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").type("text/plain").build()
            );
            return;
        }

        String email = null;
        if (authorizationHeader.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            byte[] bytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(bytes, StandardCharsets.UTF_8);
            String[] tmp = credentials.split(":", 2);
            email = tmp[0];
            String password = tmp[1];
            if (!userDao.verify(email, password)) {
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").type("text/plain").build()
                );
                return;
            }
        }

        String principalUsername = email;
        securityContext.setPrincipalUsername(principalUsername);
        securityContext.setContext(requestContext);
        requestContext.setSecurityContext(securityContext);
    }
}
