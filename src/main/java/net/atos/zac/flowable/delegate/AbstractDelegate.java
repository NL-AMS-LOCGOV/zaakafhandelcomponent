/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.delegate;

import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_IDENTIFICATIE;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public abstract class AbstractDelegate implements JavaDelegate {

    protected String getZaakIdentificatie(final DelegateExecution delegateExecution) {
        return (String) delegateExecution.getParent().getVariable(VAR_ZAAK_IDENTIFICATIE);
    }
}
