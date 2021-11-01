/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache;

/* Dit is in feite een enum maar in de CacheResult annotatie kunnen alleen een string constanten gebruikt worden.
   Vandaar deze util implementatie.
 */
public class CacheEnum {

    public static final String ZTC_RESULTAATTYPE = "ztc-resultaattype-cache";

    public static final String ZTC_STATUSTYPE = "ztc-statustype-cache";

    public static final String ZTC_ZAAKTYPE = "ztc-zaaktype-cache";

    public static final String ZTC_ZAAKTYPE_RESULTAATTYPE = "ztc-zaaktype-resultaattype-cache";

    public static final String ZTC_ZAAKTYPE_ROLTYPE = "ztc-zaaktype-roltype-cache";

    public static final String ZTC_ZAAKTYPE_STATUSTYPE = "ztc-zaaktype-statustype-cache";

    private CacheEnum() {
    }
}
