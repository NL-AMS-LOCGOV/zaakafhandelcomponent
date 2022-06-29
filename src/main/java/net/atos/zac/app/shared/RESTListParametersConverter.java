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

public abstract class RESTListParametersConverter<LP extends ListParameters, RLP extends RESTListParameters> {

    public LP convert(final RLP restListParameters) {
        final LP listParameters = getListParameters();
        if (restListParameters == null) {
            return listParameters;
        }
        if (StringUtils.isNotBlank(restListParameters.sort)) {
            listParameters.setSorting(new Sorting(restListParameters.sort, SorteerRichting.fromValue(restListParameters.order)));
        }
        listParameters.setPaging(new Paging(restListParameters.page, restListParameters.maxResults));
        doConvert(listParameters, restListParameters);
        return listParameters;
    }

    protected abstract void doConvert(final LP listParameters, final RLP restListParameters);

    protected abstract LP getListParameters();
}
