/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.junit.jupiter.api.Test;

import net.atos.zac.event.OpcodeEnum;
import net.atos.zac.websocket.SessionRegistry;
import net.atos.zac.websocket.event.ScreenObjectTypeEnum;
import net.atos.zac.websocket.event.ScreenUpdateEvent;

public class SessionRegistryTest {

    final static SessionRegistry sessionRegistry = new SessionRegistry();

    final static Session SESSION1 = new Session() {
        @Override
        public WebSocketContainer getContainer() {
            return null;
        }

        @Override
        public void addMessageHandler(final MessageHandler messageHandler) throws IllegalStateException {

        }

        @Override
        public <T> void addMessageHandler(final Class<T> aClass, final MessageHandler.Whole<T> whole) {

        }

        @Override
        public <T> void addMessageHandler(final Class<T> aClass, final MessageHandler.Partial<T> partial) {

        }

        @Override
        public Set<MessageHandler> getMessageHandlers() {
            return null;
        }

        @Override
        public void removeMessageHandler(final MessageHandler messageHandler) {

        }

        @Override
        public String getProtocolVersion() {
            return null;
        }

        @Override
        public String getNegotiatedSubprotocol() {
            return null;
        }

        @Override
        public List<Extension> getNegotiatedExtensions() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public long getMaxIdleTimeout() {
            return 0;
        }

        @Override
        public void setMaxIdleTimeout(final long l) {

        }

        @Override
        public void setMaxBinaryMessageBufferSize(final int i) {

        }

        @Override
        public int getMaxBinaryMessageBufferSize() {
            return 0;
        }

        @Override
        public void setMaxTextMessageBufferSize(final int i) {

        }

        @Override
        public int getMaxTextMessageBufferSize() {
            return 0;
        }

        @Override
        public RemoteEndpoint.Async getAsyncRemote() {
            return null;
        }

        @Override
        public RemoteEndpoint.Basic getBasicRemote() {
            return null;
        }

        @Override
        public String getId() {
            return "1";
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void close(final CloseReason closeReason) throws IOException {

        }

        @Override
        public URI getRequestURI() {
            return null;
        }

        @Override
        public Map<String, List<String>> getRequestParameterMap() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public Map<String, String> getPathParameters() {
            return null;
        }

        @Override
        public Map<String, Object> getUserProperties() {
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public Set<Session> getOpenSessions() {
            return null;
        }
    };

    private static final Session SESSION2 = new Session() {
        @Override
        public WebSocketContainer getContainer() {
            return null;
        }

        @Override
        public void addMessageHandler(final MessageHandler messageHandler) throws IllegalStateException {

        }

        @Override
        public <T> void addMessageHandler(final Class<T> aClass, final MessageHandler.Whole<T> whole) {

        }

        @Override
        public <T> void addMessageHandler(final Class<T> aClass, final MessageHandler.Partial<T> partial) {

        }

        @Override
        public Set<MessageHandler> getMessageHandlers() {
            return null;
        }

        @Override
        public void removeMessageHandler(final MessageHandler messageHandler) {

        }

        @Override
        public String getProtocolVersion() {
            return null;
        }

        @Override
        public String getNegotiatedSubprotocol() {
            return null;
        }

        @Override
        public List<Extension> getNegotiatedExtensions() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public long getMaxIdleTimeout() {
            return 0;
        }

        @Override
        public void setMaxIdleTimeout(final long l) {

        }

        @Override
        public void setMaxBinaryMessageBufferSize(final int i) {

        }

        @Override
        public int getMaxBinaryMessageBufferSize() {
            return 0;
        }

        @Override
        public void setMaxTextMessageBufferSize(final int i) {

        }

        @Override
        public int getMaxTextMessageBufferSize() {
            return 0;
        }

        @Override
        public RemoteEndpoint.Async getAsyncRemote() {
            return null;
        }

        @Override
        public RemoteEndpoint.Basic getBasicRemote() {
            return null;
        }

        @Override
        public String getId() {
            return "2";
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void close(final CloseReason closeReason) throws IOException {

        }

        @Override
        public URI getRequestURI() {
            return null;
        }

        @Override
        public Map<String, List<String>> getRequestParameterMap() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public Map<String, String> getPathParameters() {
            return null;
        }

        @Override
        public Map<String, Object> getUserProperties() {
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public Set<Session> getOpenSessions() {
            return null;
        }
    };

    private static final ScreenUpdateEvent EVENT1 = new ScreenUpdateEvent(OpcodeEnum.CREATE, ScreenObjectTypeEnum.ZAAK, "1");

    private static final ScreenUpdateEvent EVENT1a = new ScreenUpdateEvent(OpcodeEnum.CREATE, ScreenObjectTypeEnum.ZAAK, "\"1\"");

    private static final ScreenUpdateEvent EVENT1b = new ScreenUpdateEvent(OpcodeEnum.CREATE, ScreenObjectTypeEnum.ZAAK, "\"\"1\"\"");

    private static final ScreenUpdateEvent EVENT2 = new ScreenUpdateEvent(OpcodeEnum.CREATE, ScreenObjectTypeEnum.ZAAK, "2");

    private static final ScreenUpdateEvent EVENT3 = new ScreenUpdateEvent(OpcodeEnum.CREATE, ScreenObjectTypeEnum.TAAK, "1");

    private static final ScreenUpdateEvent EVENT4 = new ScreenUpdateEvent(OpcodeEnum.UPDATE, ScreenObjectTypeEnum.ZAAK, "1");

    @Test
    public void testFixGelijk() {
        assertEquals(sessionRegistry.fix(EVENT1), sessionRegistry.fix(EVENT1));
        assertEquals(sessionRegistry.fix(EVENT1a), sessionRegistry.fix(EVENT1a));
        assertEquals(sessionRegistry.fix(EVENT1b), sessionRegistry.fix(EVENT1b));
        assertEquals(sessionRegistry.fix(EVENT1), sessionRegistry.fix(EVENT1a));
        assertEquals(sessionRegistry.fix(EVENT1), sessionRegistry.fix(EVENT1b));
        assertEquals(sessionRegistry.fix(EVENT1a), sessionRegistry.fix(EVENT1b));
        assertEquals(sessionRegistry.fix(EVENT2), sessionRegistry.fix(EVENT2));
        assertEquals(sessionRegistry.fix(EVENT3), sessionRegistry.fix(EVENT3));
        assertEquals(sessionRegistry.fix(EVENT4), sessionRegistry.fix(EVENT4));
    }

    @Test
    public void testFixNietGelijk() {
        assertNotEquals(sessionRegistry.fix(EVENT1), sessionRegistry.fix(EVENT2));
        assertNotEquals(sessionRegistry.fix(EVENT1), sessionRegistry.fix(EVENT3));
        assertNotEquals(sessionRegistry.fix(EVENT1), sessionRegistry.fix(EVENT4));
        assertNotEquals(sessionRegistry.fix(EVENT1a), sessionRegistry.fix(EVENT2));
        assertNotEquals(sessionRegistry.fix(EVENT1a), sessionRegistry.fix(EVENT3));
        assertNotEquals(sessionRegistry.fix(EVENT1a), sessionRegistry.fix(EVENT4));
        assertNotEquals(sessionRegistry.fix(EVENT1b), sessionRegistry.fix(EVENT2));
        assertNotEquals(sessionRegistry.fix(EVENT1b), sessionRegistry.fix(EVENT3));
        assertNotEquals(sessionRegistry.fix(EVENT1b), sessionRegistry.fix(EVENT4));
        assertNotEquals(sessionRegistry.fix(EVENT2), sessionRegistry.fix(EVENT3));
        assertNotEquals(sessionRegistry.fix(EVENT2), sessionRegistry.fix(EVENT4));
        assertNotEquals(sessionRegistry.fix(EVENT3), sessionRegistry.fix(EVENT4));
    }

    @Test
    public void testAdd() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);

        assertEquals(1, registry.getSessions(EVENT1).size());
        assertTrue(registry.getSessions(EVENT1).contains(SESSION1));
    }

    @Test
    public void testAddDubbel() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.add(EVENT1, SESSION1);

        assertEquals(1, registry.getSessions(EVENT1).size());
        assertTrue(registry.getSessions(EVENT1).contains(SESSION1));
    }

    @Test
    public void testAddFixed() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.add(EVENT1a, SESSION1);
        registry.add(EVENT1b, SESSION1);

        assertEquals(1, registry.getSessions(EVENT1).size());
        assertEquals(1, registry.getSessions(EVENT1a).size());
        assertEquals(1, registry.getSessions(EVENT1b).size());
        assertTrue(registry.getSessions(EVENT1).contains(SESSION1));
        assertTrue(registry.getSessions(EVENT1a).contains(SESSION1));
        assertTrue(registry.getSessions(EVENT1b).contains(SESSION1));
    }

