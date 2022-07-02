/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.opa.OPAAdminClient;

public class PoliciesDeployer {

    private static final String POLICIES_FOLDER_NAME = "policies";

    private static final String POLICIES_FILE_NAME = "policies";

    private static final String POLICIES_FILE_EXTENSION = ".rego";

    private static final Logger LOG = Logger.getLogger(PoliciesDeployer.class.getName());

    @Inject
    @RestClient
    private OPAAdminClient opaAdminClient;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        try (final InputStream policiesInputStream = getClass().getClassLoader().getResourceAsStream(format("%s/%s", POLICIES_FOLDER_NAME, POLICIES_FILE_NAME));
             final BufferedReader policiesReader = new BufferedReader(new InputStreamReader(policiesInputStream, StandardCharsets.UTF_8))) {
            policiesReader.lines().filter(StringUtils::isNotBlank).forEach(this::deployPolicy);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deployPolicy(final String policyFileName) {
        try (final InputStream policyInputStream = getClass().getClassLoader().getResourceAsStream(format("%s/%s", POLICIES_FOLDER_NAME, policyFileName))) {
            final String policy = new String(policyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            final String moduleId = StringUtils.substringBefore(policyFileName, POLICIES_FILE_EXTENSION);
            LOG.info(String.format("Deploying policy module: %s", moduleId));
            opaAdminClient.policyUpdate(moduleId, policy);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
