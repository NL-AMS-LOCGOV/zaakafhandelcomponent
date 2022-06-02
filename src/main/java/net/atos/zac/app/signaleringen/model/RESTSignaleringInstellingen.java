/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.signaleringen.model;

import net.atos.zac.signalering.model.SignaleringSubject;
import net.atos.zac.signalering.model.SignaleringType;

public class RESTSignaleringInstellingen {

    public Long id;

    public SignaleringType.Type type;

    public SignaleringSubject subjecttype;

    public Boolean dashboard;

    public Boolean mail;
}
