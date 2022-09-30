/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.configuratie;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;
import net.atos.zac.configuratie.model.Taal;

@ApplicationScoped
@Transactional
public class ConfiguratieService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    public List<Taal> listTalen() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Taal> query = builder.createQuery(Taal.class);
        final Root<Taal> root = query.from(Taal.class);
        query.orderBy(builder.asc(root.get("naam")));
        final TypedQuery<Taal> emQuery = entityManager.createQuery(query);
        return emQuery.getResultList();
    }

    public Taal readTaal(final long id) {
        return entityManager.find(Taal.class, id);
    }

    public Taal findDefaultTaal() {
        return findTaal(TAAL_NEDERLANDS);
    }

    public Taal findTaal(final String code) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Taal> query = builder.createQuery(Taal.class);
        final Root<Taal> root = query.from(Taal.class);
        query.where(builder.equal(root.get("code"), code));
        final TypedQuery<Taal> emQuery = entityManager.createQuery(query);
        final List<Taal> talen = emQuery.getResultList();
        return talen.isEmpty() ? null : talen.get(0);
    }

    //TODO zaakafhandelcomponent#1468 vervangen van onderstaande placeholders
    public static final String BRON_ORGANISATIE = "123443210";

    public static final String VERANTWOORDELIJKE_ORGANISATIE = "316245124";

    public static final String CATALOGUS_DOMEIN = "ALG";

    public static final String OMSCHRIJVING_TAAK_DOCUMENT = "taak-document";

    public static final String OMSCHRIJVING_VOORWAARDEN_GEBRUIKSRECHTEN = "geen";

    public static final String TAAL_NEDERLANDS = "dut"; // ISO 639-2/B

    public static final String STATUSTYPE_OMSCHRIJVING_HEROPEND = "Heropend";

    public static final String STATUSTYPE_OMSCHRIJVING_INTAKE = "Intake";

    public static final String STATUSTYPE_OMSCHRIJVING_IN_BEHANDELING = "In behandeling";

    public static final String STATUSTYPE_OMSCHRIJVING_AFGEROND = "Afgerond";

    public static final String TAAK_ELEMENT_ONDERTEKENEN = "ondertekenen";

    public static final String BIJLAGEN = "bijlagen";

    public static final String COMMUNICATIEKANAAL_EFORMULIER = "E-formulier";

    public static final String INFORMATIEOBJECTTYPE_OMSCHRIJVING_EMAIL = "e-mail";

    // Base URL of the zaakafhandelcomponent: protocol, host, port and context (no trailing slash)
    @Inject
    @ConfigProperty(name = "CONTEXT_URL")
    private String contextUrl;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    @ConfigProperty(name = "AUTH_RESOURCE")
    private String authResource;

    private URI catalogusURI;

    public URI readDefaultCatalogusURI() {
        if (catalogusURI == null) {
            final CatalogusListParameters catalogusListParameters = new CatalogusListParameters();
            catalogusListParameters.setDomein(CATALOGUS_DOMEIN);
            catalogusURI = ztcClientService.readCatalogus(catalogusListParameters).getUrl();
        }
        return catalogusURI;
    }

    public boolean isLocalDevelopment() {
        return authResource.contains("localhost");
    }

    public URI zaakTonenUrl(final String zaakIdentificatie) {
        return UriBuilder.fromUri(contextUrl).path("zaken/{zaakIdentificatie}").build(zaakIdentificatie);
    }

    public URI taakTonenUrl(final String taakId) {
        return UriBuilder.fromUri(contextUrl).path("taken/{taakId}").build(taakId);
    }

    public URI informatieobjectTonenUrl(final UUID enkelvoudigInformatieobjectUUID) {
        return UriBuilder.fromUri(contextUrl).path("informatie-objecten/{enkelvoudigInformatieobjectUUID}").build(enkelvoudigInformatieobjectUUID.toString());
    }
}
