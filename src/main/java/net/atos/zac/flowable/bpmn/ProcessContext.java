/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.bpmn;

import java.io.Serializable;

import javax.inject.Named;

import org.flowable.cdi.annotation.BusinessProcessScoped;

/**
 *
 */
@Named("context")
@BusinessProcessScoped
public class ProcessContext implements Serializable {

    private static final long serialVersionUID = -5864458093277400941L;

    private String trace;

    public String getTrace() {
        return trace;
    }

    public void setTrace(final String trace) {
        this.trace = trace;
    }
}
