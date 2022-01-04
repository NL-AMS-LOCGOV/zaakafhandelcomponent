/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductAanvraag {

    /* ID van de submission in Open Forms (REQUIRED) */
    private String submissionId;

    /* Type productaanvraag (REQUIRED) */
    private String type;

    /* Object met de ingezonden formulierdata (REQUIRED) */
    private Map<String, Object> data;

    /* Burgerservicenummer */
    private String bsn;

    /* KVK-nummer van het bedrijf in het Handelsregister */
    private String kvk;

    /* URL van een document (in een Documenten API) dat de CSV met ingezonden formulierdata bevat */
    private URI csvUrl;

    /* URL van een document (in een Documenten API) dat de bevestigings PDF van Open Forms bevat */
    private URI pdfUrl;

    /* Lijst met URLs van de bijlagen van het ingezonden formulier in een Documenten API */
    private List<URI> attachments;

    public ProductAanvraag(final Map<String, Object> data) {
        submissionId = (String) data.get("submission_id");
        type = (String) data.get("type");
        this.data = (Map<String, Object>) data.get("data");
        bsn = (String) data.get("bsn");
        kvk = (String) data.get("kvk");
        if (data.containsKey("csv_url")) {
            csvUrl = URI.create((String) data.get("csv_url"));
        }
        if (data.containsKey("pdf_url")) {
            pdfUrl = URI.create((String) data.get("pdf_url"));
        }
        attachments = ((List<String>) data.get("attachments")).stream().map(URI::create).collect(Collectors.toList());
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(final String submissionId) {
        this.submissionId = submissionId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(final Map<String, Object> data) {
        this.data = data;
    }

    public String getBsn() {
        return bsn;
    }

    public void setBsn(final String bsn) {
        this.bsn = bsn;
    }

    public String getKvk() {
        return kvk;
    }

    public void setKvk(final String kvk) {
        this.kvk = kvk;
    }

    public URI getCsvUrl() {
        return csvUrl;
    }

    public void setCsvUrl(final URI csvUrl) {
        this.csvUrl = csvUrl;
    }

    public URI getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(final URI pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public List<URI> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<URI> attachments) {
        this.attachments = attachments;
    }
}
