/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebListener
public class ServletRequestProducingListener implements ServletRequestListener {

    private static final Logger LOG = Logger.getLogger(ServletRequestProducingListener.class.getName());

    private static final ThreadLocal<ServletRequest> SERVLET_REQUESTS = new ThreadLocal<>();

    @Override
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
        SERVLET_REQUESTS.set(servletRequestEvent.getServletRequest());
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        SERVLET_REQUESTS.remove();
    }

    @Named("activeSession")
    @Produces
    @ActiveSession
    public HttpSession getActiveSession() {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) SERVLET_REQUESTS.get();
        final HttpSession httpSession = httpServletRequest != null ? httpServletRequest.getSession(false) : null;
        if (httpSession == null || SecurityUtil.getLoggedInUser(httpSession) == null) {
            LOG.info(SecurityUtil.log("getActiveSession", httpSession));
        }
        return httpSession;
    }
}
