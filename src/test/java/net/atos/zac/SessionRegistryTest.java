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

import net.atos.zac.event.Opcode;
import net.atos.zac.websocket.SessionRegistry;
import net.atos.zac.websocket.event.ScreenEventType;
import net.atos.zac.websocket.event.ScreenEvent;

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

    private static final ScreenEvent CREATED_ZAAK1 = new ScreenEvent(Opcode.CREATED, ScreenEventType.ZAAK, "1");

    private static final ScreenEvent CREATED_TAAK1 = new ScreenEvent(Opcode.CREATED, ScreenEventType.TAAK, "1");

    private static final ScreenEvent CREATED_DOCUMENT1 = new ScreenEvent(Opcode.CREATED, ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT, "1");

    private static final ScreenEvent UPDATED_ZAAK1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK, "1");

    private static final ScreenEvent UPDATED_ZAAK1a = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK, "\"1\"");

    private static final ScreenEvent UPDATED_ZAAK1b = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK, "\"\"1\"\"");

    private static final ScreenEvent UPDATED_ZAAK2 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK, "2");

    private static final ScreenEvent DELETED_ZAAK2 = new ScreenEvent(Opcode.DELETED, ScreenEventType.ZAAK, "2");

    private static final ScreenEvent UPDATED_TAAK1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.TAAK, "1");

    private static final ScreenEvent UPDATED_DOCUMENT1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT, "1");

    private static final ScreenEvent UPDATED_ZAAKROLLEN1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK_ROLLEN, "1");

    private static final ScreenEvent UPDATED_ZAAKTAKEN1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK_TAKEN, "1");

    private static final ScreenEvent UPDATED_ZAAKDOCUMENTEN1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ZAAK_INFORMATIEOBJECTEN, "1");

    private static final ScreenEvent DELETED_ZAAK1 = new ScreenEvent(Opcode.DELETED, ScreenEventType.ZAAK, "1");

    private static final ScreenEvent DELETED_TAAK1 = new ScreenEvent(Opcode.DELETED, ScreenEventType.TAAK, "1");

    private static final ScreenEvent DELETED_DOCUMENT1 = new ScreenEvent(Opcode.DELETED, ScreenEventType.ENKELVOUDIG_INFORMATIEOBJECT, "1");

    private static final ScreenEvent ANY_ZAAK1 = new ScreenEvent(Opcode.ANY, ScreenEventType.ZAAK, "1");

    private static final ScreenEvent UPDATED_ANY1 = new ScreenEvent(Opcode.UPDATED, ScreenEventType.ANY, "1");

    private static final ScreenEvent ANY_ANY1 = new ScreenEvent(Opcode.ANY, ScreenEventType.ANY, "1");

    @Test
    public void testFixGelijk() {
        assertEquals(sessionRegistry.fix(UPDATED_ZAAK1), sessionRegistry.fix(UPDATED_ZAAK1));
        assertEquals(sessionRegistry.fix(UPDATED_ZAAK1), sessionRegistry.fix(UPDATED_ZAAK1a));
        assertEquals(sessionRegistry.fix(UPDATED_ZAAK1), sessionRegistry.fix(UPDATED_ZAAK1b));
        assertEquals(sessionRegistry.fix(UPDATED_ZAAK1a), sessionRegistry.fix(UPDATED_ZAAK1b));
        assertEquals(sessionRegistry.fix(UPDATED_TAAK1), sessionRegistry.fix(UPDATED_TAAK1));
        assertEquals(sessionRegistry.fix(DELETED_ZAAK1), sessionRegistry.fix(DELETED_ZAAK1));
    }

    @Test
    public void testFixNietGelijk() {
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1), sessionRegistry.fix(UPDATED_ZAAK2));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1), sessionRegistry.fix(UPDATED_TAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1), sessionRegistry.fix(DELETED_ZAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1a), sessionRegistry.fix(UPDATED_ZAAK2));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1a), sessionRegistry.fix(UPDATED_TAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1a), sessionRegistry.fix(DELETED_ZAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1b), sessionRegistry.fix(UPDATED_ZAAK2));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1b), sessionRegistry.fix(UPDATED_TAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK1b), sessionRegistry.fix(DELETED_ZAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK2), sessionRegistry.fix(UPDATED_TAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_ZAAK2), sessionRegistry.fix(DELETED_ZAAK1));
        assertNotEquals(sessionRegistry.fix(UPDATED_TAAK1), sessionRegistry.fix(DELETED_ZAAK1));
    }

    @Test
    public void testAdd() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION1));
    }

    @Test
    public void testAddDubbel() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.create(UPDATED_ZAAK1, SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION1));
    }

    @Test
    public void testAddFixed() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.create(UPDATED_ZAAK1a, SESSION1);
        registry.create(UPDATED_ZAAK1b, SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAK1a).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAK1b).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION1));
        assertTrue(registry.listSessions(UPDATED_ZAAK1a).contains(SESSION1));
        assertTrue(registry.listSessions(UPDATED_ZAAK1b).contains(SESSION1));
    }

    @Test
    public void testAddAndere() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.create(UPDATED_ZAAK1, SESSION2);
        registry.create(UPDATED_ZAAK2, SESSION1);
        registry.create(UPDATED_TAAK1, SESSION1);
        registry.create(DELETED_ZAAK1, SESSION1);

        assertEquals(2, registry.listSessions(UPDATED_ZAAK1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAK2).size());
        assertEquals(1, registry.listSessions(UPDATED_TAAK1).size());
        assertEquals(1, registry.listSessions(DELETED_ZAAK1).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION1));
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION2));
        assertTrue(registry.listSessions(UPDATED_ZAAK2).contains(SESSION1));
        assertTrue(registry.listSessions(UPDATED_TAAK1).contains(SESSION1));
        assertTrue(registry.listSessions(DELETED_ZAAK1).contains(SESSION1));
    }

    @Test
    public void testRemove() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.delete(UPDATED_ZAAK1, SESSION1);

        assertTrue(registry.listSessions(UPDATED_ZAAK1).isEmpty());
    }

    @Test
    public void testRemoveFixed() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.delete(UPDATED_ZAAK1a, SESSION1);

        registry.listSessions(UPDATED_ZAAK1).forEach(session -> System.out.println(session.getId()));
        assertTrue(registry.listSessions(UPDATED_ZAAK1).isEmpty());
        assertTrue(registry.listSessions(UPDATED_ZAAK1a).isEmpty());
    }

    @Test
    public void testRemoveAndere() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.delete(UPDATED_ZAAK1, SESSION2);
        registry.delete(UPDATED_ZAAK2, SESSION1);
        registry.delete(UPDATED_TAAK1, SESSION1);
        registry.delete(DELETED_ZAAK1, SESSION1);

        assertTrue(registry.listSessions(UPDATED_ZAAK2).isEmpty());
        assertTrue(registry.listSessions(UPDATED_TAAK1).isEmpty());
        assertTrue(registry.listSessions(DELETED_ZAAK1).isEmpty());
        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION1));
    }

    @Test
    public void testRemoveAll() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ZAAK1, SESSION1);
        registry.create(UPDATED_ZAAK1, SESSION2);
        registry.create(UPDATED_ZAAK2, SESSION1);
        registry.create(UPDATED_TAAK1, SESSION1);
        registry.create(DELETED_ZAAK1, SESSION1);
        registry.deleteAll(SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK2).isEmpty());
        assertTrue(registry.listSessions(UPDATED_TAAK1).isEmpty());
        assertTrue(registry.listSessions(DELETED_ZAAK1).isEmpty());
        assertTrue(registry.listSessions(UPDATED_ZAAK1).contains(SESSION2));
    }

    @Test
    public void testAddAnyOpcode() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(ANY_ZAAK1, SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertEquals(1, registry.listSessions(DELETED_ZAAK1).size());
        assertTrue(registry.listSessions(CREATED_ZAAK1).isEmpty());
        assertTrue(registry.listSessions(UPDATED_ZAAK2).isEmpty());
        assertTrue(registry.listSessions(UPDATED_TAAK1).isEmpty());
    }

    @Test
    public void testAddAnyObjectType() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(UPDATED_ANY1, SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertEquals(1, registry.listSessions(UPDATED_TAAK1).size());
        assertEquals(1, registry.listSessions(UPDATED_DOCUMENT1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAKROLLEN1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAKTAKEN1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAKDOCUMENTEN1).size());
        assertTrue(registry.listSessions(UPDATED_ZAAK2).isEmpty());
        assertTrue(registry.listSessions(DELETED_ZAAK1).isEmpty());
        assertTrue(registry.listSessions(CREATED_ZAAK1).isEmpty());
    }

    @Test
    public void testAddAnyOpcodeAnyObjectType() {
        final SessionRegistry registry = new SessionRegistry();
        registry.create(ANY_ANY1, SESSION1);

        assertEquals(1, registry.listSessions(UPDATED_ZAAK1).size());
        assertEquals(1, registry.listSessions(UPDATED_TAAK1).size());
        assertEquals(1, registry.listSessions(UPDATED_DOCUMENT1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAKROLLEN1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAKTAKEN1).size());
        assertEquals(1, registry.listSessions(UPDATED_ZAAKDOCUMENTEN1).size());
        assertEquals(1, registry.listSessions(DELETED_ZAAK1).size());
        assertEquals(1, registry.listSessions(DELETED_TAAK1).size());
        assertEquals(1, registry.listSessions(DELETED_DOCUMENT1).size());
        assertTrue(registry.listSessions(CREATED_ZAAK1).isEmpty());
        assertTrue(registry.listSessions(CREATED_TAAK1).isEmpty());
        assertTrue(registry.listSessions(CREATED_DOCUMENT1).isEmpty());
        assertTrue(registry.listSessions(UPDATED_ZAAK2).isEmpty());
        assertTrue(registry.listSessions(DELETED_ZAAK2).isEmpty());
    }
}
