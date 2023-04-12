/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.bpmn;

import static net.atos.zac.flowable.ZaakVariabelenService.VAR_ZAAK_UUID;
import static net.atos.zac.flowable.cmmn.CreateHumanTaskInterceptor.SECONDS_TO_DELAY;

import java.util.UUID;

import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;

import net.atos.zac.flowable.FlowableHelper;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventType;

public class CreateUserTaskInterceptor implements org.flowable.engine.interceptor.CreateUserTaskInterceptor {

    public static final String VAR_PROCESS_OWNER = "owner";

    @Override
    public void beforeCreateUserTask(final CreateUserTaskBeforeContext context) {
        final String owner = (String) context.getExecution().getVariable(VAR_PROCESS_OWNER);
        if (owner != null) {
            context.setOwner(owner);
        }
    }

    @Override
    public void afterCreateUserTask(final CreateUserTaskAfterContext context) {
        final UUID zaakUUID = (UUID) context.getExecution().getParent().getVariable(VAR_ZAAK_UUID);
        final ScreenEvent screenEvent = ScreenEventType.ZAAK_TAKEN.updated(zaakUUID);
        // Wait some time before handling the event to make sure that the task has been created.
        screenEvent.setDelay(SECONDS_TO_DELAY);
        FlowableHelper.getInstance().getEventingService().send(screenEvent);
        FlowableHelper.getInstance().getIndexeerService().addOrUpdateTaak(context.getTaskEntity().getId());
    }
}
