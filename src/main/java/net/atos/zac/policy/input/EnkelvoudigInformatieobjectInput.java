/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import javax.json.bind.annotation.JsonbProperty;

import net.atos.zac.authentication.LoggedInUser;

public class EnkelvoudigInformatieobjectInput extends ZaakInput {

    @JsonbProperty("enkelvoudig_informatieobject")
    private final EnkelvoudigInformatieobjectData enkelvoudigInformatieobject;

    public EnkelvoudigInformatieobjectInput(final LoggedInUser loggedInUser, final EnkelvoudigInformatieobjectData enkelvoudigInformatieobjectData,
            final ZaakData zaakData) {
        super(loggedInUser, zaakData);
        enkelvoudigInformatieobject = enkelvoudigInformatieobjectData;
    }

    public EnkelvoudigInformatieobjectData getEnkelvoudigInformatieobject() {
        return enkelvoudigInformatieobject;
    }
}
