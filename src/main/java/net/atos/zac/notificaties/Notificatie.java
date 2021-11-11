/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;
import static net.atos.zac.notificaties.Action.UPDATE;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

public class Notificatie {

    private final Channel channel;

    private final URI mainResourceUrl;

    private final net.atos.zac.notificaties.Resource resourceType;

    private final URI resourceUrl;

    private final Action action;

    @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS)
    private final ZonedDateTime creationDateTime;

    private final Map<String, String> properties = new HashMap<>();

    @JsonbCreator
    public Notificatie(
            @JsonbProperty("kanaal") final String channel,
            @JsonbProperty("hoofdObject") final URI mainResourceUrl,
            @JsonbProperty("resource") final String resourceType,
            @JsonbProperty("resourceUrl") final URI resourceUrl,
            @JsonbProperty("actie") final String action,
            @JsonbProperty("aanmaakdatum") final ZonedDateTime creationDateTime) {
        this.channel = Channel.value(channel);
        this.mainResourceUrl = mainResourceUrl;
        this.resourceType = net.atos.zac.notificaties.Resource.value(resourceType);
        this.resourceUrl = resourceUrl;
        this.action = Action.value(action);
        this.creationDateTime = creationDateTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public Resource getMainResource() {
        return new Resource(
                getMainResourceType(),
                getMainResourceUrl(),
                getMainResourceType() == getResourceType() && getMainResourceUrl().equals(getResourceUrl())
                        ? getAction()
                        : UPDATE);
    }

    public net.atos.zac.notificaties.Resource getMainResourceType() {
        return channel.getResourceType();
    }

    public URI getMainResourceUrl() {
        return mainResourceUrl;
    }

    public Resource getResource() {
        return new Resource(
                getResourceType(),
                getResourceUrl(),
                getAction());
    }

    public net.atos.zac.notificaties.Resource getResourceType() {
        return resourceType;
    }

    public URI getResourceUrl() {
        return resourceUrl;
    }

    public Action getAction() {
        return action;
    }

    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void setProperty(final String key, final String value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
        }
    }

    public static class Resource {
        private final net.atos.zac.notificaties.Resource type;

        private final URI url;

        private final Action action;

        /**
         * Use this for the actually modified resource
         *
         * @param type   the type of resource
         * @param url    the identification of the resource
         * @param action the type of modification
         */
        private Resource(final net.atos.zac.notificaties.Resource type, final URI url, final Action action) {
            this.action = action;
            this.type = type;
            this.url = url;
        }

        public net.atos.zac.notificaties.Resource getType() {
            return type;
        }

        public URI getUrl() {
            return url;
        }

        public Action getAction() {
            return action;
        }
    }
}
