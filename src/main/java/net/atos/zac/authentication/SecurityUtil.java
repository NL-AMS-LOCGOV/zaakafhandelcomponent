/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

public class SecurityUtil implements Serializable {

    private static final long serialVersionUID = 654714651976511004L;

    /**
     * Constant which indicates in which {@link HttpSession} attribute the current authenticated {@link LoggedInUser} can be found.
     */
    public static final String LOGGED_IN_USER_SESSION_ATTRIBUTE = "logged-in-user";

    @Inject
    @ActiveSession
    private HttpSession httpSession;

    /**
     * Produces an authenticated {@link LoggedInUser} for use in CDI Beans.
     * The authenticated {@link LoggedInUser} instance is retrieved from the current user session, where it is set via the {@link UserPrincipalFilter}
     *
     * @return - {@link LoggedInUser} - The current logged in user.
     */
    @Produces
    public LoggedInUser getLoggedInUser() {
        return getLoggedInUser(httpSession);
    }

    public static LoggedInUser getLoggedInUser(final HttpSession httpSession) {
        if (httpSession != null) {
            return (LoggedInUser) httpSession.getAttribute(LOGGED_IN_USER_SESSION_ATTRIBUTE);
        } else {
            return null;
        }
    }

    public static void setLoggedInUser(final HttpSession httpSession, final LoggedInUser loggedInUser) {
        httpSession.setAttribute(SecurityUtil.LOGGED_IN_USER_SESSION_ATTRIBUTE, loggedInUser);
    }
}
