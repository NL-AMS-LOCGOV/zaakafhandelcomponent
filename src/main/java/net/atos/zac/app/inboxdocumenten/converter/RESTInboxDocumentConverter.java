/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.inboxdocumenten.converter;

import java.util.List;
import java.util.stream.Collectors;

import net.atos.zac.app.inboxdocumenten.model.RESTInboxDocument;
import net.atos.zac.documenten.model.InboxDocument;

public class RESTInboxDocumentConverter {

    public RESTInboxDocument convert(final InboxDocument document) {
        final RESTInboxDocument restDocument = new RESTInboxDocument();
        restDocument.id = document.getId();
        restDocument.enkelvoudiginformatieobjectUUID = document.getEnkelvoudiginformatieobjectUUID();
        restDocument.enkelvoudiginformatieobjectID = document.getEnkelvoudiginformatieobjectID();
        restDocument.titel = document.getTitel();
        restDocument.creatiedatum = document.getCreatiedatum().toLocalDate();
        restDocument.bestandsnaam = document.getBestandsnaam();
        return restDocument;
    }

    public List<RESTInboxDocument> convert(final List<InboxDocument> documenten) {
        return documenten.stream().map(this::convert).collect(Collectors.toList());
    }
}
