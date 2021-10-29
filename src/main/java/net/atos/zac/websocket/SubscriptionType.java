/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;


import javax.websocket.Session;

import net.atos.zac.websocket.event.SchermUpdateEvent;

/**
 * Geeft aan of een client zich abonneert op een event,
 * of dat een client een event (of alle events) juist niet meer wil ontvangen
 */
public enum SubscriptionType {
    /**
     * Abonneren op een event
     */
    TOEVOEGEN {
        @Override
        protected void register(final SessionRegistry registry, final Session session, final SchermUpdateEvent event) {
            registry.add(event, session);
        }
    },
    /**
     * Abonnement op een event opzeggen
     */
    VERWIJDEREN {
        @Override
        protected void register(final SessionRegistry registry, final Session session, final SchermUpdateEvent event) {
            registry.remove(event, session);
        }
    },
    /**
     * Abonnementen op alle events opzeggen
     */
    VERWIJDER_ALLES {
        // 1 instantie is wel genoeg voor dit immutable object.
        private final SubscriptionMessage MESSAGE = new SubscriptionMessage(this, null);

        @Override
        protected void register(final SessionRegistry registry, final Session session, final SchermUpdateEvent event) {
            registry.removeAll(session);
        }

        @Override
        public SubscriptionMessage message(final SchermUpdateEvent event) {
            if (event != null) {
                throw new IllegalArgumentException("Onverwacht event argument");
            }
            return message();
        }

        @Override
        public SubscriptionMessage message() {
            return MESSAGE;
        }
    };

    protected abstract void register(final SessionRegistry registry, final Session session, final SchermUpdateEvent event);

    /**
     * Factory method voor SubscriptionMessages (types TOEVOEGEN en VERWIJDEREN).
     *
     * @param event het event waarop geabonneerd wordt.
     * @return het bericht
     */
    public SubscriptionMessage message(final SchermUpdateEvent event) {
        if (event != null) {
            return new SubscriptionMessage(this, event);
        }
        return message();
    }

    /**
     * Factory method voor SubscriptionMessages (type VERWIJDER_ALLES).
     *
     * @return het bericht
     */
    public SubscriptionMessage message() {
        throw new IllegalArgumentException("Ontbrekend event argument");
    }

    /**
     * Bericht wat afhankelijk van het subscriptionType aangeeft aan op welk event de client zich wil abonneren,
     * of welk event de client juist niet meer wil ontvangen.
     * <p>
     * Geen public constructor, gebruik de factory methods!
     */
    public static final class SubscriptionMessage {

        private final SubscriptionType subscriptionType;

        private final SchermUpdateEvent event;

        private SubscriptionMessage(final SubscriptionType subscriptionType, final SchermUpdateEvent event) {
            this.subscriptionType = subscriptionType;
            this.event = event;
        }

        public final SubscriptionType getSubscriptionType() {
            return subscriptionType;
        }

        public final SchermUpdateEvent getEvent() {
            return event;
        }

        /**
         * Verwerk dit bericht bij een van de sessions in een session registry.
         *
         * @param registry de session registry waarin het bericht verwerkt moet worden
         * @param session  de session waarbij het bericht verwerkt moet worden
         */
        public final void register(final SessionRegistry registry, final Session session) {
            subscriptionType.register(registry, session, event);
        }
    }
}
