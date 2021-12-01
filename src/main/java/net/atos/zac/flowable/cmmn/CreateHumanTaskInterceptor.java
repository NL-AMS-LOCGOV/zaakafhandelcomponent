/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static net.atos.zac.flowable.cmmn.ExtraheerGroepLifecycleListener.VAR_TASK_CANDIDATE_GROUP_ID;

import java.util.List;
import java.util.UUID;

import org.flowable.cmmn.engine.interceptor.CreateHumanTaskAfterContext;
import org.flowable.cmmn.engine.interceptor.CreateHumanTaskBeforeContext;

import net.atos.zac.flowable.FlowableHelper;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventType;

public class CreateHumanTaskInterceptor implements org.flowable.cmmn.engine.interceptor.CreateHumanTaskInterceptor {

    public static final String VAR_TASK_ZAAK_UUID = "zaakUUID";

    private static final int SECONDS_TO_WAIT = 3;

    @Override
    public void beforeCreateHumanTask(final CreateHumanTaskBeforeContext context) {
        final String candidateGroupId = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_CANDIDATE_GROUP_ID);
        if (candidateGroupId != null) {
            context.setCandidateGroups(List.of(candidateGroupId));
        }
    }

    @Override
    public void afterCreateHumanTask(final CreateHumanTaskAfterContext context) {
        final UUID zaakUUID = (UUID) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_ZAAK_UUID);
        final ScreenEvent screenEvent = ScreenEventType.ZAAK_TAKEN.updated(zaakUUID);
        // Wait a few seconds before sending the event to the screen to make sure that the task is created.
        screenEvent.setSecondsToWait(SECONDS_TO_WAIT);
        FlowableHelper.getInstance().getEventingService().send(screenEvent);
    }
}
