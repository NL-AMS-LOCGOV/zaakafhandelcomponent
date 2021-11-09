/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.notificaties;

import static net.atos.client.zgw.shared.util.DateTimeUtil.DATE_TIME_FORMAT_WITH_MILLISECONDS;
import static net.atos.zac.notificaties.ActionEnum.UPDATE;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;

public class Notificatie {

    private final ChannelEnum channel;

    private final URI mainResourceUrl;

    private final ResourceEnum resourceType;

    private final URI resourceUrl;

    private final ActionEnum action;

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
        this.channel = ChannelEnum.value(channel);
        this.mainResourceUrl = mainResourceUrl;
        this.resourceType = ResourceEnum.value(resourceType);
        this.resourceUrl = resourceUrl;
        this.action = ActionEnum.value(action);
        this.creationDateTime = creationDateTime;
    }

    public ChannelEnum getChannel() {
        return channel;
    }

    public Resource getMainResource() {
        return new Resource(
                getMainResourceType(),
                getMainResourceUrl(),
                getMainResourceType() == getResourceType() ? getAction() : UPDATE);
    }

    public ResourceEnum getMainResourceType() {
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

    public ResourceEnum getResourceType() {
        return resourceType;
    }

    public URI getResourceUrl() {
        return resourceUrl;
    }

    public ActionEnum getAction() {
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
        private final ResourceEnum type;

        private final URI url;

        private final ActionEnum action;

        /**
         * Use this for the actually modified resource
         *
         * @param type   the type of resource
         * @param url    the identification of the resource
         * @param action the type of modification
         */
        private Resource(final ResourceEnum type, final URI url, final ActionEnum action) {
            this.action = action;
            this.type = type;
            this.url = url;
        }

        public ResourceEnum getType() {
            return type;
        }

        public URI getUrl() {
            return url;
        }

        public ActionEnum getAction() {
            return action;
        }
    }
}
