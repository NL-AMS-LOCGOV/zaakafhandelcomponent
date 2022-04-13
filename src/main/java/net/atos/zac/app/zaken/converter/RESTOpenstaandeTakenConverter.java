/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import net.atos.zac.app.zaken.model.RESTOpenstaandeTaken;

import net.atos.zac.flowable.FlowableService;

import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import javax.inject.Inject;

import java.util.List;
import java.util.UUID;

public class RESTOpenstaandeTakenConverter {

    @Inject
    private FlowableService flowableService;

    public RESTOpenstaandeTaken convert(final UUID zaakUUID) {
        final List<Task> openstaandeTaken = flowableService.listOpenTasksforCase(zaakUUID);
        final RESTOpenstaandeTaken restOpenstaandeTaken = new RESTOpenstaandeTaken();

        if (openstaandeTaken != null) {
            restOpenstaandeTaken.aantalOpenstaandeTaken = openstaandeTaken.size();
            restOpenstaandeTaken.taakNamen = openstaandeTaken.stream().map(TaskInfo::getName).toList();
        }

        return restOpenstaandeTaken;
    }
}
