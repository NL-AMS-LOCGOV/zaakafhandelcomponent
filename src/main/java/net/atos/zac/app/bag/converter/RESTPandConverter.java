/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.net.URI;
import java.util.List;

import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Pand;
import net.atos.client.bag.model.PandIOHal;
import net.atos.client.bag.model.PandIOHalBasis;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectPand;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectPand;
import net.atos.zac.app.bag.model.RESTPand;

public class RESTPandConverter {

    public List<RESTPand> convertToREST(final List<PandIOHalBasis> panden) {
        if (panden == null) {
            return List.of();
        }
        return panden.stream().map(this::convertToREST).toList();
    }

    public RESTPand convertToREST(final PandIOHalBasis pandIO) {
        if (pandIO == null) {
            return null;
        }
        return convertToREST(pandIO.getPand());
    }

    public RESTPand convertToREST(final PandIOHal pandIO) {
        if (pandIO == null) {
            return null;
        }
        final RESTPand restPand = convertToREST(pandIO.getPand());
        restPand.url = URI.create(pandIO.getLinks().getSelf().getHref());
        return restPand;
    }

    public RESTPand convertToREST(final ZaakobjectPand zaakobjectPand) {
        if (zaakobjectPand == null || zaakobjectPand.getObjectIdentificatie() == null) {
            return null;
        }
        final ObjectPand pand = zaakobjectPand.getObjectIdentificatie();
        final RESTPand restPand = new RESTPand();
        restPand.identificatie = pand.getIdentificatie();
        return restPand;
    }

    public ZaakobjectPand convertToZaakobject(final RESTPand pand, final Zaak zaak) {
        return new ZaakobjectPand(zaak.getUrl(), pand.url, new ObjectPand(pand.identificatie));
    }

    public RESTPand convertToREST(final Pand pand) {
        final RESTPand restPand = new RESTPand();
        restPand.identificatie = pand.getIdentificatie();
        restPand.status = pand.getStatus();
        if (pand.getStatus() != null) {
            restPand.statusWeergave = pand.getStatus().toString();
        }
        restPand.oorspronkelijkBouwjaar = pand.getOorspronkelijkBouwjaar();
        restPand.geconstateerd = Indicatie.J.equals(pand.getGeconstateerd());
        restPand.geometry = RESTBAGConverter.convertVlak(pand.getGeometrie());
        return restPand;
    }

}
