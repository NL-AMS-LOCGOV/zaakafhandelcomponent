/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.converter;

import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import net.atos.client.bag.model.AdresseerbaarObjectIOHal;
import net.atos.client.bag.model.Gebruiksdoel;
import net.atos.client.bag.model.Indicatie;
import net.atos.client.bag.model.Ligplaats;
import net.atos.client.bag.model.Standplaats;
import net.atos.client.bag.model.TypeAdresseerbaarObject;
import net.atos.client.bag.model.Verblijfsobject;
import net.atos.zac.app.bag.model.RESTAdresseerbaarObject;

public class RESTAdreseerbaarObjectConverter {

    public RESTAdresseerbaarObject convertToREST(final AdresseerbaarObjectIOHal adresseerbaarObjectIOHal) {
        if (adresseerbaarObjectIOHal == null) {
            return null;
        }

        if (adresseerbaarObjectIOHal.getLigplaats() != null) {
            return convertToREST(adresseerbaarObjectIOHal.getLigplaats().getLigplaats());
        } else if (adresseerbaarObjectIOHal.getStandplaats() != null) {
            return convertToREST(adresseerbaarObjectIOHal.getStandplaats().getStandplaats());
        } else if (adresseerbaarObjectIOHal.getVerblijfsobject() != null) {
            return convertToREST(adresseerbaarObjectIOHal.getVerblijfsobject().getVerblijfsobject());
        } else {
            throw new IllegalStateException("adresseerbaarObject is leeg");
        }
    }

    public RESTAdresseerbaarObject convertToREST(final Ligplaats ligplaats) {
        final RESTAdresseerbaarObject restAdresseerbaarObject = new RESTAdresseerbaarObject();
        restAdresseerbaarObject.typeAdresseerbaarObject = TypeAdresseerbaarObject.LIGPLAATS;
        restAdresseerbaarObject.identificatie = ligplaats.getIdentificatie();
        restAdresseerbaarObject.status = ligplaats.getStatus().toString();
        restAdresseerbaarObject.geconstateerd = Indicatie.J.equals(ligplaats.getGeconstateerd());
        restAdresseerbaarObject.geometry = RESTBAGConverter.convertVlak(ligplaats.getGeometrie());
        return restAdresseerbaarObject;
    }

    public RESTAdresseerbaarObject convertToREST(final Standplaats standplaats) {
        final RESTAdresseerbaarObject restAdresseerbaarObject = new RESTAdresseerbaarObject();
        restAdresseerbaarObject.typeAdresseerbaarObject = TypeAdresseerbaarObject.STANDPLAATS;
        restAdresseerbaarObject.identificatie = standplaats.getIdentificatie();
        restAdresseerbaarObject.status = standplaats.getStatus().toString();
        restAdresseerbaarObject.geconstateerd = Indicatie.J.equals(standplaats.getGeconstateerd());
        restAdresseerbaarObject.geometry = RESTBAGConverter.convertVlak(standplaats.getGeometrie());
        return restAdresseerbaarObject;
    }

    public RESTAdresseerbaarObject convertToREST(final Verblijfsobject verblijfsobject) {
        final RESTAdresseerbaarObject restAdresseerbaarObject = new RESTAdresseerbaarObject();
        restAdresseerbaarObject.typeAdresseerbaarObject = TypeAdresseerbaarObject.VERBLIJFSOBJECT;
        restAdresseerbaarObject.identificatie = verblijfsobject.getIdentificatie();
        restAdresseerbaarObject.status = verblijfsobject.getStatus().toString();
        restAdresseerbaarObject.geconstateerd = Indicatie.J.equals(verblijfsobject.getGeconstateerd());
        restAdresseerbaarObject.vboDoel = ListUtils.emptyIfNull(
                verblijfsobject.getGebruiksdoelen()).stream().map(Gebruiksdoel::toString).collect(Collectors.joining(", "));
        restAdresseerbaarObject.vboOppervlakte = verblijfsobject.getOppervlakte() != null ? verblijfsobject.getOppervlakte() : 0;
        restAdresseerbaarObject.geometry = RESTBAGConverter.convertPuntOrVlak(verblijfsobject.getGeometrie());
        return restAdresseerbaarObject;
    }
}
