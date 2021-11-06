/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.ZaakEigenschap;
import net.atos.zac.app.zaken.model.RESTZaakEigenschap;

public class RESTZaakEigenschappenConverter {

    @Inject
    private ZRCClientService zrcClientService;

    public RESTZaakEigenschap convert(final URI uri) {
        if (uri != null) {
            final ZaakEigenschap zaakeigenschap = zrcClientService.readZaakeigenschap(uri);
            if (zaakeigenschap != null) {
                final RESTZaakEigenschap restZaakEigenschap = new RESTZaakEigenschap();
                restZaakEigenschap.naam = zaakeigenschap.getNaam();
                restZaakEigenschap.waarde = zaakeigenschap.getWaarde();
                return restZaakEigenschap;
            }
        }
        return null;
    }

    public List<RESTZaakEigenschap> convert(final Collection<URI> eigenschappen) {
        if (eigenschappen == null) {
            return null;
        }
        return eigenschappen.stream().map(this::convert).collect(Collectors.toList());
    }
}
