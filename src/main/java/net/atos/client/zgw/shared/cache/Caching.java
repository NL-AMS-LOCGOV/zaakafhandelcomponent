/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache;

import java.util.List;
import java.util.logging.Logger;

/**
 * Let op!
 * <p>
 * Methods met caching NOOIT van binnen de service aanroepen (anders werkt de caching niet).
 * En bij managed caches geen sleutels anders dan URI en UID introduceren.
 * Bij caches waarbij het resultaat null kan zijn Optional gebruiken want null wordt niet gecachet.
 */
public interface Caching {
    Logger LOG = Logger.getLogger(Caching.class.getName());

    String ZTC_CACHE_TIME = "ztc-cache-datetime";

    String ZTC_RESULTAATTYPE = "ztc-resultaattype";

    String ZTC_BESLUITTYPE = "ztc-besluittype";

    String ZTC_STATUSTYPE = "ztc-statustype";

    String ZTC_INFORMATIEOBJECTTYPE = "ztc-informatieobjecttype";

    String ZTC_ZAAKTYPE = "ztc-zaaktype";

    String ZTC_ROLTYPE = "ztc-roltype";

    String ZTC_ZAAKTYPE_INFORMATIEOBJECTTYPE = "ztc-zaaktypeinformatieobjecttype";

    String ZAC_ZAAKAFHANDELPARAMETERS_MANAGED = "zac-zaakafhandelparameters-read";

    String ZAC_ZAAKAFHANDELPARAMETERS = "zac-zaakafhandelparameters-list";

    List<String> cacheNames();

    default String cleared(final String cache) {
        final String message = String.format("%s cache cleared", cache);
        LOG.info(message);
        return message;
    }

    default <KEY> void removed(final String cache, final KEY key) {
        LOG.fine(() -> String.format("Removed from %s cache: %s", cache, key.toString()));
    }
}
