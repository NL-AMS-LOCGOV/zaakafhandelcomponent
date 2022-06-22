/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;

import net.atos.zac.app.zaken.model.RelatieType;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RelevanteZaak;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.zaken.model.RESTGerelateerdeZaak;

public class RESTGerelateerdeZaakConverter {

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    public List<RESTGerelateerdeZaak> getGerelateerdeZaken(final Zaak zaak) {
        final List<RESTGerelateerdeZaak> list = new ArrayList<>();
        final RESTGerelateerdeZaak hoofdzaak = convertHoofdzaak(zaak.getHoofdzaak());
        final List<RESTGerelateerdeZaak> deelzaken = convertDeelzaken(zaak.getDeelzaken());
        final List<RESTGerelateerdeZaak> relevanteAndereZaken = convertRelevanteAndereZaken(zaak.getRelevanteAndereZaken());

        if (hoofdzaak != null) {
            list.add(hoofdzaak);
        }
        if (deelzaken != null) {
            list.addAll(deelzaken);
        }
        if (relevanteAndereZaken != null) {
            list.addAll(relevanteAndereZaken);
        }
        return list;
    }

    private RESTGerelateerdeZaak convert(final URI zaakURI, final RelatieType relatieType) {
        final Zaak zaak = zrcClientService.readZaak(zaakURI);
        final RESTGerelateerdeZaak restGerelateerdeZaak = new RESTGerelateerdeZaak();
        restGerelateerdeZaak.relatieType = relatieType;
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        restGerelateerdeZaak.zaaktypeOmschrijving = zaaktype.getOmschrijving();
        final Status status = zrcClientService.readStatus(zaak.getStatus());
        final Statustype statustype = ztcClientService.readStatustype(status.getStatustype());
        restGerelateerdeZaak.statustypeOmschrijving = statustype.getOmschrijving();
        restGerelateerdeZaak.identificatie = zaak.getIdentificatie();
        restGerelateerdeZaak.startdatum = zaak.getStartdatum();
        return restGerelateerdeZaak;
    }

    private List<RESTGerelateerdeZaak> convertRelevanteAndereZaken(final List<RelevanteZaak> relevanteZaken) {
        if (CollectionUtils.isEmpty(relevanteZaken)) {
            return null;
        }
        final List<RESTGerelateerdeZaak> list = new ArrayList<>();
        for (final RelevanteZaak relevanteZaak : relevanteZaken) {
            switch (relevanteZaak.getAardRelatie()) {
                case VERVOLG: //De andere zaak gaf aanleiding tot het starten van de onderhanden zaak.
                    list.add(convert(relevanteZaak.getUrl(), RelatieType.VERVOLG));
                    break;
                case ONDERWERP: //De andere zaak is relevant voor cq. is onderwerp van de onderhanden zaak.
                    list.add(convert(relevanteZaak.getUrl(), RelatieType.RELEVANT));
                    break;
                case BIJDRAGE: //Aan het bereiken van de uitkomst van de andere zaak levert de onderhanden zaak een bijdrage.
                    list.add(convert(relevanteZaak.getUrl(), RelatieType.BIJDRAGE));
                    break;
                default:
                    throw new IllegalStateException("Onbekende waarde: " + relevanteZaak.getAardRelatie());
            }
        }
        return list;
    }

    private RESTGerelateerdeZaak convertHoofdzaak(final URI uri) {
        if (uri == null) {
            return null;
        }
        return convert(uri, RelatieType.HOOFDZAAK);
    }

    private List<RESTGerelateerdeZaak> convertDeelzaken(final Collection<URI> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return null;
        }
        return uris.stream().map((URI zaak) -> convert(zaak, RelatieType.DEELZAAK)).toList();
    }
}
