/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import org.flowable.cmmn.model.HumanTask;

/**
 * When a Human task plan item becomes active:
 * - the value of the 'Assigments' attribute of the Human Task Plan Item in the CMMN model is read and added as a variable to the plan item instance.
 */
public class ExtraheerGroepLifecycleListener implements PlanItemInstanceLifecycleListener {

    public static final String VAR_TASK_CANDIDATE_GROUP_ID = "candidateGroupId";

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
                throw new RuntimeException(String.format("Human task plan item with name '%s' has more than 1 candidate group. This is not supported.",
                                                         planItemInstance.getName()));
            }
            final String groupId = humanTask.getCandidateGroups().get(0);
            planItemInstance.setVariableLocal(VAR_TASK_CANDIDATE_GROUP_ID, groupId);
        }
    }
}
