/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util.event;

public class JobEvent {

    private JobId jobId;

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
    public JobEvent(final JobId jobId) {
        this.jobId = jobId;
    }

    public JobId getJobId() {
        return jobId;
    }
}
