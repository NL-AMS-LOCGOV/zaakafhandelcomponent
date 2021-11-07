/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import static net.atos.zac.websocket.SubscriptionType.VERWIJDER_ALLES;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import net.atos.zac.event.ActionEnum;
import net.atos.zac.websocket.event.ScreenObjectTypeEnum;
import net.atos.zac.websocket.event.ScreenUpdateEvent;

/**
 * Converteert websocket berichten naar SubscriptionType.SubscriptionMessage objecten
 */
public class WebSocketSubscriptionMessageDecoder implements Decoder.Text<SubscriptionType.SubscriptionMessage> {

    private static final Logger LOG = Logger.getLogger(WebSocketSubscriptionMessageDecoder.class.getName());

    private static final String SUBSCRIPTION_TYPE = "subscriptionType";

    private static final String EVENT = "event";

    private static final String EVENT_OPERATIE = "operatie";

    private static final String EVENT_OBJECT_TYPE = "objectType";

    private static final String EVENT_OBJECT_ID = "objectId";

    @Override
    public SubscriptionType.SubscriptionMessage decode(final String jsonMessage) throws DecodeException {
        try (final JsonReader jsonReader = Json.createReader(new StringReader(jsonMessage))) {
            final JsonObject jsonObject = jsonReader.readObject();

            final SubscriptionType subscriptionType = SubscriptionType.valueOf(jsonObject.getString(SUBSCRIPTION_TYPE));
            if (subscriptionType == VERWIJDER_ALLES) {
                return subscriptionType.message();
            }

            final JsonObject jsonEvent = jsonObject.getJsonObject(EVENT);
            final ActionEnum operatie = ActionEnum.valueOf(jsonEvent.getString(EVENT_OPERATIE));
            final ScreenObjectTypeEnum objectType = ScreenObjectTypeEnum.valueOf(jsonEvent.getString(EVENT_OBJECT_TYPE));
            final JsonValue jsonObjectId = jsonEvent.get(EVENT_OBJECT_ID);
            return subscriptionType.message(new ScreenUpdateEvent(operatie, objectType, jsonObjectId != null ? jsonObjectId.toString() : null));
        }
    }

    @Override
    public void init(final EndpointConfig endpointConfig) {
        // NOOP
    }

    @Override
    public void destroy() {
        // NOOP
    }

    @Override
    public boolean willDecode(final String jsonMessage) {
        try (final JsonReader jsonReader = Json.createReader(new StringReader(jsonMessage))) {
            jsonReader.readObject();
            return true;
        } catch (final JsonException e) {
            LOG.log(Level.WARNING, String.format("SubscriptionMessage kan niet gedecodeerd worden: %s", jsonMessage), e);
            return false;
        }
    }
}
