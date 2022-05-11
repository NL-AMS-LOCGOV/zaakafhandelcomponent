/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.sd.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

public class UnattendedResponse {

    @JsonbProperty("file")
    public List<File> files;
}
