/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.input;

import javax.json.bind.annotation.JsonbProperty;

import net.atos.zac.authentication.LoggedInUser;

public class DocumentInput extends UserInput {

    @JsonbProperty("document")
    private final DocumentData documentData;

    public DocumentInput(final LoggedInUser loggedInUser, final DocumentData documentData) {
        super(loggedInUser);
        this.documentData = documentData;
    }

    public DocumentData getDocumentData() {
        return documentData;
    }
}
