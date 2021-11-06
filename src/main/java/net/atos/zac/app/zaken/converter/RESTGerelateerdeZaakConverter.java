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

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RelevanteZaak;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.app.zaken.model.RESTGerelateerdeZaak;

public class RESTGerelateerdeZaakConverter {

    @Inject
    private ZRCClientService zrcClientService;

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

    private RESTGerelateerdeZaak convert(final URI zaakURI, final RESTGerelateerdeZaak.RelatieType relatieType) {
        final Zaak zaak = zrcClientService.readZaak(zaakURI);
        final RESTGerelateerdeZaak restZaak = new RESTGerelateerdeZaak();
        restZaak.relatieType = relatieType;
        restZaak.omschrijving = zaak.getOmschrijving();
        restZaak.toelichting = zaak.getToelichting();
        restZaak.identificatie = zaak.getIdentificatie();
        restZaak.uuid = zaak.getUuid().toString();
        restZaak.einddatum = zaak.getEinddatum();
        restZaak.startdatum = zaak.getStartdatum();
        return restZaak;
    }

    private List<RESTGerelateerdeZaak> convertRelevanteAndereZaken(final List<RelevanteZaak> relevanteZaken) {
        if (CollectionUtils.isEmpty(relevanteZaken)) {
            return null;
        }
        final List<RESTGerelateerdeZaak> list = new ArrayList<>();
        for (final RelevanteZaak relevanteZaak : relevanteZaken) {
            switch (relevanteZaak.getAardRelatie()) {
                case VERVOLG: //De andere zaak gaf aanleiding tot het starten van de onderhanden zaak.
                    list.add(convert(relevanteZaak.getUrl(), RESTGerelateerdeZaak.RelatieType.VERVOLG));
                    break;
                case ONDERWERP: //De andere zaak is relevant voor cq. is onderwerp van de onderhanden zaak.
                    list.add(convert(relevanteZaak.getUrl(), RESTGerelateerdeZaak.RelatieType.RELEVANT));
                    break;
                case BIJDRAGE: //Aan het bereiken van de uitkomst van de andere zaak levert de onderhanden zaak een bijdrage.
                    list.add(convert(relevanteZaak.getUrl(), RESTGerelateerdeZaak.RelatieType.BIJDRAGE));
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
        return convert(uri, RESTGerelateerdeZaak.RelatieType.HOOFDZAAK);
    }

    private List<RESTGerelateerdeZaak> convertDeelzaken(final Collection<URI> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return null;
        }
        return uris.stream().map((URI zaak) -> convert(zaak, RESTGerelateerdeZaak.RelatieType.DEELZAAK)).collect(Collectors.toList());
    }
}
