/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.signalering.model;

import static net.atos.zac.signalering.model.SignaleringTarget.GROUP;
import static net.atos.zac.signalering.model.SignaleringTarget.USER;

import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;

public class SignaleringInstellingenZoekParameters {
    private final SignaleringTarget ownertype;

    private final String owner;

    private SignaleringType.Type type;

    private boolean dashboard;

    private boolean mail;

    public SignaleringInstellingenZoekParameters(final Signalering signalering) {
        this.ownertype = signalering.getTargettype();
        this.owner = signalering.getTarget();
        this.type = signalering.getType().getType();
    }

    public SignaleringInstellingenZoekParameters(final SignaleringTarget ownertype, final String owner) {
        this.ownertype = ownertype;
        this.owner = owner;
    }

    public SignaleringInstellingenZoekParameters(final Group owner) {
        this(GROUP, owner.getId());
    }

    public SignaleringInstellingenZoekParameters(final User owner) {
        this(USER, owner.getId());
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

    public Boolean getDashboard() {
        return dashboard;
    }

    public SignaleringInstellingenZoekParameters dashboard() {
        this.dashboard = true;
        return this;
    }

    public Boolean getMail() {
        return mail;
    }

    public SignaleringInstellingenZoekParameters mail() {
        this.mail = true;
        return this;
    }
}
