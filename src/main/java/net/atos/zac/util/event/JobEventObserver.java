/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util.event;


import static net.atos.zac.util.event.JobEvent.TAAK_SIGNALERINGEN_JOB;
import static net.atos.zac.util.event.JobEvent.ZAAK_SIGNALERINGEN_JOB;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import net.atos.zac.signalering.SignaleringenJob;

/**
 * This bean listens for JobEvents and handles them by starting the relevant job.
 */
@ManagedBean
public class JobEventObserver {
    private static final Logger LOG = Logger.getLogger(JobEventObserver.class.getName());

    @Inject
    private SignaleringenJob signaleringenJob;

    public void onFire(final @ObservesAsync JobEvent event) {
        try {
            switch (event.getJobId()) {
                case TAAK_SIGNALERINGEN_JOB -> signaleringenJob.taakSignaleringenVerzenden();
                case ZAAK_SIGNALERINGEN_JOB -> signaleringenJob.zaakSignaleringenVerzenden();
                default -> throw new IllegalArgumentException(String.format("unknown job %s", event.getJobId()));
            }
        } catch (final Exception ex) {
            LOG.log(Level.SEVERE, "asynchronous guard", ex);
        }
    }
}
