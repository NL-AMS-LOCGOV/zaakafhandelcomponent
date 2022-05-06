/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.io.IOException;
import java.util.logging.Logger;

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

import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;

@WebFilter(filterName = "UserPrincipalFilter")
public class UserPrincipalFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(UserPrincipalFilter.class.getName());

    private static final String GROUP_MEMBERSHIP_CLAIM_NAME = "group_membership";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            final OidcPrincipal principal = (OidcPrincipal) httpServletRequest.getUserPrincipal();

            if (principal != null) {
                HttpSession httpSession = httpServletRequest.getSession(true);
                Medewerker ingelogdeMedewerker = SecurityUtil.getIngelogdeMedewerker(httpSession);

                if (ingelogdeMedewerker != null && !ingelogdeMedewerker.getGebruikersnaam().equals(principal.getName())) {
                    LOG.info(String.format("HTTP session of medewerker '%s' on context path %s is invalidated", ingelogdeMedewerker.getGebruikersnaam(),
                                           httpServletRequest.getServletContext().getContextPath()));
                    ingelogdeMedewerker = null;
                    httpSession.invalidate();
                    httpSession = httpServletRequest.getSession(true);
                }

                if (ingelogdeMedewerker == null) {
                    ingelogdeMedewerker = createMedewerker(principal.getOidcSecurityContext().getIDToken());
                    SecurityUtil.setIngelogdeMedewerker(httpSession, ingelogdeMedewerker);
                    LOG.info(String.format("Medewerker '%s' logged in on context path %s", ingelogdeMedewerker.getGebruikersnaam(),
                                           httpServletRequest.getServletContext().getContextPath()));
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public Medewerker createMedewerker(final IDToken idToken) {
        final User user = new User(idToken.getPreferredUsername(), idToken.getGivenName(), idToken.getFamilyName(), idToken.getName(), idToken.getEmail());
        return new Medewerker(user, idToken.getStringListClaimValue(GROUP_MEMBERSHIP_CLAIM_NAME).stream().map(groupId -> new Group(groupId, null)).toList());
    }
}
