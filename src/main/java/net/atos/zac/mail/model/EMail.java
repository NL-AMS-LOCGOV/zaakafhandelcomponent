/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.mail.model;

import javax.json.bind.annotation.JsonbProperty;

import java.util.List;

public class EMail {

    @JsonbProperty("HTMLPart")
    private String body;

    @JsonbProperty("From")
    private Verstuurder verstuurder;

    @JsonbProperty("To")
    private List<Ontvanger> ontvangers;

    @JsonbProperty("Subject")
    private String onderwerp;

    @JsonbProperty("Attachments")
    private List<Attachment> attachments;

    public EMail(final String body, final Verstuurder verstuurder, final List<Ontvanger> ontvangers,
            final String onderwerp, final List<Attachment> attachments) {
        this.body = "<pre>" + body + "</pre>";
        this.verstuurder = verstuurder;
        this.ontvangers = ontvangers;
        this.onderwerp = onderwerp;
        this.attachments = attachments;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public Verstuurder getVerstuurder() {
        return verstuurder;
    }

    public void setVerstuurder(final Verstuurder verstuurder) {
        this.verstuurder = verstuurder;
    }

    public List<Ontvanger> getOntvangers() {
        return ontvangers;
    }

    public void setOntvangers(final List<Ontvanger> ontvangers) {
        this.ontvangers = ontvangers;
    }

    public String getOnderwerp() {
        return onderwerp;
    }

    public void setOnderwerp(final String onderwerp) {
        this.onderwerp = onderwerp;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
