/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import static net.atos.zac.websocket.SubscriptionType.DELETE_ALL;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import net.atos.zac.event.Opcode;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventId;
import net.atos.zac.websocket.event.ScreenEventType;

/**
 * Converts websocket messages to SubscriptionType.SubscriptionMessage objects
 */
public class WebSocketSubscriptionMessageDecoder implements Decoder.Text<SubscriptionType.SubscriptionMessage> {

    private static final Logger LOG = Logger.getLogger(WebSocketSubscriptionMessageDecoder.class.getName());

    private static final String SUBSCRIPTION_TYPE = "subscriptionType";

    private static final String EVENT = "event";

    private static final String EVENT_OPCODE = "opcode";

    private static final String EVENT_TYPE = "objectType";

    private static final String EVENT_OBJECT_ID = "objectId";

    private static final String EVENT_RESOURCE = "resource";

    @Override
    public SubscriptionType.SubscriptionMessage decode(final String jsonMessage) throws DecodeException {
        try (final JsonReader jsonReader = Json.createReader(new StringReader(jsonMessage))) {
            final JsonObject jsonObject = jsonReader.readObject();

            final SubscriptionType subscriptionType = SubscriptionType.valueOf(jsonObject.getString(SUBSCRIPTION_TYPE));
            if (subscriptionType == DELETE_ALL) {
                return subscriptionType.message();
            }

            final JsonObject jsonEvent = jsonObject.getJsonObject(EVENT);
            final Opcode operatie = Opcode.valueOf(jsonEvent.getString(EVENT_OPCODE));
            final ScreenEventType objectType = ScreenEventType.valueOf(jsonEvent.getString(EVENT_TYPE));
            final String resource = jsonEvent.getJsonObject(EVENT_OBJECT_ID).getString(EVENT_RESOURCE);
            return subscriptionType.message(new ScreenEvent(operatie, objectType, new ScreenEventId(resource, null)));
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
            LOG.log(Level.WARNING, String.format("SubscriptionMessage cannot be decrypted: %s", jsonMessage), e);
            return false;
        }
    }
}
