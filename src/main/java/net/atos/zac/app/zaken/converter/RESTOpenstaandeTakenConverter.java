/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.zaken.model.RESTOpenstaandeTaken;
import net.atos.zac.flowable.TaskService;

public class RESTOpenstaandeTakenConverter {

    @Inject
    private TaskService taskService;

    public RESTOpenstaandeTaken convert(final UUID zaakUUID) {
        final List<Task> openstaandeTaken = taskService.listOpenTasks(zaakUUID);
        final RESTOpenstaandeTaken restOpenstaandeTaken = new RESTOpenstaandeTaken();

        if (openstaandeTaken != null) {
            restOpenstaandeTaken.aantalOpenstaandeTaken = openstaandeTaken.size();
            restOpenstaandeTaken.taakNamen = openstaandeTaken.stream().map(TaskInfo::getName).toList();
        }

        return restOpenstaandeTaken;
    }
}
