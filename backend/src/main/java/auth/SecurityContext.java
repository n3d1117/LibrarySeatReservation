package auth;

import dao.UserDao;
import model.Role;
import model.User;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import java.security.Principal;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SecurityContext implements javax.ws.rs.core.SecurityContext {

    private static final Logger LOGGER = Logger.getLogger(SecurityContext.class.getName());

    @Inject private UserDao dao;
    private String principalUsername;
    private ContainerRequestContext context;

    public void setPrincipalUsername(String principalUsername) {
        this.principalUsername = principalUsername;
    }

    public void setContext(ContainerRequestContext context) {
        this.context = context;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> principalUsername;
    }

    @Override
    public boolean isUserInRole(String role) {
        LOGGER.info(String.format("Checking role for username %s...", principalUsername));
        try {
            User principal = dao.findByUsername(principalUsername);
            return principal.getRoles().stream()
                    .map(Role::toString)
                    .collect(Collectors.toList())
                    .contains(role);
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean isSecure() {
        return context.getSecurityContext().isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}
