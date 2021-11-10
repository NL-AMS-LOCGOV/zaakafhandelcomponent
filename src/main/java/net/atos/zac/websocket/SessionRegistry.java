/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Singleton;
import javax.websocket.Session;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import net.atos.zac.websocket.event.ScreenUpdateEvent;

/**
 * This EJB is used to maintain a list of active sessions.
 * EventSessions contains all (from the browser) registered client sessions
 */
@Singleton
public class SessionRegistry {

    private static final Pattern QUOTED = Pattern.compile("^\"(.*)\"$");

    private final SetMultimap<ScreenUpdateEvent, Session> eventSessions = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    /**
     * Return a set of all active sessions for a particular event.
     *
     * @return Set with active sessions
     */
    public Set<Session> listSessions(final ScreenUpdateEvent event) {
        return Collections.unmodifiableSet(eventSessions.get(fix(event)));
    }

    /**
     * Add a session for a specific event
     *
     * @param event   event
     * @param session session
     */
    public void create(final ScreenUpdateEvent event, final Session session) {
        if (session != null) {
            eventSessions.put(fix(event), session);
        }
    }

    /**
     * Delete a session for a specific event
     *
     * @param event   event
     * @param session session
     */
    public void delete(final ScreenUpdateEvent event, final Session session) {
        if (session != null) {
            eventSessions.get(fix(event)).remove(session);
        }
    }

    /**
     * Delete a session for all events
     *
     * @param session session
     */
    public void deleteAll(final Session session) {
        if (session != null) {
            eventSessions.values().removeAll(Collections.singleton(session));
        }
    }

    /**
     * This method is applied to all event arguments to make sure that the opbjectId being quoted (by Angular?) doesn't cause any problems.
     * Events that are otherwise equal except for the quoted/unquoted objectIds should in all cases be regarded as the same event.
     *
     * @param event the event in which the objectId may have been quoted
     * @return an event in which the objectId is stripped of any quotes
     */
    public ScreenUpdateEvent fix(final ScreenUpdateEvent event) {
        final Matcher matcher = QUOTED.matcher(event.getObjectId());
        return matcher.matches() ? fix(new ScreenUpdateEvent(event.getOperation(), event.getObjectType(), matcher.replaceAll("$1"))) : event;
    }
}
