/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.cmmn.model.HumanTask;

/**
 * Wanneer een Human task plan item actief wordt,
 * wordt de waarde uit het 'Assigments' attribuut uitgelezen en vertaald naar een local variable.
 */
public class ExtraheerGroepLifecycleListener implements PlanItemInstanceLifecycleListener {

    public static final String VAR_LOCAL_CANDIDATE_GROUP_ID = "candidateGroupId";

    private final String sourceState;

    private final String targetState;

    public ExtraheerGroepLifecycleListener(final String sourceState, final String targetState) {
        this.sourceState = sourceState;
        this.targetState = targetState;
    }

    @Override
    public String getSourceState() {
        return sourceState;
    }

    @Override
    public String getTargetState() {
        return targetState;
    }

    @Override
    public void stateChanged(final DelegatePlanItemInstance planItemInstance, final String oldState, final String newState) {
        final HumanTask humanTask = (HumanTask) planItemInstance.getPlanItemDefinition();
        if (!humanTask.getCandidateGroups().isEmpty()) {
            if (humanTask.getCandidateGroups().size() > 1) {
                throw new RuntimeException(String.format("Human task plan item met name '%s' heeft meer dan 1  candidate group. Dit wordt niet ondersteund.",
                                                         planItemInstance.getName()));
            }
            final String groupId = humanTask.getCandidateGroups().get(0);
            planItemInstance.setVariableLocal(VAR_LOCAL_CANDIDATE_GROUP_ID, groupId);
        }
    }
}
