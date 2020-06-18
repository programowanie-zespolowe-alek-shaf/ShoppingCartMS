package pl.agh.shopping.card.application.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
public class AuthorizationService {

    public void checkAuthorization(String cartOwner) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object user = authentication.getPrincipal();
        if (cartOwner != null) {
            if (Objects.equals(user.toString(), cartOwner)) return;
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                if (Objects.equals(role, "ROLE_ADMIN"))
                    return;
            }
            throw new AccessDeniedException("user not authorised");
        }
    }
}
