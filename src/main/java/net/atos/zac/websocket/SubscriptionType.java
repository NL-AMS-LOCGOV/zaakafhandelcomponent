/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;


import javax.websocket.Session;

import net.atos.zac.websocket.event.ScreenUpdateEvent;

/**
 * Indicates whether a client subscribes to an event, or whether a client no longer wishes to receive an event (or all events)
 * Maps to subscription-type.ts
 */
public enum SubscriptionType {
    /**
     * Subscribe to an event
     */
    CREATE {
        @Override
        protected void register(final SessionRegistry registry, final Session session, final ScreenUpdateEvent event) {
            registry.create(event, session);
        }
    },
    /**
     * Cancel subscription to an event
     */
    DELETE {
        @Override
        protected void register(final SessionRegistry registry, final Session session, final ScreenUpdateEvent event) {
            registry.delete(event, session);
        }
    },
    /**
     * Cancel subscriptions to all events
     */
    DELETE_ALL {
        // 1 instance is enough for this immutable object.
        private final SubscriptionMessage MESSAGE = new SubscriptionMessage(this, null);

        @Override
        protected void register(final SessionRegistry registry, final Session session, final ScreenUpdateEvent event) {
            registry.deleteAll(session);
        }

        @Override
        public SubscriptionMessage message(final ScreenUpdateEvent event) {
            if (event != null) {
                throw new IllegalArgumentException("Unexpected event argument");
            }
            return message();
        }

        @Override
        public SubscriptionMessage message() {
            return MESSAGE;
        }
    };

    protected abstract void register(final SessionRegistry registry, final Session session, final ScreenUpdateEvent event);

    /**
     * Factory method for SubscriptionMessages ({@link SubscriptionType#CREATE} and {@link SubscriptionType#DELETE} types).
     *
     * @param event the event subscribed to.
     * @return message
     */
    public SubscriptionMessage message(final ScreenUpdateEvent event) {
        if (event != null) {
            return new SubscriptionMessage(this, event);
        }
        return message();
    }

    /**
     * Factory method for SubscriptionMessages (type {@link SubscriptionType#DELETE_ALL).
     *
     * @return message
     */
    public SubscriptionMessage message() {
        throw new IllegalArgumentException("Missing event argument");
    }

    /**
     * Message which, depending on the subscriptionType, indicates to which event the client wishes to subscribe, or which event the client no longer wishes to receive.
     * <p>
     * No public constructor, use the factory methods!
     */
    public static final class SubscriptionMessage {

        private final SubscriptionType subscriptionType;

        private final ScreenUpdateEvent event;

        private SubscriptionMessage(final SubscriptionType subscriptionType, final ScreenUpdateEvent event) {
            this.subscriptionType = subscriptionType;
            this.event = event;
        }

        public final SubscriptionType getSubscriptionType() {
            return subscriptionType;
        }

        public final ScreenUpdateEvent getEvent() {
            return event;
        }

        /**
         * Process this message at one of the sessions in a session registry.
         *
         * @param registry the session registry in which the message must be processed
         * @param session  the session in which the message is to be processed
         */
        public final void register(final SessionRegistry registry, final Session session) {
            subscriptionType.register(registry, session, event);
        }
    }
}
