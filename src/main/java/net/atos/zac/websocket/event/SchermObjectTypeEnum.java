/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import static net.atos.zac.event.OperatieEnum.TOEVOEGING;
import static net.atos.zac.event.OperatieEnum.VERWIJDERING;
import static net.atos.zac.event.OperatieEnum.WIJZIGING;

import java.net.URI;
import java.util.UUID;

import org.flowable.task.api.TaskInfo;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.event.OperatieEnum;

/**
 * Enumeratie die de soorten object wijzigingen bevat zoals die gebruikt worden door het {@link SchermUpdateEvent}.
 */
public enum SchermObjectTypeEnum {

    /**
     * indicatie dat een operatie op een document is opgetreden
     */
    DOCUMENT {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final EnkelvoudigInformatieobject document) {
            return instance(operatie, this, document);
        }
    },

    /**
     * indicatie dat eem operatie op een taak is opgetreden
     */
    TAAK {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final TaskInfo taak) {
            return instance(operatie, this, taak);
        }
    },

    /**
     * indicatie dat er een operatie op een zaak is opgetreden
     */
    ZAAK {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final Zaak zaak) {
            return instance(operatie, this, zaak);
        }
    },

    /**
     * indicatie dat een operatie op een of meer documenten van de opgegeven zaak is opgetreden
     */
    ZAAK_DOCUMENTEN {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final Zaak zaak) {
            return instance(operatie, this, zaak);
        }
    },

    /**
     * indicatie dat een operatie op een of meer taken van de opgegeven zaak is opgetreden
     */
    ZAAK_TAKEN {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final Zaak zaak) {
            return instance(operatie, this, zaak);
        }
    },

    /**
     * indicatie dat een operatie op een of meer betrokkenen van de opgegeven zaak is opgetreden
     */
    ZAAK_BETROKKENEN {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final Zaak zaak) {
            return instance(operatie, this, zaak);
        }
    },

    /**
     * indicatie dat een operatie op een of meer aan de opgegeven zaak gekoppelde zaken is opgetreden
     */
    ZAAK_ZAKEN {
        @Override
        protected SchermUpdateEvent event(final OperatieEnum operatie, final Zaak zaak) {
            return instance(operatie, this, zaak);
        }
    };

    // Dit is de uiteindelijke echte factory method
    private static SchermUpdateEvent instance(final OperatieEnum operatie, final SchermObjectTypeEnum type, final String id) {
        return new SchermUpdateEvent(operatie, type, id);
    }

    // Bij deze methods bepaal je zelf wat er als id gebruikt wordt, let er op dat dit consistent is met de andere methods
    private static SchermUpdateEvent instance(final OperatieEnum operatie, final SchermObjectTypeEnum type, final UUID uuid) {
        return instance(operatie, type, uuid.toString());
    }

    private static SchermUpdateEvent instance(final OperatieEnum operatie, final SchermObjectTypeEnum type, final URI url) {
        return instance(operatie, type, URIUtil.parseUUIDFromResourceURI(url));
    }

    // Deze methods bepalen wat er als id gebruikt wordt, zodat dit overal hetzelfde is
    private static SchermUpdateEvent instance(final OperatieEnum operatie, final SchermObjectTypeEnum type, final Zaak zaak) {
        return instance(operatie, type, zaak.getUuid());
    }

    private static SchermUpdateEvent instance(final OperatieEnum operatie, final SchermObjectTypeEnum type, final EnkelvoudigInformatieobject document) {
        return instance(operatie, type, document.getUrl());
    }

    private static SchermUpdateEvent instance(final OperatieEnum operatie, final SchermObjectTypeEnum type, final TaskInfo taak) {
        return instance(operatie, type, taak.getId());
    }

    // Deze methods bepalen op welke object types de verschillende argumenten zijn toegestaan
    private SchermUpdateEvent event(final OperatieEnum operatie, final UUID uuid) {
        return instance(operatie, this, uuid); // Toegestaan bij alle objecttypes
    }

    private SchermUpdateEvent event(final OperatieEnum operatie, final URI url) {
        return instance(operatie, this, URIUtil.parseUUIDFromResourceURI(url)); // Toegestaan bij alle objecttypes
    }

    protected SchermUpdateEvent event(final OperatieEnum operatie, final Zaak zaak) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    protected SchermUpdateEvent event(final OperatieEnum operatie, final EnkelvoudigInformatieobject document) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    protected SchermUpdateEvent event(final OperatieEnum operatie, final TaskInfo taak) {
        throw new IllegalArgumentException(); // Niet toegestaan behalve bij objecttypes waar deze method een override heeft
    }

    // Dit zijn factory methods om handig en eenduidig SchermUpdateEvents te maken voor een objecttype

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander toevoeging methods.
     *
     * @param uuid de indentificatie van het toegevoegde object.
     * @return een instance van het event
     */
    public final SchermUpdateEvent toevoeging(final UUID uuid) {
        return event(TOEVOEGING, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander toevoeging methods.
     *
     * @param url de indentificatie van het toegevoegde object.
     * @return een instance van het event
     */
    public final SchermUpdateEvent toevoeging(final URI url) {
        return event(TOEVOEGING, url);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de toegevoegde zaak.
     * @return een instance van het event
     */
    public final SchermUpdateEvent toevoeging(final Zaak zaak) {
        return event(TOEVOEGING, zaak);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een document).
     *
     * @param document het toegevoegde document.
     * @return een instance van het event
     */
    public final SchermUpdateEvent toevoeging(final EnkelvoudigInformatieobject document) {
        return event(TOEVOEGING, document);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een taak).
     *
     * @param taak de toegevoegde taak.
     * @return een instance van het event
     */
    public final SchermUpdateEvent toevoeging(final TaskInfo taak) {
        return event(TOEVOEGING, taak);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander wijziging methods.
     *
     * @param uuid de indentificatie van het gewijzigde object.
     * @return eem instance van het event
     */
    public final SchermUpdateEvent wijziging(final UUID uuid) {
        return event(WIJZIGING, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander toevoeging methods.
     *
     * @param url de indentificatie van het gewijzigde object.
     * @return een instance van het event
     */
    public final SchermUpdateEvent wijziging(final URI url) {
        return event(WIJZIGING, url);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de gewijzigde zaak.
     * @return een instance van het event
     */
    public final SchermUpdateEvent wijziging(final Zaak zaak) {
        return event(WIJZIGING, zaak);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een document).
     *
     * @param document het gewijzigde document.
     * @return een instance van het event
     */
    public final SchermUpdateEvent wijziging(final EnkelvoudigInformatieobject document) {
        return event(WIJZIGING, document);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een taak).
     *
     * @param taak de gewijzigde taak.
     * @return een instance van het event
     */
    public final SchermUpdateEvent wijziging(final TaskInfo taak) {
        return event(WIJZIGING, taak);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste UUID mee te geven.
     * Gebruik bij voorkeur de ander verwijdering methods.
     *
     * @param uuid de indentificatie van het verwijderde object.
     * @return een instance van het event
     */
    public final SchermUpdateEvent verwijdering(final UUID uuid) {
        return event(VERWIJDERING, uuid);
    }

    /**
     * Let op! Als je deze method gebruikt ben je er zelf verantwoordelijk voor om de juiste URI mee te geven.
     * Gebruik bij voorkeur de ander toevoeging methods.
     *
     * @param url de indentificatie van het verwijderd object.
     * @return een instance van het event
     */
    public final SchermUpdateEvent verwijdering(final URI url) {
        return event(VERWIJDERING, url);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een zaak).
     *
     * @param zaak de verwijderde zaak.
     * @return een instance van het event
     */
    public final SchermUpdateEvent verwijdering(final Zaak zaak) {
        return event(VERWIJDERING, zaak);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een document).
     *
     * @param document het verwijderde document.
     * @return een instance van het event
     */
    public final SchermUpdateEvent verwijdering(final EnkelvoudigInformatieobject document) {
        return event(VERWIJDERING, document);
    }

    /**
     * Factory method voor SchermUpdateEvent (met identificatie van een taak).
     *
     * @param taak de verwijderde taak.
     * @return een instance van het event
     */
    public final SchermUpdateEvent verwijdering(final TaskInfo taak) {
        return event(VERWIJDERING, taak);
    }
}
