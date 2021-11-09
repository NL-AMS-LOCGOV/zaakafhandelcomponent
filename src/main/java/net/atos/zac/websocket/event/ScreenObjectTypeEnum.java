/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import static net.atos.zac.event.OpcodeEnum.CREATE;
import static net.atos.zac.event.OpcodeEnum.DELETE;
import static net.atos.zac.event.OpcodeEnum.UPDATE;

import java.net.URI;
import java.util.UUID;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.OpcodeEnum;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link ScreenUpdateEvent}.
 */
public enum ScreenObjectTypeEnum {

    ENKELVOUDIG_INFORMATIEOBJECT {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
            return instance(action, this, enkelvoudigInformatieobject);
        }
    },

    TAAK {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final TaskInfo taak) {
            return instance(action, this, taak);
        }
    },

    ZAAK {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_INFORMATIEOBJECT {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_TAKEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_BETROKKENEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    },

    ZAAK_ZAKEN {
        @Override
        protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
            return instance(action, this, zaak);
        }
    };

    // Dit is de uiteindelijke echte factory method
    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final String id) {
        return new ScreenUpdateEvent(action, type, id);
    }

    // Bij deze methods bepaal je zelf wat er als id gebruikt wordt, let er op dat dit consistent is met de andere methods
    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final UUID uuid) {
        return instance(action, type, uuid.toString());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final URI url) {
        return instance(action, type, URIUtil.parseUUIDFromResourceURI(url));
    }

    // Deze methods bepalen wat er als id gebruikt wordt, zodat dit overal hetzelfde is
    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final Zaak zaak) {
        return instance(action, type, zaak.getUuid());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type,
            final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return instance(action, type, enkelvoudigInformatieobject.getUrl());
    }

    private static ScreenUpdateEvent instance(final OpcodeEnum action, final ScreenObjectTypeEnum type, final TaskInfo taak) {
        return instance(action, type, taak.getId());
    }

    // Deze methods bepalen op welke object types de verschillende argumenten zijn toegestaan
    private ScreenUpdateEvent event(final OpcodeEnum action, final UUID uuid) {
        return instance(action, this, uuid); // Toegestaan bij alle objecttypes
    }

    private ScreenUpdateEvent event(final OpcodeEnum action, final URI url) {
        return instance(action, this, URIUtil.parseUUIDFromResourceURI(url)); // Toegestaan bij alle objecttypes
    }

    protected ScreenUpdateEvent event(final OpcodeEnum action, final Zaak zaak) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    protected ScreenUpdateEvent event(final OpcodeEnum action, final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    protected ScreenUpdateEvent event(final OpcodeEnum action, final TaskInfo taak) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    // Dit zijn factory methods om handig en eenduidig SchermUpdateEvents te maken voor een objecttype

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param uuid de indentificatie van het toegevoegde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent creation(final UUID uuid) {
        return event(CREATE, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param url de indentificatie van het toegevoegde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent creation(final URI url) {
        return event(CREATE, url);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de toegevoegde zaak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent creation(final Zaak zaak) {
        return event(CREATE, zaak);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een enkelvoudigInformatieobject).
     *
     * @param enkelvoudigInformatieobject het toegevoegde enkelvoudigInformatieobject.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent creation(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(CREATE, enkelvoudigInformatieobject);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een taak).
     *
     * @param taak de toegevoegde taak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent creation(final TaskInfo taak) {
        return event(CREATE, taak);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander wijziging methods.
     *
     * @param uuid de indentificatie van het gewijzigde object.
     * @return eem instance van het event
     */
    public final ScreenUpdateEvent update(final UUID uuid) {
        return event(UPDATE, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param url de indentificatie van het gewijzigde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent update(final URI url) {
        return event(UPDATE, url);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de gewijzigde zaak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent update(final Zaak zaak) {
        return event(UPDATE, zaak);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een enkelvoudigInformatieobject).
     *
     * @param enkelvoudigInformatieobject het gewijzigde enkelvoudigInformatieobject.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent update(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(UPDATE, enkelvoudigInformatieobject);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een taak).
     *
     * @param taak de gewijzigde taak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent update(final TaskInfo taak) {
        return event(UPDATE, taak);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander deletion methods.
     *
     * @param uuid de indentificatie van het verwijderde object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deletion(final UUID uuid) {
        return event(DELETE, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander creation methods.
     *
     * @param url de indentificatie van het verwijderd object.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deletion(final URI url) {
        return event(DELETE, url);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de verwijderde zaak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deletion(final Zaak zaak) {
        return event(DELETE, zaak);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een enkelvoudigInformatieobject).
     *
     * @param enkelvoudigInformatieobject het verwijderde enkelvoudigInformatieobject.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deletion(final EnkelvoudigInformatieobject enkelvoudigInformatieobject) {
        return event(DELETE, enkelvoudigInformatieobject);
    }

    /**
     * Factory method voor ScreenUpdateEvent (met identificatie van een taak).
     *
     * @param taak de verwijderde taak.
     * @return een instance van het event
     */
    public final ScreenUpdateEvent deletion(final TaskInfo taak) {
        return event(DELETE, taak);
    }
}
