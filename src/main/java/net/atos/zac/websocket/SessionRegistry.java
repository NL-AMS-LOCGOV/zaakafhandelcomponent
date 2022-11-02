/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import net.atos.zac.event.Opcode;
import net.atos.zac.websocket.event.ScreenEvent;
import net.atos.zac.websocket.event.ScreenEventId;
import net.atos.zac.websocket.event.ScreenEventType;

/**
 * This Registry is used to maintain a list of active sessions.
 * EventSessions contains all (from the browser) registered client sessions
 */
@ApplicationScoped
public class SessionRegistry {

    private static final Pattern QUOTED = Pattern.compile("^\"(.*)\"$");

    private final SetMultimap<ScreenEvent, Session> eventSessions = Multimaps.synchronizedSetMultimap(
            HashMultimap.create());

    /**
     * Return a set of all active sessions for a particular event.
     *
     * @return Set with active sessions
     */
    public Set<Session> listSessions(final ScreenEvent event) {
        return Collections.unmodifiableSet(eventSessions.get(fix(event)));
    }

    /**
     * Add a session for a specific event
     *
     * @param wildcarded event
     * @param session    session
     */
    public void create(final ScreenEvent wildcarded, final Session session) {
        if (session != null) {
            glob(fix(wildcarded)).forEach(event -> eventSessions.put(event, session));
        }
    }

    /**
     * Delete a session for a specific event
     *
     * @param wildcarded event
     * @param session    session
     */
    public void delete(final ScreenEvent wildcarded, final Session session) {
        if (session != null) {
            glob(fix(wildcarded)).forEach(event -> eventSessions.get(event).remove(session));
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

    private List<ScreenEvent> glob(final ScreenEvent event) {
        if (event.getOpcode() == Opcode.ANY) {
            final Set<Opcode> anyOpcode = Opcode.any();
            anyOpcode.remove(
                    Opcode.CREATED);// There will not be any websocket subscriptions with this opcode, so skip it in globbing.
            if (event.getObjectType() == ScreenEventType.ANY) {
                return anyOpcode.stream()
                        .flatMap(operation -> ScreenEventType.any().stream()
                                .map(objecType -> new Wrapper(operation, objecType)))
                        .map(wrapper -> new ScreenEvent(wrapper.opcode, wrapper.objecType, event.getObjectId()))
                        .collect(Collectors.toList());
            }
            return anyOpcode.stream()
                    .map(operation -> new ScreenEvent(operation, event.getObjectType(), event.getObjectId()))
                    .collect(Collectors.toList());
        }
        if (event.getObjectType() == ScreenEventType.ANY) {
            return ScreenEventType.any().stream()
                    .map(objectType -> new ScreenEvent(event.getOpcode(), objectType, event.getObjectId()))
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(event);
    }

    private static class Wrapper {
        private final Opcode opcode;

        private final ScreenEventType objecType;

        private Wrapper(final Opcode opcode, final ScreenEventType objecType) {
            this.opcode = opcode;
            this.objecType = objecType;
        }
    }

    /**
     * This method is applied to all event arguments to make sure that the objectId being quoted (by Angular?) doesn't cause any problems.
     * Events that are otherwise equal except for the quoted/unquoted objectIds should in all cases be regarded as the same event.
     *
     * @param event the event in which the objectId may have been quoted
     * @return an event in which the objectId is stripped of any quotes
     */
    public ScreenEvent fix(final ScreenEvent event) {
        final String resource = fix(event.getObjectId().getResource());
        final String detail = fix(event.getObjectId().getDetail());
        if (resource != null || detail != null) {
            return new ScreenEvent(event.getOpcode(), event.getObjectType(),
                                   new ScreenEventId(resource, detail));
        }
        return event;
    }

    private String fix(final String id) {
        if (id != null) {
            final Matcher matcher = QUOTED.matcher(id);
            if (matcher.matches()) {
                return fix(matcher.replaceAll("$1"));
            }
        }
        return id;
    }
}
