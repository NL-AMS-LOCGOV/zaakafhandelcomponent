/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import net.atos.zac.event.AbstractUpdateEvent;
import net.atos.zac.event.OperatieEnum;

/**
 * Dit event wordt gebruikt voor het doorgeven van een service-laag aanpassing naar een update van de web-pagina.
 * 1. Bij sommige wijzigingen in de service-laag wordt een event gegooid.
 * 2. Vanuit de service-laag wordt de ObjectEventProducerImpl aangeroepen
 * 3. De ObjectEventProducerImpl gooit objecten o.a. dit object (SchermUpdateEvent) naar de queue: /jms/queue/SchermUpdate
 * 4. WebSocketsObjectMessageDrivenBean haalt het object weer van de queue en gooit deze via webSockets naar de browser.
 */
public class SchermUpdateEvent extends AbstractUpdateEvent<ObjectTypeEnum, String> {

    private static final long serialVersionUID = -740125186878024703L;

    @NotNull
    private ObjectTypeEnum objectType;

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
        super(operatie, objectId);
        this.objectType = objectType;
    }

    @Override
    public ObjectTypeEnum getObjectType() {
        return objectType;
    }
}
