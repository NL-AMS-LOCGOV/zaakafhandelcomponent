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
import java.util.Map;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

public class Notificatie {

    private Channel channel;

    private URI mainResourceUrl;

    private Resource resource;

    private URI resourceUrl;

    private Action action;

    @JsonbDateFormat(DATE_TIME_FORMAT_WITH_MILLISECONDS)
    private ZonedDateTime creationDateTime;

    private Map<String, String> properties;

    public Channel getChannel() {
        return channel;
    }

    @JsonbProperty("kanaal")
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }

    public URI getMainResourceUrl() {
        return mainResourceUrl;
    }

    @JsonbProperty("hoofdObject")
    public void setMainResourceUrl(final URI mainResourceUrl) {
        this.mainResourceUrl = mainResourceUrl;
    }

    public Resource getResource() {
        return resource;
    }

    @JsonbProperty("resource")
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    public URI getResourceUrl() {
        return resourceUrl;
    }

    @JsonbProperty("resourceUrl")
    public void setResourceUrl(final URI resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public Action getAction() {
        return action;
    }

    @JsonbProperty("actie")
    public void setAction(final Action action) {
        this.action = action;
    }

    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    @JsonbProperty("aanmaakdatum")
    public void setCreationDateTime(final ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Map<String, String> getProperties() {
        return properties != null ? properties : Collections.emptyMap();
    }

    @JsonbProperty("kenmerken")
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonbTransient
    public ResourceInfo getResourceInfo() {
        return new ResourceInfo(getResource(), getResourceUrl(), getAction());
    }

    @JsonbTransient
    public Resource getMainResourceType() {
        return channel.getResourceType();
    }

    @JsonbTransient
    public ResourceInfo getMainResourceInfo() {
        return new ResourceInfo(
                getMainResourceType(),
                getMainResourceUrl(),
                getMainResourceType() == getResource() && getMainResourceUrl().equals(getResourceUrl()) ? getAction() : UPDATE);
    }

    public static class ResourceInfo {

        private final Resource type;

        private final URI url;

        private final Action action;

        /**
         * Use this for the actually modified resource
         *
         * @param type   the type of resource
         * @param url    the identification of the resource
         * @param action the type of modification
         */
        private ResourceInfo(final Resource type, final URI url, final Action action) {
            this.action = action;
            this.type = type;
            this.url = url;
        }

        public Resource getType() {
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
