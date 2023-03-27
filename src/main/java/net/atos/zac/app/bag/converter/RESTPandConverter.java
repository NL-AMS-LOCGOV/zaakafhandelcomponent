/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.util.List;

import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Pand;
import net.atos.client.bag.model.PandIOHalBasis;
import net.atos.zac.app.bag.model.RESTPand;

public class RESTPandConverter {

    public List<RESTPand> convertToRest(final List<PandIOHalBasis> panden) {
        if (panden == null) {
            return List.of();
        }
        return panden.stream().map(this::convertToREST).toList();
    }

    public RESTPand convertToREST(final PandIOHalBasis pandIO) {
        final Pand pand = pandIO.getPand();
        final RESTPand restPand = new RESTPand();
        restPand.identificatie = pand.getIdentificatie();
        restPand.status = pand.getStatus();
        restPand.oorspronkelijkBouwjaar = pand.getOorspronkelijkBouwjaar();
        restPand.geconstateerd = Indicatie.J.equals(pand.getGeconstateerd());
        return restPand;
    }
}
