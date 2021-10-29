/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.websocket.Session;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import net.atos.zac.websocket.event.SchermUpdateEvent;

/**
 * Deze class wordt gebruikt voor het bijhouden van een lijst met actieve sessies.
 * eventSessions bevat alle (vanuit de browser) geregistreerde client sessies
 */
@Singleton
public class SessionRegistry {

    private static final Pattern QUOTED = Pattern.compile("^\"(.*)\"$");

    private final SetMultimap<SchermUpdateEvent, Session> eventSessions = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    /**
     * Geef een set met alle actieve sessies voor een bepaald event terug.
     *
     * @return Set met actieve sessies
     */
    public Set<Session> getSessions(final SchermUpdateEvent event) {
        return Collections.unmodifiableSet(eventSessions.get(fix(event)));
    }

    /**
     * Voeg een sessie toe voor een bepaald event
     *
     * @param event   het event
     * @param session de toe te voegen sessie
     */
    public void add(final SchermUpdateEvent event, final Session session) {
        if (session != null) {
            eventSessions.put(fix(event), session);
        }
    }

    /**
     * Verwijder een sessie voor een bepaald event
     *
     * @param event   het event
     * @param session de te verwijderen sessie
     */
    public void remove(final SchermUpdateEvent event, final Session session) {
        if (session != null) {
            eventSessions.get(fix(event)).remove(session);
        }
    }

    /**
     * Verwijder een sessie voor alle events
     *
     * @param session de te verwijderen sessie
     */
    public void removeAll(final Session session) {
        if (session != null) {
            eventSessions.values().removeAll(Collections.singleton(session));
        }
    }

    /**
     * Deze method wordt op alle event argumenten toegepast om ervoor te zorgen dat het eventueel gequote zijn van het opbjectId (door Angular?) geen
     * problemen geeft. Events die behalve de wel/niet gequote objectIds verder gelijk zijn moeten in alle gevallen als hetzelfde event worden beschouwd.
     *
     * @param event het event waarin mogelijk het objectId gequote is
     * @return een event waarin het objectId is ontdaan van de eventuele quotes
     */
    public SchermUpdateEvent fix(final SchermUpdateEvent event) {
        final Matcher matcher = QUOTED.matcher(event.getObjectId());
        return matcher.matches() ? fix(new SchermUpdateEvent(event.getOperatie(), event.getObjectType(), matcher.replaceAll("$1"))) : event;
    }
}
