/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;


import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * If the requested resource doesn't exist, use index.html
 * more information at https://angular.io/guide/deployment#server-configuration
 */
@WebFilter(filterName = "indexRewriteFilter")
public class indexRewriteFilter implements Filter {

    private final List<String> resourcePaths = List.of("/assets", "/rest", "/websocket");

    private static final Pattern REGEX_RESOURCES = Pattern.compile("^.*.(js|js.map|json|css|txt|jpe?g|png|gif|svg|ico|webmanifest|eot|ttf|woff|woff2)$");

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest req = (HttpServletRequest) request;
            if (req.getServletPath().equals("/startformulieren.html")) {
                chain.doFilter(request, response);
            } else if (isResourcePath(req) || isResource(req)) {
                chain.doFilter(request, response);
            } else {
                req.getRequestDispatcher("/index.html").forward(request, response);
            }
        }
    }

    private boolean isResourcePath(final HttpServletRequest req) {
        return resourcePaths.stream().anyMatch(s -> req.getServletPath().startsWith(s));
    }

    private boolean isResource(final HttpServletRequest req) {
        return REGEX_RESOURCES.matcher(req.getServletPath()).find();
    }
}
