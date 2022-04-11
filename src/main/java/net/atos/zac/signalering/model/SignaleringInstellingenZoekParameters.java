/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.signalering.model.SignaleringTarget.GROEP;
import static net.atos.zac.signalering.model.SignaleringTarget.MEDEWERKER;

import org.flowable.idm.api.Group;

import net.atos.zac.authentication.Medewerker;

public class SignaleringInstellingenZoekParameters {
    private final SignaleringTarget ownertype;

    private final String owner;

    private SignaleringType.Type type;

    public SignaleringInstellingenZoekParameters(final Signalering signalering) {
        this.ownertype = signalering.getTargettype();
        this.owner = signalering.getTarget();
        this.type = signalering.getType().getType();
    }

    public SignaleringInstellingenZoekParameters(final Group owner) {
        this.ownertype = GROEP;
        this.owner = owner.getId();
    }

    public SignaleringInstellingenZoekParameters(final Medewerker owner) {
        this.ownertype = MEDEWERKER;
        this.owner = owner.getGebruikersnaam();
    }

    public SignaleringTarget getOwnertype() {
        return ownertype;
    }

    public String getOwner() {
        return owner;
    }

    public SignaleringType.Type getType() {
        return type;
    }

    public SignaleringInstellingenZoekParameters type(final SignaleringType.Type type) {
        this.type = type;
        return this;
    }
}
