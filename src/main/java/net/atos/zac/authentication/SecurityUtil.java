/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

public class SecurityUtil implements Serializable {

    private static final long serialVersionUID = 654714651976511004L;

    private static final Logger LOG = Logger.getLogger(SecurityUtil.class.getName());

    /**
     * Constant which indicates in which {@link HttpSession} attribute the current authenticated {@link LoggedInUser} can be found.
     */
    public static final String LOGGED_IN_USER_SESSION_ATTRIBUTE = "logged-in-user";

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
        final HttpSession httpSession = this.httpSession.get();
        final LoggedInUser loggedInUser = SecurityUtil.getLoggedInUser(httpSession);
        if (httpSession == null || loggedInUser == null) {
            LOG.info(SecurityUtil.log("getLoggedInUser", httpSession, loggedInUser));
        }
        return loggedInUser;
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

    public static String log(final String method, final HttpSession session, final LoggedInUser user) {
        return method +
                " session " + (session == null ? "null" : session.getId()) +
                " user " + (user == null ? "null" : user.getId());
    }

    public static String log(final String method, final HttpSession session) {
        return log(method, session, getLoggedInUser(session));
    }
}
