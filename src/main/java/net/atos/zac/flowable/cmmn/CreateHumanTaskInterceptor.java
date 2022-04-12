/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import static net.atos.zac.flowable.FlowableService.VAR_TASK_TAAKDATA;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.flowable.cmmn.engine.interceptor.CreateHumanTaskAfterContext;
import org.flowable.cmmn.engine.interceptor.CreateHumanTaskBeforeContext;

import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.FlowableHelper;
import net.atos.zac.signalering.event.SignaleringEvent;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventType;

public class CreateHumanTaskInterceptor implements org.flowable.cmmn.engine.interceptor.CreateHumanTaskInterceptor {

    public static final String VAR_TASK_ZAAK_UUID = "zaakUUID";

    public static final String VAR_TASK_DUE_DATE = "dueDate";

    public static final String VAR_TASK_CANDIDATE_GROUP = "candidateGroupId";

    public static final String VAR_TASK_ASSIGNEE = "assignee";

    public static final String VAR_TASK_OWNER = "owner";

    @Override
    public void beforeCreateHumanTask(final CreateHumanTaskBeforeContext context) {
        final String owner = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_OWNER);
        if (owner != null) {
            context.setOwner(owner);
        }
        final String candidateGroupId = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_CANDIDATE_GROUP);
        if (candidateGroupId != null) {
            context.setCandidateGroups(List.of(candidateGroupId));
        }
        final String assignee = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_ASSIGNEE);
        if (assignee != null) {
            context.setAssignee(assignee);
        }
    }

    @Override
    public void afterCreateHumanTask(final CreateHumanTaskAfterContext context) {
        final Map<String, String> taakdata = (Map<String, String>) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_TAAKDATA);
        FlowableHelper.getInstance().getFlowableService().updateTaakdata(context.getTaskEntity().getId(), taakdata);
        context.getTaskEntity().setDueDate((Date) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_DUE_DATE));
        final UUID zaakUUID = (UUID) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TASK_ZAAK_UUID);
        final ScreenEvent screenEvent = ScreenEventType.ZAAK_TAKEN.updated(zaakUUID);
        // Wait some time before sending the event to the screen to make sure that the task is created.
        screenEvent.setDelay(true);
        FlowableHelper.getInstance().getEventingService().send(screenEvent);

        if (context.getTaskEntity().getAssignee() != null) {
            // On creation of a human task the owner is assumed to be the actor who created it
            final Medewerker actor = context.getHumanTask().getOwner() != null ?
                    FlowableHelper.getInstance().createMedewerker(context.getHumanTask().getOwner())
                    : null;
            final SignaleringEvent<String> signaleringEvent = SignaleringEventUtil.event(SignaleringType.Type.TAAK_OP_NAAM, context.getTaskEntity(), actor);
            FlowableHelper.getInstance().getEventingService().send(signaleringEvent);
        }
    }
}
