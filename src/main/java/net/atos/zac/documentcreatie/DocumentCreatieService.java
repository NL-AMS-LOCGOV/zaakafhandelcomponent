/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.sd.SmartDocumentsClient;

@ApplicationScoped
public class DocumentCreatieService {

    @Inject
    @RestClient
    private SmartDocumentsClient smartDocumentsClient;


}
