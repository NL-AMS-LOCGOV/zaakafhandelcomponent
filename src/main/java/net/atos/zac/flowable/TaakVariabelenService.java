/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAKTYPE_DOMEIN;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAKTYPE_OMSCHRIJVING;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAKTYPE_UUUID;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_IDENTIFICATIE;
import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_UUID;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

@ApplicationScoped
@Transactional
public class TaakVariabelenService {

    private static final String VAR_TASK_TAAKDATA = "taakdata";

    private static final String VAR_TASK_TAAKDOCUMENTEN = "taakdocumenten";

    private static final String VAR_TASK_TAAKINFORMATIE = "taakinformatie";

    @Inject
    private TaskService taskService;

    @Inject
    private TakenService takenService;

    public Map<String, String> readTaakdata(final TaskInfo taskInfo) {
        return (Map<String, String>) findTaskVariable(taskInfo, VAR_TASK_TAAKDATA).orElse(Collections.emptyMap());
    }

    public void setTaakdata(final Task task, final Map<String, String> taakdata) {
        setTaskVariable(task, VAR_TASK_TAAKDATA, taakdata);
    }

    public Map<String, String> readTaakinformatie(final TaskInfo taskInfo) {
        return (Map<String, String>) findTaskVariable(taskInfo, VAR_TASK_TAAKINFORMATIE).orElse(Collections.emptyMap());
    }

    public void setTaakinformatie(final Task task, final Map<String, String> taakinformatie) {
        setTaskVariable(task, VAR_TASK_TAAKINFORMATIE, taakinformatie);
    }

    public List<UUID> readTaakdocumenten(final TaskInfo taskInfo) {
        return (List<UUID>) findTaskVariable(taskInfo, VAR_TASK_TAAKDOCUMENTEN).orElse(Collections.emptyList());
    }

    public void setTaakdocumenten(final Task task, final List<UUID> taakdocumenten) {
        setTaskVariable(task, VAR_TASK_TAAKDOCUMENTEN, taakdocumenten);
    }

    public UUID readZaakUUID(final TaskInfo taskInfo) {
        return (UUID) readVariable(taskInfo, VAR_ZAAK_UUID);
    }

    public String readZaakIdentificatie(final TaskInfo taskInfo) {
        return (String) readVariable(taskInfo, VAR_ZAAK_IDENTIFICATIE);
    }

    public UUID readZaaktypeUUID(final TaskInfo taskInfo) {
        return (UUID) readVariable(taskInfo, VAR_ZAAKTYPE_UUUID);
    }

    public String readZaaktypeOmschrijving(final TaskInfo taskInfo) {
        return (String) readVariable(taskInfo, VAR_ZAAKTYPE_OMSCHRIJVING);
    }

    public String readZaaktypeDomein(final TaskInfo taskInfo) {
        return (String) findTaskVariable(taskInfo, VAR_ZAAKTYPE_DOMEIN).orElse(null);
    }

    private Map<String, Object> getVariables(final TaskInfo taskInfo) {
        return takenService.isCmmnTask(taskInfo) ? taskInfo.getCaseVariables() : taskInfo.getProcessVariables();
    }

    private Optional<Object> findVariable(final TaskInfo taskInfo, final String variableName) {
        final Object value = getVariables(taskInfo).get(variableName);
        if (value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    private Object readVariable(final TaskInfo taskInfo, final String variableName) {
        return findVariable(taskInfo, variableName)
                .orElseThrow(() -> new RuntimeException(
                        "No variable found with name '%s' for task with name '%s' and id '%s'"
                                .formatted(variableName, taskInfo.getName(), taskInfo.getId())));
    }

    private Optional<Object> findTaskVariable(final TaskInfo taskInfo, final String variableName) {
        final Object value = taskInfo.getTaskLocalVariables().get(variableName);
        if (value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    private void setTaskVariable(final Task task, final String variableName, final Object value) {
        taskService.setVariableLocal(task.getId(), variableName, value);
    }
}
