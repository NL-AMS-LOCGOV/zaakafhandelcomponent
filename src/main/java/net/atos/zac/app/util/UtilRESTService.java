/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.util;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.ztc.ZTCClientService;

@Path("util")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_HTML)
public class UtilRESTService {

    private static final String ZTC = h(2, "ztcClientService");

    @Inject
    private ZTCClientService ztcClientService;

    @GET
    public String index() {
        return body(h(1, "Util") +
                            links(Stream.of("cache", "cache/ztc")) +
                            links(Stream.of("cache/clear", "cache/ztc/clear")));
    }

    private String links(final Stream<String> url) {
        return ul(url.map(method -> a("/rest/util/" + method, method)));
    }

    @GET
    @Path("cache")
    public String getCaches() {
        return body(Stream.of(getZtcClientCaches()));
    }

    @GET
    @Path("cache/ztc")
    public String getZtcCaches() {
        return body(getZtcClientCaches());
    }

    private String getZtcClientCaches() {
        return ZTC + ul(ztcClientService.cacheNames().stream());
    }

    @GET
    @Path("cache/clear")
    public String clearCaches() {
        return body(Stream.of(clearZtcClientCaches()));
    }

    @GET
    @Path("cache/ztc/clear")
    public String clearZtcCaches() {
        return body(clearZtcClientCaches());
    }

    private String clearZtcClientCaches() {
        return ZTC + ul(Stream.of(ztcClientService.clearZaaktypeCache(),
                                  ztcClientService.clearStatustypeCache(),
                                  ztcClientService.clearResultaattypeCache(),
                                  ztcClientService.clearInformatieobjecttypeCache(),
                                  ztcClientService.clearZaaktypeInformatieobjecttypeCache(),
                                  ztcClientService.clearBesluittypeCache(),
                                  ztcClientService.clearRoltypeCache(),
                                  ztcClientService.clearCacheTime()));
    }

    private static String body(final Stream<String> utils) {
        return body(utils.collect(joining()));
    }

    private static String body(final String utils) {
        return "<html></head><body>" + utils + "</body></html>";
    }

    private static String h(final int i, final String label) {
        return "<h" + i + ">" + label + "</h" + i + ">";
    }

    private static String ul(final Stream<String> li) {
        return "<ul>" + li.sorted().collect(joining("</li><li>", "<li>", "</li>")) + "</ul>";
    }

    private static String a(final String url, final String label) {
        return "<a href=\"" + url + "\">" + label + "</a>";
    }
}
