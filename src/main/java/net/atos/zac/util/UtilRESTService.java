/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.ztc.ZTCClientService;

@Path("util")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_HTML)
public class UtilRESTService {

    private static final Logger LOG = Logger.getLogger(UtilRESTService.class.getName());

    private static final String ZGW = h(2, "zgwApiService");

    private static final String ZRC = h(2, "zrcClientService");

    private static final String ZTC = h(2, "ztcClientService");

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @GET
    public String index() {
        return body(h(1, "Util") +
                            links(Stream.of("cache",
                                            "cache/zgw",
                                            "cache/zrc",
                                            "cache/ztc")) +
                            links(Stream.of("cache/clear",
                                            "cache/zgw/clear",
                                            "cache/zrc/clear",
                                            "cache/ztc/clear")));
    }

    private String links(final Stream<String> url) {
        return ul(url.map(method -> a("/rest/util/" + method, method)));
    }

    @GET
    @Path("cache")
    public String getCaches() {
        return body(Stream.of(getZgwApiCaches(),
                              getZrcClientCaches(),
                              getZtcClientCaches()));
    }

    @GET
    @Path("cache/zgw")
    public String getZgwCaches() {
        return body(getZgwApiCaches());
    }

    private String getZgwApiCaches() {
        return ZGW + ul(zgwApiService.cacheNames().stream());
    }

    @GET
    @Path("cache/zrc")
    public String getZrcCaches() {
        return body(getZrcClientCaches());
    }

    private String getZrcClientCaches() {
        return ZRC + ul(zrcClientService.cacheNames().stream());
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
        return body(Stream.of(clearZgwApiCaches(),
                              clearZrcClientCaches(),
                              clearZtcClientCaches()));
    }

    @GET
    @Path("cache/zgw/clear")
    public String clearZgwCaches() {
        return body(clearZgwApiCaches());
    }

    private String clearZgwApiCaches() {
        return ZGW + ul(Stream.of(zgwApiService.clearZaakBehandelaarManagedCache(),
                                  zgwApiService.clearZaakGroepManagedCache()));
    }

    @GET
    @Path("cache/zrc/clear")
    public String clearZrcCaches() {
        return body(clearZrcClientCaches());
    }

    private String clearZrcClientCaches() {
        return ZRC + ul(Stream.of(zrcClientService.clearZaakstatusManagedCache()));
    }

    @GET
    @Path("cache/ztc/clear")
    public String clearZtcCaches() {
        return body(clearZtcClientCaches());
    }

    private String clearZtcClientCaches() {
        return ZTC + ul(Stream.of(ztcClientService.clearStatustypeCache(),
                                  ztcClientService.clearZaaktypeStatustypeManagedCache(),
                                  ztcClientService.clearResultaattypeCache(),
                                  ztcClientService.clearZaaktypeResultaattypeManagedCache(),
                                  ztcClientService.clearZaaktypeRoltypeCache(),
                                  ztcClientService.clearZaaktypeUrlCache(),
                                  ztcClientService.clearZaaktypeCache(),
                                  ztcClientService.clearZaaktypeManagedCache()));
    }

    private static String body(final Stream<String> utils) {
        return body(utils.collect(Collectors.joining()));
    }

    private static String body(final String utils) {
        return "<html></head><body>" + utils + "</body></html>";
    }

    private static String h(final int i, final String label) {
        return "<h" + i + ">" + label + "</h" + i + ">";
    }

    private static String ul(final Stream<String> li) {
        return "<ul>" + li.sorted().collect(Collectors.joining("</li><li>", "<li>", "</li>")) + "</ul>";
    }

    private static String a(final String url, final String label) {
        return "<a href=\"" + url + "\">" + label + "</a>";
    }
}
