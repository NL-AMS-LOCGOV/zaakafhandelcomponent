/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import net.atos.zac.flowable.IdmService;

@WebFilter(filterName = "UserPrincipalFilter")
public class UserPrincipalFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(UserPrincipalFilter.class.getName());

    @EJB
    private IdmService idmService;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            final Principal principal = httpServletRequest.getUserPrincipal();

            if (principal != null) {
                HttpSession httpSession = httpServletRequest.getSession(true);
                Medewerker ingelogdeMedewerker = SecurityUtil.getIngelogdeMedewerker(httpSession);

                if (ingelogdeMedewerker != null && !ingelogdeMedewerker.getGebruikersnaam().equals(principal.getName())) {
                    ingelogdeMedewerker = null;
                    httpSession.invalidate();
                    httpSession = httpServletRequest.getSession(true);
                    LOG.info(String.format("HTTP session of medewerker '%s' on context path %s is invalidated", ingelogdeMedewerker.getGebruikersnaam(),
                                           httpServletRequest.getServletContext().getContextPath()));
                }

                if (ingelogdeMedewerker == null) {
                    ingelogdeMedewerker = createIngelogdeMedewerker(principal.getName());
                    SecurityUtil.setIngelogdeMedewerker(httpSession, ingelogdeMedewerker);
                    LOG.info(String.format("Medewerker '%s' logged in on context path %s", ingelogdeMedewerker.getGebruikersnaam(),
                                           httpServletRequest.getServletContext().getContextPath()));
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Medewerker createIngelogdeMedewerker(final String gebruikersnaam) {
        final User user = idmService.findUser(gebruikersnaam);
        if (user == null) {
            throw new RuntimeException(String.format("Gebruiker met gebruikersnaam '%s' is niet bekend.", gebruikersnaam));
        }
        final List<Group> groups = idmService.listGroupsForUser(gebruikersnaam);
        return new Medewerker(user, groups);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
