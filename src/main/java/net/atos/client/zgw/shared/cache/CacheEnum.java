/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache;

/* Dit is in feite een enum maar in de CacheResult annotatie kunnen alleen een string constanten gebruikt worden.
   Vandaar deze util implementatie.
 */
public class CacheEnum {

    // Deze cache is managed (alleen zaak URI en UUID? als sleutel gebruiken!)
    public static final String ZRC_ZAAK_STATUS_MANAGED = "zrc-zaak-status-managed-cache";

    // Deze cache is managed (alleen zaak URI en UUID? als sleutel gebruiken!)
    public static final String ZRC_ZAAK_ROL_MANAGED = "zrc-zaak-rol-managed-cache";

    public static final String ZTC_RESULTAATTYPE = "ztc-resultaattype-cache";

    public static final String ZTC_STATUSTYPE = "ztc-statustype-cache";

    public static final String ZTC_ZAAKTYPE = "ztc-zaaktype-cache";

    // Deze cache is managed (alleen URI en UUID als sleutel gebruiken!)
    public static final String ZTC_ZAAKTYPE_MANAGED = "ztc-zaaktype-managed-cache";

    // Deze cache is managed (alleen zaaktype URI als sleutel gebruiken!)
    public static final String ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED = "ztc-zaaktype-resultaattype=managed-cache";

    public static final String ZTC_ZAAKTYPE_ROLTYPE = "ztc-zaaktype-roltype-cache";

    // Deze cache is managed (alleen zaaktype URI als sleutel gebruiken!)
    public static final String ZTC_ZAAKTYPE_STATUSTYPE_MANAGED = "ztc-zaaktype-statustype-managed-cache";

    public static final String ZTC_ZAAKTYPE_URL = "ztc-zaaktype-url-cache";

    private CacheEnum() {
    }
}
