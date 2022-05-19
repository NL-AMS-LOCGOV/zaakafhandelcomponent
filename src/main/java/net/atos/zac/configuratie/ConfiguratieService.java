/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.configuratie;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import org.eclipse.microprofile.config.ConfigProvider;
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

    //TODO ESUITEDEV-25102 vervangen van onderstaande placeholders
    public static final String BRON_ORGANISATIE = "123443210";

    public static final String VERANTWOORDELIJKE_ORGANISATIE = "316245124";

    public static final String CATALOGUS_DOMEIN = "ALG";

    public static final String MELDING_KLEIN_EVENEMENT_ZAAKTYPE_IDENTIFICATIE = "melding-klein-evenement";

    public static final String OMSCHRIJVING_TAAK_DOCUMENT = "taak-document";

    public static final String TAAL_NEDERLANDS = "dut"; // ISO 639-2/B

    // Base URL of the zaakafhandelcomponent: protocol, host, port and context (no trailing slash)
    public static final String CONTEXT_URL = ConfigProvider.getConfig().getValue("context.url", String.class);

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

    private URI absoluteUrl(final String path, final String key) {
        try {
            return new URI(String.format("%s/%s/%s", CONTEXT_URL, path, URLEncoder.encode(key, StandardCharsets.UTF_8)));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public URI zaakTonenUrl(final String zaakIdentificatie) {
        return absoluteUrl("zaken", zaakIdentificatie);
    }

    public URI taakTonenUrl(final String taakId) {
        return absoluteUrl("taken", taakId);
    }

    public URI informatieobjectTonenUrl(final UUID enkelvoudigInformatieobjectUUID) {
        return absoluteUrl("informatie-objecten", enkelvoudigInformatieobjectUUID.toString());
    }
}
