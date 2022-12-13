/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import org.flowable.cdi.spi.CmmnEngineLookup;
import org.flowable.cmmn.engine.CmmnEngine;

import net.atos.zac.flowable.bpmn.ProcessEngineLookupImpl;

public class CmmnEngineLookupImpl implements CmmnEngineLookup {

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public CmmnEngine getCmmnEngine() {
        return ProcessEngineLookupImpl.getCmmnEngineConfiguration().buildCmmnEngine();
    }

    @Override
    public void ungetCmmnEngine() {
        // Geen actie nodig.
    }
}
