/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static net.atos.zac.notificaties.Resource.APPLICATIE;
import static net.atos.zac.notificaties.Resource.BESLUIT;
import static net.atos.zac.notificaties.Resource.BESLUITTYPE;
import static net.atos.zac.notificaties.Resource.INFORMATIEOBJECT;
import static net.atos.zac.notificaties.Resource.INFORMATIEOBJECTTYPE;
import static net.atos.zac.notificaties.Resource.ZAAK;
import static net.atos.zac.notificaties.Resource.ZAAKTYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Enumeratie die de kanalen bevat zoals die binnenkomen op de {@link NotificatieReceiver}.
 * <p>
 * http://open-zaak.default/ref/kanalen/
 */
public enum Channel {
    AUTORISATIES("autorisaties", APPLICATIE),
    BESLUITEN("besluiten", BESLUIT),
    BESLUITTYPEN("besluittypen", BESLUITTYPE),
    INFORMATIEOBJECTEN("documenten", INFORMATIEOBJECT),
    INFORMATIEOBJECTTYPEN("informatieobjecttypen", INFORMATIEOBJECTTYPE),
    ZAKEN("zaken", ZAAK),
    ZAAKTYPEN("zaaktypen", ZAAKTYPE);

    private static final Logger LOG = Logger.getLogger(Channel.class.getName());

    private final String code;

    private final Resource resourceType;

    private static final Map<String, Channel> VALUES = new HashMap<>();

    static {
        for (final Channel value : values()) {
            VALUES.put(value.code, value);
        }
    }

    Channel(final String code, final Resource resourceType) {
        this.code = code;
        this.resourceType = resourceType;
    }

    public Resource getResourceType() {
        return resourceType;
    }

    public static Channel value(final String code) {
        final Channel value = VALUES.get(code);
        if (value == null) {
            LOG.warning(String.format("unknown %s channel", code));
        }
        return value;
    }
}
