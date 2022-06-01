/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.shared;

import org.apache.commons.lang3.StringUtils;

import net.atos.zac.shared.model.ListParameters;
import net.atos.zac.shared.model.Paging;
import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.shared.model.Sorting;

public class RESTListParametersConverter {

    public static ListParameters convert(final RESTListParameters restParameters) {
        final ListParameters parameters = new ListParameters();
        if (restParameters == null) {
            return parameters;
        }
        if (StringUtils.isNotBlank(restParameters.sort)) {
            parameters.setSorting(new Sorting(restParameters.sort, SorteerRichting.fromValue(restParameters.order)));
        }
        parameters.setPaging(new Paging(restParameters.page, restParameters.maxResults));
        return parameters;
    }
}