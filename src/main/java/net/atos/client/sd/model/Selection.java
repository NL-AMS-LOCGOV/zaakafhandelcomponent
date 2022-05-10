/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.sd.model;

import javax.json.bind.annotation.JsonbProperty;

public class Selection {

    @JsonbProperty("TemplateGroup")
    public String templateGroup;

    @JsonbProperty("Template")
    public String template;
}
