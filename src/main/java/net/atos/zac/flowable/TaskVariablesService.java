/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.variable.api.history.HistoricVariableInstance;

@ApplicationScoped
@Transactional
public class TaskVariablesService {

    private static final String VAR_TAAKDATA = "taakdata";

    private static final String VAR_TAAKDOCUMENTEN = "taakdocumenten";

    private static final String VAR_TAAKINFORMATIE = "taakinformatie";

    @Inject
    private CmmnTaskService cmmnTaskService;

    @Inject
    private CmmnHistoryService cmmnHistoryService;

    public Map<String, String> findTaakdata(final String taskId) {
        return (Map<String, String>) findTaskVariable(taskId, VAR_TAAKDATA);
    }

    public void setTaakdata(final String taskId, final Map<String, String> taakdata) {
        setTaskVariable(taskId, VAR_TAAKDATA, taakdata);
    }

    public Map<String, String> findTaakinformatie(final String taskId) {
        return (Map<String, String>) findTaskVariable(taskId, VAR_TAAKINFORMATIE);
    }

    public void setTaakinformatie(final String taskId, final Map<String, String> taakinformatie) {
        setTaskVariable(taskId, VAR_TAAKINFORMATIE, taakinformatie);
    }

    public List<UUID> findTaakdocumenten(final String taskId) {
        return (List<UUID>) findTaskVariable(taskId, VAR_TAAKDOCUMENTEN);
    }

    public void setTaakdocumenten(final String taskId, final List<UUID> taakdocumenten) {
        setTaskVariable(taskId, VAR_TAAKDOCUMENTEN, taakdocumenten);
    }

    private Object findTaskVariable(final String taskId, final String variableName) {
        if (cmmnTaskService.createTaskQuery().taskId(taskId).singleResult() != null) {
            return cmmnTaskService.getVariableLocal(taskId, variableName);
        } else {
            final HistoricVariableInstance historicVariableInstance = cmmnHistoryService.createHistoricVariableInstanceQuery()
                    .taskId(taskId).variableName(variableName).singleResult();
            return historicVariableInstance != null ? historicVariableInstance.getValue() : null;
        }
    }

    private void setTaskVariable(final String taskId, final String variableName, Object value) {
        cmmnTaskService.setVariableLocal(taskId, variableName, value);
    }
}
