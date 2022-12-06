/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.flowable.cmmn.engine.interceptor.CreateHumanTaskAfterContext;
import org.flowable.cmmn.engine.interceptor.CreateHumanTaskBeforeContext;

import net.atos.zac.flowable.FlowableHelper;
import net.atos.zac.identity.model.User;
import net.atos.zac.signalering.event.SignaleringEvent;
import net.atos.zac.signalering.event.SignaleringEventUtil;
import net.atos.zac.signalering.model.SignaleringType;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventType;

public class CreateHumanTaskInterceptor implements org.flowable.cmmn.engine.interceptor.CreateHumanTaskInterceptor {

    public static final String VAR_TRANSIENT_TAAKDATA = "taakdata";

    public static final String VAR_TRANSIENT_ZAAK_UUID = "zaakUUID";

    public static final String VAR_TRANSIENT_DUE_DATE = "dueDate";

    public static final String VAR_TRANSIENT_CANDIDATE_GROUP = "candidateGroupId";

    public static final String VAR_TRANSIENT_ASSIGNEE = "assignee";

    public static final String VAR_TRANSIENT_OWNER = "owner";

    // This must be lower than the DEFAULT_SUSPENSION_TIMEOUT defined in websockets.service.ts
    private static final int SECONDS_TO_DELAY = 3;

    @Override
    public void beforeCreateHumanTask(final CreateHumanTaskBeforeContext context) {
        final String owner = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TRANSIENT_OWNER);
        if (owner != null) {
            context.setOwner(owner);
        }
        final String candidateGroupId = (String) context.getPlanItemInstanceEntity()
                .getTransientVariable(VAR_TRANSIENT_CANDIDATE_GROUP);
        if (candidateGroupId != null) {
            context.setCandidateGroups(List.of(candidateGroupId));
        }
        final String assignee = (String) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TRANSIENT_ASSIGNEE);
        if (assignee != null) {
            context.setAssignee(assignee);
        }
    }

    @Override
    public void afterCreateHumanTask(final CreateHumanTaskAfterContext context) {
        final Map<String, String> taakdata = (Map<String, String>) context.getPlanItemInstanceEntity()
                .getTransientVariable(VAR_TRANSIENT_TAAKDATA);
        FlowableHelper.getInstance().getTaskVariablesService().setTaakdata(context.getTaskEntity().getId(), taakdata);
        context.getTaskEntity()
                .setDueDate((Date) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TRANSIENT_DUE_DATE));
        final UUID zaakUUID = (UUID) context.getPlanItemInstanceEntity().getTransientVariable(VAR_TRANSIENT_ZAAK_UUID);
        final ScreenEvent screenEvent = ScreenEventType.ZAAK_TAKEN.updated(zaakUUID);
        // Wait some time before sending the event to make sure that the task is created.
        screenEvent.setDelay(SECONDS_TO_DELAY);
        FlowableHelper.getInstance().getEventingService().send(screenEvent);

        if (context.getTaskEntity().getAssignee() != null) {
            // On creation of a human task it's owner is assumed to be the actor who created it.
            final String owner = context.getHumanTask().getOwner();
            final User user = owner != null ? FlowableHelper.getInstance().getIdentityService().readUser(owner) : null;
            final SignaleringEvent<?> signaleringEvent =
                    SignaleringEventUtil.event(SignaleringType.Type.TAAK_OP_NAAM, context.getTaskEntity(), user);
            // Wait some time before sending the event to make sure that the task is created.
            signaleringEvent.setDelay(SECONDS_TO_DELAY);
            FlowableHelper.getInstance().getEventingService().send(signaleringEvent);
        }
        FlowableHelper.getInstance().getIndexeerService().addTaak(context.getTaskEntity().getId());
    }
}
