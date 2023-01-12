/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class SecurityUtil implements Serializable {

    private static final long serialVersionUID = 654714651976511004L;

    /**
     * Constant which indicates in which {@link HttpSession} attribute the current authenticated {@link LoggedInUser} can be found.
     */
    public static final String LOGGED_IN_USER_SESSION_ATTRIBUTE = "logged-in-user";

    public static final LoggedInUser FUNCTIONEEL_GEBRUIKER =
            new LoggedInUser("FG", "", "Functionele gebruiker", "Functionele gebruiker", null,
                    Set.of("functionele_gebruiker"), Collections.emptySet());

    @Inject
    @ActiveSession
    private Instance<HttpSession> httpSession;

    /**
     * Produces an authenticated {@link LoggedInUser} for use in CDI Beans.
     * The authenticated {@link LoggedInUser} instance is retrieved from the current user session, where it is set via the {@link UserPrincipalFilter}
     *
     * @return - {@link LoggedInUser} - The current logged in user.
     */
    @Produces
    public LoggedInUser getLoggedInUser() {
        return getLoggedInUser(httpSession.get());
    }

    public static LoggedInUser getLoggedInUser(final HttpSession httpSession) {
        if (httpSession != null) {
            return (LoggedInUser) httpSession.getAttribute(LOGGED_IN_USER_SESSION_ATTRIBUTE);
        } else {
            return FUNCTIONEEL_GEBRUIKER; // No session in async context!
        }
    }

    public static void setLoggedInUser(final HttpSession httpSession, final LoggedInUser loggedInUser) {
        httpSession.setAttribute(SecurityUtil.LOGGED_IN_USER_SESSION_ATTRIBUTE, loggedInUser);
    }

    public static void setFunctioneelGebruiker(final HttpSession httpSession) {
        setLoggedInUser(httpSession, FUNCTIONEEL_GEBRUIKER);
    }
}
