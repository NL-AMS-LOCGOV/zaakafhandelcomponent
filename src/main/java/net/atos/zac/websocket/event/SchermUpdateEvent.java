/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import java.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import net.atos.zac.event.AbstractUpdateEvent;

/**
 * Dit event wordt gebruikt voor het doorgeven van een service-laag aanpassing naar een update van de web-pagina.
 * 1. Bij sommige wijzigingen in de service-laag wordt een event gegooid.
 * 2. Vanuit de service-laag wordt de ObjectEventProducerImpl aangeroepen
 * 3. De ObjectEventProducerImpl gooit objecten o.a. dit object (SchermUpdateEvent) naar de queue: /jms/queue/SchermUpdate
 * 4. WebSocketsObjectMessageDrivenBean haalt het object weer van de queue en gooit deze via webSockets naar de browser.
 */
public class SchermUpdateEvent extends AbstractUpdateEvent {

    private static final long serialVersionUID = -740125186878024703L;

    private long timestamp;

    @NotNull
    private OperatieEnum operatie;

    @NotNull
    private ObjectTypeEnum objectType;

    @NotBlank
    @Valid
    private String objectId;

    /**
     * Constructor for the sake of JAXB
     */
    public SchermUpdateEvent() {
        super();
    }

    /**
     * Constructor die alle verplichte velden bevat.
     *
     * @param operatie   de operatie die uitgevoerd is op het betreffende object
     * @param objectType het type object waarop de operatie is uitgevoerd
     * @param objectId   de identificatie van het object waarop een operatie is uitgevoerd
     */
    public SchermUpdateEvent(final OperatieEnum operatie, final ObjectTypeEnum objectType, final String objectId) {
        this.timestamp = Instant.now().getEpochSecond();
        this.operatie = operatie;
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public OperatieEnum getOperatie() {
        return operatie;
    }

    public ObjectTypeEnum getObjectType() {
        return objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public boolean equals(final Object obj) {
        // snel antwoord
        if (obj == this) {
            return true;
        }
        // gebruik getClass i.p.v. instanceof, maar dan wel met de null check
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // cast en vergelijk
        final SchermUpdateEvent other = (SchermUpdateEvent) obj;
        if (operatie != other.operatie) {
            return false;
        }
        if (objectType != other.objectType) {
            return false;
        }
        return objectId.equals(other.objectId);
    }

    @Override
    public int hashCode() {
        int result = operatie.hashCode();
        result = 31 * result + objectType.hashCode();
        result = 31 * result + objectId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", getClass().getSimpleName(), operatie, objectType, objectId);
    }
}
