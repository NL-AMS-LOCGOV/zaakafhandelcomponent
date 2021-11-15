package net.atos.client.zgw.shared.cache;

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

    // Deze cache is managed (alleen zaak URI als sleutel gebruiken!)
    String ZGW_ZAAK_BEHANDELAAR_MANAGED = "zgw-zaak-behandelaar-managed";

    // Deze cache is managed (alleen zaak URI als sleutel gebruiken!)
    String ZGW_ZAAK_GROEP_MANAGED = "zgw-zaak-groep-managed";

    // Deze cache is managed (alleen URI als sleutel gebruiken!)
    String ZRC_STATUS_MANAGED = "zrc-status-managed";

    String ZTC_RESULTAATTYPE = "ztc-resultaattype";

    String ZTC_STATUSTYPE = "ztc-statustype";

    String ZTC_ZAAKTYPE = "ztc-zaaktype";

    // Deze cache is managed (alleen URI en UUID als sleutel gebruiken!)
    String ZTC_ZAAKTYPE_MANAGED = "ztc-zaaktype-managed";

    // Deze cache is managed (alleen zaaktype URI als sleutel gebruiken!)
    String ZTC_ZAAKTYPE_RESULTAATTYPE_MANAGED = "ztc-zaaktype-resultaattype-managed";

    String ZTC_ZAAKTYPE_ROLTYPE = "ztc-zaaktype-roltype";

    // Deze cache is managed (alleen zaaktype URI als sleutel gebruiken!)
    String ZTC_ZAAKTYPE_STATUSTYPE_MANAGED = "ztc-zaaktype-statustype-managed";

    String ZTC_ZAAKTYPE_URL = "ztc-zaaktype-url";

    default void cleared(final String cache) {
        LOG.info(String.format("Cleared %s cache", cache));
    }

    default <KEY> void removed(final String cache, final KEY key) {
        LOG.info(String.format("Removed from %s cache: %s", cache, key.toString()));
    }
}
