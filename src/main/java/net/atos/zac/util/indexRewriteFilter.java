/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(filterName = "indexRewriteFilter")
public class indexRewriteFilter implements Filter {

    private final List<String> resourcePaths =
            Arrays.asList("/assets", "/rest", "/websocket");

    private static final Pattern REGEX_RESOURCES = Pattern.compile("^.*.(js|js.map|json|css|txt|jpe?g|png|gif|svg|ico|webmanifest|eot|ttf|woff|woff2)$");

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest req = (HttpServletRequest) request;
            if (isResourcePath(req) || isResource(req)) {
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
        final Matcher expressieMatcherItem = REGEX_RESOURCES.matcher(req.getServletPath());
        return expressieMatcherItem.find();
    }
}
