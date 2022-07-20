/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class BAGClientService {

    private static final Logger LOG = Logger.getLogger(BAGClientService.class.getName());

    @Inject
    @RestClient
    private AdresApiClient adresApiClient;

}
