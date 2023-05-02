/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag.model;

import net.atos.client.bag.model.TypeAdresseerbaarObject;

public class RESTAdresseerbaarObject extends RESTBAGObject {

    public TypeAdresseerbaarObject typeAdresseerbaarObject;

    public String status;

    public String vboDoel;

    public int vboOppervlakte;


    public RESTAdresseerbaarObject() {
    }

    @Override
    public BAGObjectType getBagObjectType() {
        return BAGObjectType.ADRESSEERBAAR_OBJECT;
    }

    @Override
    public String getOmschrijving() {
        return "%s %s".formatted(typeAdresseerbaarObject.toString(), identificatie);
    }

}
