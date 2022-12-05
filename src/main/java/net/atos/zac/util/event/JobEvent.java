/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util.event;

import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;

public class JobEvent extends AbstractEvent<String, JobId> {

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
        super(Opcode.CREATED, jobId);
    }

    public String getObjectType() {
        return "cron job";
    }
}