    @Test
    public void testAddAndere() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.add(EVENT1, SESSION2);
        registry.add(EVENT2, SESSION1);
        registry.add(EVENT3, SESSION1);
        registry.add(EVENT4, SESSION1);

        assertEquals(2, registry.getSessions(EVENT1).size());
        assertEquals(1, registry.getSessions(EVENT2).size());
        assertEquals(1, registry.getSessions(EVENT3).size());
        assertEquals(1, registry.getSessions(EVENT4).size());
        assertTrue(registry.getSessions(EVENT1).contains(SESSION1));
        assertTrue(registry.getSessions(EVENT1).contains(SESSION2));
        assertTrue(registry.getSessions(EVENT2).contains(SESSION1));
        assertTrue(registry.getSessions(EVENT3).contains(SESSION1));
        assertTrue(registry.getSessions(EVENT4).contains(SESSION1));
    }

    @Test
    public void testRemove() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.remove(EVENT1, SESSION1);

        assertTrue(registry.getSessions(EVENT1).isEmpty());
    }

    @Test
    public void testRemoveFixed() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.remove(EVENT1a, SESSION1);

        registry.getSessions(EVENT1).forEach(session -> System.out.println(session.getId()));
        assertTrue(registry.getSessions(EVENT1).isEmpty());
        assertTrue(registry.getSessions(EVENT1a).isEmpty());
    }

    @Test
    public void testRemoveAndere() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.remove(EVENT1, SESSION2);
        registry.remove(EVENT2, SESSION1);
        registry.remove(EVENT3, SESSION1);
        registry.remove(EVENT4, SESSION1);

        assertTrue(registry.getSessions(EVENT2).isEmpty());
        assertTrue(registry.getSessions(EVENT3).isEmpty());
        assertTrue(registry.getSessions(EVENT4).isEmpty());
        assertEquals(1, registry.getSessions(EVENT1).size());
        assertTrue(registry.getSessions(EVENT1).contains(SESSION1));
    }

    @Test
    public void testRemoveAll() {
        final SessionRegistry registry = new SessionRegistry();
        registry.add(EVENT1, SESSION1);
        registry.add(EVENT1, SESSION2);
        registry.add(EVENT2, SESSION1);
        registry.add(EVENT3, SESSION1);
        registry.add(EVENT4, SESSION1);
        registry.removeAll(SESSION1);

        assertEquals(1, registry.getSessions(EVENT1).size());
        assertTrue(registry.getSessions(EVENT2).isEmpty());
        assertTrue(registry.getSessions(EVENT3).isEmpty());
        assertTrue(registry.getSessions(EVENT4).isEmpty());
        assertTrue(registry.getSessions(EVENT1).contains(SESSION2));
    }
}
