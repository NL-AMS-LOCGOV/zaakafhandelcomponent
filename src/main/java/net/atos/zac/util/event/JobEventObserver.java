/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util.event;


import static net.atos.zac.util.event.JobEvent.TAAK_SIGNALERINGEN_JOB;
import static net.atos.zac.util.event.JobEvent.ZAAK_SIGNALERINGEN_JOB;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import net.atos.zac.signalering.SignaleringenJob;

/**
 * This bean listens for JobEvents and handles them by starting the relevant job.
 */
@ManagedBean
public class JobEventObserver {

    @Inject
    private SignaleringenJob signaleringenJob;

    public void onFire(final @ObservesAsync JobEvent event) {
        switch (event.getJobId()) {
            case TAAK_SIGNALERINGEN_JOB -> signaleringenJob.taakSignaleringenVerzenden();
            case ZAAK_SIGNALERINGEN_JOB -> signaleringenJob.zaakSignaleringenVerzenden();
            default -> throw new IllegalArgumentException(String.format("unknown job %s", event.getJobId()));
        }
    }
}
