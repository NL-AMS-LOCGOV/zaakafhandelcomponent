/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.wildfly.security.http.oidc.IDToken;
import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@WebFilter(filterName = "UserPrincipalFilter")
public class UserPrincipalFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(UserPrincipalFilter.class.getName());

    private static final String ROL_DOMEIN_ELK_ZAAKTYPE = "domein_elk_zaaktype";

    private static final String GROUP_MEMBERSHIP_CLAIM_NAME = "group_membership";

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws ServletException, IOException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            final OidcPrincipal principal = (OidcPrincipal) httpServletRequest.getUserPrincipal();

            if (principal != null) {
                HttpSession httpSession = httpServletRequest.getSession(true);
                LoggedInUser loggedInUser = SecurityUtil.getLoggedInUser(httpSession);
                if (loggedInUser != null && !loggedInUser.getId().equals(principal.getName())) {
                    LOG.info(String.format("HTTP session of user '%s' on context path %s is invalidated",
                            loggedInUser.getId(), httpServletRequest.getServletContext().getContextPath()));
                    httpSession.invalidate();
                    loggedInUser = null;
                    httpSession = httpServletRequest.getSession(true);
                }

                if (loggedInUser == null) {
                    loggedInUser = createLoggedInUser(principal.getOidcSecurityContext());
                    SecurityUtil.setLoggedInUser(httpSession, loggedInUser);
                    LOG.info(String.format("User logged in: '%s' with roles: %s, groups: %s en zaaktypen: %s",
                                           loggedInUser.getId(),
                                           loggedInUser.getRoles(), loggedInUser.getGroupIds(),
                                           loggedInUser.isGeautoriseerdVoorAlleZaaktypen() ? "ELK-ZAAKTYPE" :
                                                   loggedInUser.getGeautoriseerdeZaaktypen()));
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private LoggedInUser createLoggedInUser(final OidcSecurityContext context) {
        final IDToken idToken = context.getIDToken();
        final Set<String> roles = Set.copyOf(context.getToken().getRealmAccessClaim().getRoles());
        return new LoggedInUser(idToken.getPreferredUsername(),
                idToken.getGivenName(),
                idToken.getFamilyName(),
                idToken.getName(),
                idToken.getEmail(),
                roles,
                Set.copyOf(idToken.getStringListClaimValue(GROUP_MEMBERSHIP_CLAIM_NAME)),
                getGeautoriseerdeZaaktypen(roles));
    }

    private Set<String> getGeautoriseerdeZaaktypen(final Set<String> roles) {
        if (roles.contains(ROL_DOMEIN_ELK_ZAAKTYPE)) {
            return null;
        } else {
            return zaakafhandelParameterService.listZaakafhandelParameters().stream()
                    .filter(zaakafhandelParameters -> zaakafhandelParameters.getDomein() != null &&
                            roles.contains(zaakafhandelParameters.getDomein()))
                    .map(ZaakafhandelParameters::getZaaktypeOmschrijving)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }
}
