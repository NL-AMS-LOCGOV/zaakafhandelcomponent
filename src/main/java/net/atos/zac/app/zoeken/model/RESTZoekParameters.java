/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.util.Map;

import net.atos.zac.zoeken.model.DatumVeld;
import net.atos.zac.zoeken.model.FilterParameters;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.SorteerVeld;
import net.atos.zac.zoeken.model.ZoekVeld;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public class RESTZoekParameters {

    public ZoekObjectType type;

    public Map<ZoekVeld, String> zoeken;

    public Map<FilterVeld, FilterParameters> filters;

    public Map<DatumVeld, RESTDatumRange> datums;

    public SorteerVeld sorteerVeld;

    public String sorteerRichting;

    public int rows;

    public int page;

    public boolean alleenMijnZaken;

    public boolean alleenOpenstaandeZaken;

    public boolean alleenAfgeslotenZaken;

    public boolean alleenMijnTaken;
}
