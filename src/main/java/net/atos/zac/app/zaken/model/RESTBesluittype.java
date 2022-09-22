/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.model;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public class RESTBesluittype {

    public UUID id;

    public String naam;

    public String toelichting;

    public List<URI> informatieobjecttypen;

}
