/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import java.util.List;

import org.flowable.cmmn.engine.interceptor.CreateHumanTaskAfterContext;
import org.flowable.cmmn.engine.interceptor.CreateHumanTaskBeforeContext;

public class CreateHumanTaskInterceptor implements org.flowable.cmmn.engine.interceptor.CreateHumanTaskInterceptor {

    public static final String VAR_TRANSIENT_CANDIDATE_GROUP_ID = "candidateGroupId";

    @Override
    public void beforeCreateHumanTask(final CreateHumanTaskBeforeContext context) {
        setCandidateGroup(context);
    }

    @Override
    public void afterCreateHumanTask(final CreateHumanTaskAfterContext context) {
        // Do nothing
    }

    private void setCandidateGroup(final CreateHumanTaskBeforeContext context) {
        final String candidateGroupId = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TRANSIENT_CANDIDATE_GROUP_ID);
        if (candidateGroupId != null) {
            context.setCandidateGroups(List.of(candidateGroupId));
        }
    }
}
