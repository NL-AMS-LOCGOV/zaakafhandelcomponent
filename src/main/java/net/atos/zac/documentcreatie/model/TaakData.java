/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.model;

import java.util.Map;

public class TaakData {

    public final static String TAAKDATA_BODY = "body";

    public final static String TAAKDATA_EMAILADRES = "emailadres";

    public String naam;

    public String behandelaar;

    public Map<String, String> data;
}
