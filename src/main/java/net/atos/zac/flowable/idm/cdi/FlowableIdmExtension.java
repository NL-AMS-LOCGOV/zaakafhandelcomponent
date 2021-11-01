/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.flowable.idm.cdi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.idm.engine.IdmEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * starts / stops the {@link org.flowable.idm.engine.IdmEngine}.
 */
public class FlowableIdmExtension implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableIdmExtension.class);

    private IdmEngineLookup idmEngineLookup;

    public void afterDeploymentValidation(@Observes final AfterDeploymentValidation event, final BeanManager beanManager) {
        try {
            LOGGER.info("Initializing flowable-idm-cdi.");
            // initialize the idm engine
            lookupIdmEngine(beanManager);
        } catch (final Exception e) {
            // interpret idm engine initialization problems as definition errors
            event.addDeploymentProblem(e);
        }
    }

    protected IdmEngine lookupIdmEngine(final BeanManager beanManager) {
        final ServiceLoader<IdmEngineLookup> idmEngineServiceLoader = ServiceLoader.load(IdmEngineLookup.class);
        final Iterator<IdmEngineLookup> serviceIterator = idmEngineServiceLoader.iterator();
        final List<IdmEngineLookup> discoveredLookups = new ArrayList<>();
        while (serviceIterator.hasNext()) {
            final IdmEngineLookup serviceInstance = serviceIterator.next();
            discoveredLookups.add(serviceInstance);
        }

        Collections.sort(discoveredLookups, new Comparator<IdmEngineLookup>() {
            @Override
            public int compare(final IdmEngineLookup o1, final IdmEngineLookup o2) {
                return (-1) * ((Integer) o1.getPrecedence()).compareTo(o2.getPrecedence());
            }
        });

        IdmEngine idmEngine = null;

        for (final IdmEngineLookup idmEngineLookup : discoveredLookups) {
            idmEngine = idmEngineLookup.getIdmEngine();
            if (idmEngine != null) {
                this.idmEngineLookup = idmEngineLookup;
                LOGGER.debug("IdmEngineLookup service {} returned idm engine.", idmEngineLookup.getClass());
                break;
            } else {
                LOGGER.debug("IdmEngineLookup service {} returned 'null' value.", idmEngineLookup.getClass());
            }
        }

        if (idmEngineLookup == null) {
            throw new FlowableException(
                    "Could not find an implementation of the " + IdmEngineLookup.class.getName() + " service returning a non-null idmEngine. Giving up.");
        }

        final Bean<FlowableIdmServices> flowableIdmServicesBean = (Bean<FlowableIdmServices>) beanManager.getBeans(FlowableIdmServices.class).stream()
                .findAny()
                .orElseThrow(
                        () -> new IllegalStateException(
                                "CDI BeanManager cannot find an instance of requested type " + FlowableIdmServices.class
                                        .getName()));
        final FlowableIdmServices services = (FlowableIdmServices) beanManager
                .getReference(flowableIdmServicesBean, FlowableIdmServices.class, beanManager.createCreationalContext(flowableIdmServicesBean));
        services.setIdmEngine(idmEngine);

        return idmEngine;
    }

    public void beforeShutdown(@Observes final BeforeShutdown event) {
        if (idmEngineLookup != null) {
            idmEngineLookup.ungetIdmEngine();
            idmEngineLookup = null;
        }
        LOGGER.info("Shutting down flowable-idm-cdi");
    }
}
