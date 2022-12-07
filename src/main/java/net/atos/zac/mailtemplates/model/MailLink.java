package net.atos.zac.mailtemplates.model;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public final class MailLink {
    public final String identificatie;

    public final String url;

    public final String prefix;

    public final String suffix;

    public MailLink(final String identificatie, final URI url, final String prefix, final String suffix) {
        this.identificatie = StringEscapeUtils.escapeHtml4(identificatie);
        this.url = StringEscapeUtils.escapeHtml4(url.toString());
        this.prefix = prefix != null ? StringEscapeUtils.escapeHtml4(prefix) + " " : StringUtils.EMPTY;
        this.suffix = suffix != null ? " " + StringEscapeUtils.escapeHtml4(suffix) : StringUtils.EMPTY;
    }

    // Make sure that what is returned is FULLY encoded HTML (no injection vulnerabilities please!)
    public String toHtml() {
        return "Klik om naar %s<a href=\"%s\" title=\"de zaakafhandelcomponent...\">%s</a>%s te gaan."
                .formatted(prefix, url, identificatie, suffix);
    }
}
