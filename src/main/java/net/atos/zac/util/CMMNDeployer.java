/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CMMNDeployer {

    private static final String CMMN_MODELS_FOLDER_NAME = "cmmn";

    private static final String CASE_ID_XPATH_EXPRESSION = "/definitions/case/@id";

    private static final String CASE_NAME_XPATH_EXPRESSION = "/definitions/case/@name";

    private static final Logger LOG = Logger.getLogger(CMMNDeployer.class.getName());

    @Inject
    private CmmnRepositoryService cmmnRepositoryService;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        final Path cmmnModelsFolder = Path.of(getClass().getClassLoader().getResource(CMMN_MODELS_FOLDER_NAME).getPath());
        try (final Stream<Path> modelFiles = Files.list(cmmnModelsFolder)) {
            modelFiles.forEach(this::checkModel);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkModel(final Path modelFile) {
        try {
            final byte[] modelContent = Files.readAllBytes(modelFile);
            final Document modelXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(modelContent));
            final XPath xPath = XPathFactory.newInstance().newXPath();
            final String key = (String) xPath.evaluate(CASE_ID_XPATH_EXPRESSION, modelXml, XPathConstants.STRING);
            final CmmnDeployment cmmnDeployment = cmmnRepositoryService.createDeploymentQuery().deploymentKey(key).latest().singleResult();
            final String resourceName = modelFile.getFileName().toString();
            final InputStream deployedModelContent = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), resourceName);

            if (!IOUtils.contentEquals(new ByteArrayInputStream(modelContent), deployedModelContent)) {
                final String name = (String) xPath.evaluate(CASE_NAME_XPATH_EXPRESSION, modelXml, XPathConstants.STRING);
                cmmnRepositoryService.createDeployment().key(key).name(name).addBytes(resourceName, modelContent).deploy();
                LOG.info(String.format("Successfully deployed CMMN model with key '%s' and name '%s' from file '%s'", key, name, resourceName));
            }
        } catch (final IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
