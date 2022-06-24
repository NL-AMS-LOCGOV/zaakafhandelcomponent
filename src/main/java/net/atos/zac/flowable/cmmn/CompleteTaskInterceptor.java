/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.cmmn;

import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.interceptor.DefaultCmmnIdentityLinkInterceptor;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import net.atos.zac.flowable.FlowableHelper;

public class CompleteTaskInterceptor extends DefaultCmmnIdentityLinkInterceptor {

    public CompleteTaskInterceptor(final CmmnEngineConfiguration cmmnEngineConfiguration) {
        super(cmmnEngineConfiguration);
    }

    @Override
    public void handleCompleteTask(final TaskEntity task) {
        super.handleCompleteTask(task);
        FlowableHelper.getInstance().getIndexeerService().removeTaak(task.getId());
    }
}
