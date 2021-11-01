/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import net.atos.client.zgw.shared.util.ZGWApisInvocationBuilderFactory;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakeigenschap;

/**
 *
 */
@ApplicationScoped
public class ZRCClientService {

    @Inject
    @RestClient
    private ZRCClient zrcClient;

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

    public void updateRollenForZaak(final URI zaakURI, final Collection<Rol<?>> rollen) {
        final Collection<Rol<?>> current = getRollenForZaak(zaakURI);
        deleteDeletedRollen(current, rollen);
        deleteUpdatedRollen(current, rollen);
        createUpdatedRollen(current, rollen);
        createCreatedRollen(current, rollen);
    }

    public Zaak getZaak(final URI zaakURI) {
        return ZGWApisInvocationBuilderFactory.create(zaakURI).get(Zaak.class);
    }

    public Resultaat getResultaat(final URI resultaatURI) {
        return ZGWApisInvocationBuilderFactory.create(resultaatURI).get(Resultaat.class);
    }

    public Zaakeigenschap getZaakeigenschap(final URI zaakeigenschapURI) {
        return ZGWApisInvocationBuilderFactory.create(zaakeigenschapURI).get(Zaakeigenschap.class);
    }

    public Status getStatus(final URI statusURI) {
        return ZGWApisInvocationBuilderFactory.create(statusURI).get(Status.class);
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
}
