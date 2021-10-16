/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.shared.InvocationBuilderFactory;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieObject;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.zrc.model.ZaakinformatieobjectListParameters;

/**
 *
 */
@ApplicationScoped
public class ZRCClientService {

    @Inject
    @RestClient
    private ZRCClient zrcClient;

    public Zaak getZaakWithKenmerk(final String bron, final String kenmerk) {
        // See ESUITEDEV-22944
        Results<Zaak> results = zrcClient.zaakList(new ZaakListParameters());
        while (results != null) {
            final Optional<Zaak> zaak = results.getResults().stream()
                    .filter(z -> isNotEmpty(z.getKenmerken()) && z.getKenmerken().stream()
                            .anyMatch(zaakKenmerk -> StringUtils
                                    .equals(zaakKenmerk.getBron(), bron) && StringUtils
                                    .equals(zaakKenmerk.getKenmerk(), kenmerk)))
                    .findAny();

            if (zaak.isPresent()) {
                return zaak.get();
            } else {
                results = results.getNext();
            }
        }
        throw new IllegalStateException(String.format("Zaak from bron '%s' with kenmerk '%s' not found.", bron, kenmerk));
    }

    public List<ZaakInformatieObject> getZaakInformatieObjectenForZaak(final URI zaakURI) {
        final ZaakinformatieobjectListParameters zaakinformatieobjectListParameters = new ZaakinformatieobjectListParameters();
        zaakinformatieobjectListParameters.setZaak(zaakURI);
        return zrcClient.zaakinformatieobjectList(zaakinformatieobjectListParameters);
    }

    public void setZaakStatus(final URI zaakURI, final URI statustypeURI, final String toelichting, final ZonedDateTime datumGezet) {
        final Status status = new Status(zaakURI, statustypeURI, datumGezet);
        status.setStatustoelichting(toelichting);
        zrcClient.statusCreate(status);
    }

    public void setZaakStatus(final URI zaakURI, final URI statustypeURI, final String toelichting) {
        // Wanneer de huidige datum en tijd gebruikt wordt geeft open zaak een validatie fout dat de datum in de toekomst ligt.
        setZaakStatus(zaakURI, statustypeURI, toelichting, ZonedDateTime.now().minusDays(1));
    }

    public void setZaakResultaat(final URI zaakURI, final URI resultaattypeURI, final String toelichting) {
        final Resultaat resultaat = new Resultaat(zaakURI, resultaattypeURI);
        resultaat.setToelichting(toelichting);
        zrcClient.resultaatCreate(resultaat);
    }

    public List<Rol<?>> getRollenForZaak(final URI zaakURI) {
        final RolListParameters rolListParameters = new RolListParameters();
        rolListParameters.setZaak(zaakURI);
        return zrcClient.rolList(rolListParameters).getResults();
    }

    public void addRollenToZaak(final Collection<Rol<?>> rollen) {
        rollen.forEach(rol -> zrcClient.rolCreate(rol));
    }

    public void updateRollenForZaak(final URI zaakURI, final Collection<Rol<?>> rollen) {
        final Collection<Rol<?>> current = getRollenForZaak(zaakURI);
        deleteDeletedRollen(current, rollen);
        deleteUpdatedRollen(current, rollen);
        createUpdatedRollen(current, rollen);
        createCreatedRollen(current, rollen);
    }

    private void deleteDeletedRollen(final Collection<Rol<?>> currentRollen, final Collection<Rol<?>> rollen) {
        currentRollen.stream()
                .filter(oud -> rollen.stream()
                        .noneMatch(oud::equalBetrokkeneRol))
                .forEach(oud -> zrcClient.rolDelete(oud.getUuid()));
    }

    private void deleteUpdatedRollen(final Collection<Rol<?>> currentRollen, final Collection<Rol<?>> rollen) {
        currentRollen.stream()
                .filter(oud -> rollen.stream()
                        .filter(oud::equalBetrokkeneRol)
                        .anyMatch(nieuw -> !nieuw.equals(oud)))
                .forEach(oud -> zrcClient.rolDelete(oud.getUuid()));
    }

    private void createUpdatedRollen(final Collection<Rol<?>> currentRollen, final Collection<Rol<?>> rollen) {
        rollen.stream()
                .filter(nieuw -> currentRollen.stream()
                        .filter(nieuw::equalBetrokkeneRol)
                        .anyMatch(oud -> !oud.equals(nieuw)))
                .forEach(nieuw -> zrcClient.rolCreate(nieuw));
    }

    private void createCreatedRollen(final Collection<Rol<?>> currentRollen, final Collection<Rol<?>> rollen) {
        rollen.stream()
                .filter(nieuw -> currentRollen.stream()
                        .noneMatch(nieuw::equalBetrokkeneRol))
                .forEach(nieuw -> zrcClient.rolCreate(nieuw));
    }

    public Status getStatus(final URI statusURI) {
        return InvocationBuilderFactory.create(statusURI).get(Status.class);
    }
}
