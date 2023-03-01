/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin;

import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.admin.converter.RESTZaaktypeOverzichtConverter;
import net.atos.zac.app.admin.model.RESTBuildInformatie;
import net.atos.zac.app.admin.model.RESTZaaktypeInrichtingscheck;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.healthcheck.HealthCheckService;
import net.atos.zac.healthcheck.model.ZaaktypeInrichtingscheck;

@Singleton
@Path("health-check")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheckRESTService {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ConfiguratieService configuratieService;

    @Inject
    private HealthCheckService healthCheckService;

    @Inject
    private RESTZaaktypeOverzichtConverter zaaktypeConverter;

    @GET
    @Path("zaaktypes")
    public List<RESTZaaktypeInrichtingscheck> listZaaktypeInrichtingschecks() {
        return listZaaktypes().stream().map(zaaktype -> convertToREST(healthCheckService.controleerZaaktype(zaaktype.getUrl()))).toList();
    }

    @GET
    @Path("bestaat-communicatiekanaal-eformulier")
    public boolean readBestaatCommunicatiekanaalEformulier() {
        return healthCheckService.bestaatCommunicatiekanaalEformulier();
    }

    @DELETE
    @Path("ztc-cache")
    public ZonedDateTime clearZTCCaches() {
        ztcClientService.clearZaaktypeCache();
        ztcClientService.clearStatustypeCache();
        ztcClientService.clearResultaattypeCache();
        ztcClientService.clearInformatieobjecttypeCache();
        ztcClientService.clearZaaktypeInformatieobjecttypeCache();
        ztcClientService.clearBesluittypeCache();
        ztcClientService.clearRoltypeCache();
        ztcClientService.clearCacheTime();
        return ztcClientService.readCacheTime();
    }

    @GET
    @Path("ztc-cache")
    public ZonedDateTime readZTCCacheTime() {
        return ztcClientService.readCacheTime();
    }

    @GET
    @Path("build-informatie")
    public RESTBuildInformatie readBuildInformatie() {
        return new RESTBuildInformatie(healthCheckService.readBuildInformatie());
    }

    private List<Zaaktype> listZaaktypes() {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI()).stream()
                .filter(zaaktype -> !zaaktype.getConcept())
                .filter(Zaaktype::isNuGeldig)
                .toList();
    }

    private RESTZaaktypeInrichtingscheck convertToREST(final ZaaktypeInrichtingscheck check) {
        final RESTZaaktypeInrichtingscheck restCheck = new RESTZaaktypeInrichtingscheck();
        restCheck.zaaktype = zaaktypeConverter.convert(check.getZaaktype());
        restCheck.besluittypeAanwezig = check.isBesluittypeAanwezig();
        restCheck.resultaattypesMetVerplichtBesluit = check.getResultaattypesMetVerplichtBesluit();
        restCheck.resultaattypeAanwezig = check.isResultaattypeAanwezig();
        restCheck.informatieobjecttypeEmailAanwezig = check.isInformatieobjecttypeEmailAanwezig();
        restCheck.rolBehandelaarAanwezig = check.isRolBehandelaarAanwezig();
        restCheck.rolInitiatorAanwezig = check.isRolInitiatorAanwezig();
        restCheck.rolOverigeAanwezig = check.isRolOverigeAanwezig();
        restCheck.statustypeAfgerondAanwezig = check.isStatustypeAfgerondAanwezig();
        restCheck.statustypeAfgerondLaatsteVolgnummer = check.isStatustypeAfgerondLaatsteVolgnummer();
        restCheck.statustypeHeropendAanwezig = check.isStatustypeHeropendAanwezig();
        restCheck.statustypeInBehandelingAanwezig = check.isStatustypeInBehandelingAanwezig();
        restCheck.statustypeIntakeAanwezig = check.isStatustypeIntakeAanwezig();
        restCheck.zaakafhandelParametersValide = check.isZaakafhandelParametersValide();
        restCheck.valide = check.isValide();
        return restCheck;
    }
}
