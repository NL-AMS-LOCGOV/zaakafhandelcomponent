/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

public class SecurityUtil implements Serializable {

    private static final long serialVersionUID = 654714651976511004L;

    /**
     * Constant which indicates in which {@link HttpSession} attribute the current authenticated {@link Medewerker} can be found.
     */
    private static final String INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE = "medewerker";

    @Inject
    @ActiveSession
    private HttpSession httpSession;

    /**
     * Produces an authenticated {@link Medewerker} for use in CDI Beans.
     * The authenticated {@link Medewerker} instance is retrieved from the current user session, where it is set via the {@link UserPrincipalFilter}
     *
     * @return - {@link Medewerker} - The current logged in medewerker.
     */
    @Named("IngelogdeMedewerker")
    @Produces
    @IngelogdeMedewerker
    public Medewerker getIngelogdeMedewerker() {
        return getIngelogdeMedewerker(httpSession);

    }

    public static Medewerker getIngelogdeMedewerker(final HttpSession httpSession) {
        if (httpSession != null) {
            return (Medewerker) httpSession.getAttribute(INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE);
        } else {
            return null;
        }
    }

    public static void setIngelogdeMedewerker(final HttpSession httpSession, final Medewerker ingelogdeMedewerker) {
        httpSession.setAttribute(SecurityUtil.INGELOGDE_MEDEWERKER_SESSION_ATTRIBUTE, ingelogdeMedewerker);
    }
}
