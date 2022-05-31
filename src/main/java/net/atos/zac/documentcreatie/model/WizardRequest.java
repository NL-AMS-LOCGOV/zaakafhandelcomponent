/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import net.atos.client.sd.model.Deposit;
import net.atos.client.sd.model.SmartDocument;

public class WizardRequest extends Deposit {

    public final Registratie registratie;

    public final Data data;

    public WizardRequest(final SmartDocument smartDocument, final Registratie registratie, final Data data) {
        this.smartDocument = smartDocument;
        this.registratie = registratie;
        this.data = data;
    }

    public SmartDocument getSmartDocument() {
        return smartDocument;
    }

    public Registratie getRegistratie() {
        return registratie;
    }

    public Data getData() {
        return data;
    }
}
