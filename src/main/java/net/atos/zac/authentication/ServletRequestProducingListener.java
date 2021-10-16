/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.authentication;

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
        return ((HttpServletRequest) SERVLET_REQUESTS.get()).getSession(false);
    }
}
