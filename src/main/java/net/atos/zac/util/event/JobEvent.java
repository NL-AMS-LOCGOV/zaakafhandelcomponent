/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util.event;

public class JobEvent {
    public static final String ZAAK_SIGNALERINGEN_JOB = "ZAAK_SIGNALERINGEN";

    private String jobId;

    /**
     * Constructor for the sake of JAXB
     */
    public JobEvent() {
        super();
    }

    /**
     * Constructor with all required fields.
     *
     * @param jobId indicates the job that must be started
     */
    public JobEvent(final String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }
}
