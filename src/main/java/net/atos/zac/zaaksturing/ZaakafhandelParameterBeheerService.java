/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaaksturing;

import static net.atos.client.zgw.shared.cache.Caching.ZAC_ZAAKAFHANDELPARAMETERS;
import static net.atos.zac.util.ValidationUtil.valideerObject;
import static net.atos.zac.zaaksturing.model.ZaakafhandelParameters.CREATIEDATUM;
import static net.atos.zac.zaaksturing.model.ZaakafhandelParameters.PRODUCTAANVRAAGTYPE;
import static net.atos.zac.zaaksturing.model.ZaakafhandelParameters.ZAAKTYPE_OMSCHRIJVING;
import static net.atos.zac.zaaksturing.model.ZaakafhandelParameters.ZAAKTYPE_UUID;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import javax.cache.annotation.CacheRemoveAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.util.UriUtil;
import net.atos.zac.util.ValidationUtil;
import net.atos.zac.zaaksturing.model.HumanTaskParameters;
import net.atos.zac.zaaksturing.model.MailtemplateKoppeling;
import net.atos.zac.zaaksturing.model.UserEventListenerParameters;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;
import net.atos.zac.zaaksturing.model.ZaakbeeindigParameter;
import net.atos.zac.zaaksturing.model.ZaakbeeindigReden;

@ApplicationScoped
@Transactional
public class ZaakafhandelParameterBeheerService {

    private static final Logger LOG = Logger.getLogger(ZaakafhandelParameterBeheerService.class.getName());

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private ZTCClientService ztcClientService;

    ZaakafhandelParameters readZaakafhandelParameters(final UUID zaaktypeUUID) {
        ztcClientService.readCacheTime();
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakafhandelParameters> query = builder.createQuery(ZaakafhandelParameters.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.select(root).where(builder.equal(root.get(ZAAKTYPE_UUID), zaaktypeUUID));
        final List<ZaakafhandelParameters> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            final ZaakafhandelParameters zaakafhandelParameters = new ZaakafhandelParameters();
            zaakafhandelParameters.setZaakTypeUUID(zaaktypeUUID);
            return zaakafhandelParameters;
        }
    }

    List<ZaakafhandelParameters> listZaakafhandelParameters() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakafhandelParameters> query = builder.createQuery(ZaakafhandelParameters.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.orderBy(builder.desc(root.get("id")));
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

    @CacheRemoveAll(cacheName = ZAC_ZAAKAFHANDELPARAMETERS)
    public ZaakafhandelParameters createZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        valideerObject(zaakafhandelParameters);
        zaakafhandelParameters.getHumanTaskParametersCollection().forEach(ValidationUtil::valideerObject);
        zaakafhandelParameters.getUserEventListenerParametersCollection().forEach(ValidationUtil::valideerObject);
        zaakafhandelParameters.getMailtemplateKoppelingen().forEach(ValidationUtil::valideerObject);
        zaakafhandelParameters.setCreatiedatum(ZonedDateTime.now());
        entityManager.persist(zaakafhandelParameters);
        return zaakafhandelParameters;
    }

    public ZaakafhandelParameters updateZaakafhandelParameters(final ZaakafhandelParameters zaakafhandelParameters) {
        valideerObject(zaakafhandelParameters);
        zaakafhandelParameters.getHumanTaskParametersCollection().forEach(ValidationUtil::valideerObject);
        zaakafhandelParameters.setCreatiedatum(entityManager.find(ZaakafhandelParameters.class, zaakafhandelParameters.getId()).getCreatiedatum());
        return entityManager.merge(zaakafhandelParameters);
    }

    public Optional<UUID> findZaaktypeUUIDByProductaanvraagType(final String productaanvraagType) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UUID> query = builder.createQuery(UUID.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.select(root.get(ZAAKTYPE_UUID)).where(builder.equal(root.get(PRODUCTAANVRAAGTYPE), productaanvraagType));
        query.orderBy(builder.desc(root.get(CREATIEDATUM)));
        final List<UUID> resultList = entityManager.createQuery(query).getResultList();
        if (!resultList.isEmpty()) {
            if (resultList.size() > 1) {
                LOG.warning(String.format("Er zijn meerdere zaaktypes gevonden voor productaanvraag type: '%s'",
                                          productaanvraagType));
            }
            return Optional.of(resultList.get(0));
        }
        return Optional.empty();
    }

    public List<ZaakbeeindigReden> listZaakbeeindigRedenen() {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakbeeindigReden> query = builder.createQuery(ZaakbeeindigReden.class);
        final Root<ZaakbeeindigReden> root = query.from(ZaakbeeindigReden.class);
        query.orderBy(builder.asc(root.get("naam")));
        final TypedQuery<ZaakbeeindigReden> emQuery = entityManager.createQuery(query);
        return emQuery.getResultList();
    }


    /**
     * Zaaktype is aangepast, indien geen concept, dan de zaakafhandelparameters van de vorige versie zoveel mogelijk overnemen
     *
     * @param zaaktypeUri uri van het nieuwe zaaktype
     */
    @CacheRemoveAll(cacheName = ZAC_ZAAKAFHANDELPARAMETERS)
    public void zaaktypeAangepast(final URI zaaktypeUri) {
        ztcClientService.clearZaaktypeCache();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaaktypeUri);
        if (!zaaktype.getConcept()) {
            final String omschrijving = zaaktype.getOmschrijving();
            final ZaakafhandelParameters vorigeZaakafhandelparameters = readRecentsteZaakafhandelParameters(omschrijving);
            final ZaakafhandelParameters nieuweZaakafhandelParameters = new ZaakafhandelParameters();
            nieuweZaakafhandelParameters.setZaakTypeUUID(UriUtil.uuidFromURI(zaaktype.getUrl()));
            nieuweZaakafhandelParameters.setZaaktypeOmschrijving(zaaktype.getOmschrijving());
            nieuweZaakafhandelParameters.setCaseDefinitionID(vorigeZaakafhandelparameters.getCaseDefinitionID());
            nieuweZaakafhandelParameters.setGroepID(vorigeZaakafhandelparameters.getGroepID());
            nieuweZaakafhandelParameters.setGebruikersnaamMedewerker(vorigeZaakafhandelparameters.getGebruikersnaamMedewerker());
            if (zaaktype.isServicenormBeschikbaar()) {
                nieuweZaakafhandelParameters.setEinddatumGeplandWaarschuwing(vorigeZaakafhandelparameters.getEinddatumGeplandWaarschuwing());
            }
            nieuweZaakafhandelParameters.setUiterlijkeEinddatumAfdoeningWaarschuwing(
                    vorigeZaakafhandelparameters.getUiterlijkeEinddatumAfdoeningWaarschuwing());
            nieuweZaakafhandelParameters.setIntakeMail(vorigeZaakafhandelparameters.getIntakeMail());
            nieuweZaakafhandelParameters.setAfrondenMail(vorigeZaakafhandelparameters.getAfrondenMail());
            nieuweZaakafhandelParameters.setProductaanvraagtype(vorigeZaakafhandelparameters.getProductaanvraagtype());
            nieuweZaakafhandelParameters.setDomein(vorigeZaakafhandelparameters.getDomein());

            mapHumanTaskParameters(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters);
            mapUserEventListenerParameters(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters);
            mapZaakbeeindigGegevens(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters, zaaktype);
            mapMailtemplateKoppelingen(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters);

            createZaakafhandelParameters(nieuweZaakafhandelParameters);
        }
    }

    private ZaakafhandelParameters readRecentsteZaakafhandelParameters(final String zaaktypeOmschrijving) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZaakafhandelParameters> query = builder.createQuery(ZaakafhandelParameters.class);
        final Root<ZaakafhandelParameters> root = query.from(ZaakafhandelParameters.class);
        query.select(root).where(builder.equal(root.get(ZAAKTYPE_OMSCHRIJVING), zaaktypeOmschrijving));
        query.orderBy(builder.desc(root.get(CREATIEDATUM)));
        final List<ZaakafhandelParameters> resultList = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return new ZaakafhandelParameters();
        }
    }

    /**
     * Kopieren van de HumanTaskParameters van de oude ZaakafhandelParameters naar de nieuw ZaakafhandelParameters
     *
     * @param vorigeZaakafhandelparameters bron
     * @param nieuweZaakafhandelParameters bestemming
     */
    private void mapHumanTaskParameters(final ZaakafhandelParameters vorigeZaakafhandelparameters, final ZaakafhandelParameters nieuweZaakafhandelParameters) {
        final HashSet<HumanTaskParameters> humanTaskParametersCollection = new HashSet<>();
        vorigeZaakafhandelparameters.getHumanTaskParametersCollection().forEach(humanTaskParameters -> {
            final HumanTaskParameters nieuweHumanTaskParameters = new HumanTaskParameters();
            nieuweHumanTaskParameters.setDoorlooptijd(humanTaskParameters.getDoorlooptijd());
            nieuweHumanTaskParameters.setActief(humanTaskParameters.isActief());
            nieuweHumanTaskParameters.setStartformulierDefinitieID(humanTaskParameters.getStartformulierDefinitieID());
            nieuweHumanTaskParameters.setAfhandelformulierDefinitieID(
                    humanTaskParameters.getAfhandelformulierDefinitieID());
            nieuweHumanTaskParameters.setPlanItemDefinitionID(humanTaskParameters.getPlanItemDefinitionID());
            nieuweHumanTaskParameters.setGroepID(humanTaskParameters.getGroepID());
            humanTaskParametersCollection.add(nieuweHumanTaskParameters);
        });
        nieuweZaakafhandelParameters.setHumanTaskParametersCollection(humanTaskParametersCollection);
    }

    /**
     * Kopieren van de UserEventListenerParameters van de oude ZaakafhandelParameters naar de nieuw ZaakafhandelParameters
     *
     * @param vorigeZaakafhandelparameters bron
     * @param nieuweZaakafhandelParameters bestemming
     */
    private void mapUserEventListenerParameters(final ZaakafhandelParameters vorigeZaakafhandelparameters,
            final ZaakafhandelParameters nieuweZaakafhandelParameters) {
        final HashSet<UserEventListenerParameters> userEventListenerParametersCollection = new HashSet<>();
        vorigeZaakafhandelparameters.getUserEventListenerParametersCollection().forEach(userEventListenerParameters -> {
            final UserEventListenerParameters nieuweUserEventListenerParameters = new UserEventListenerParameters();
            nieuweUserEventListenerParameters.setPlanItemDefinitionID(userEventListenerParameters.getPlanItemDefinitionID());
            nieuweUserEventListenerParameters.setToelichting(userEventListenerParameters.getToelichting());
            userEventListenerParametersCollection.add(nieuweUserEventListenerParameters);
        });
        nieuweZaakafhandelParameters.setUserEventListenerParametersCollection(userEventListenerParametersCollection);
    }

    /**
     * Kopieren van de ZaakbeeindigGegevens van de oude ZaakafhandelParameters naar de nieuw ZaakafhandelParameters
     *
     * @param vorigeZaakafhandelparameters bron
     * @param nieuweZaakafhandelParameters bestemming
     * @param nieuwZaaktype                het nieuwe zaaktype om de resultaten van te lezen
     */
    private void mapZaakbeeindigGegevens(final ZaakafhandelParameters vorigeZaakafhandelparameters,
            final ZaakafhandelParameters nieuweZaakafhandelParameters, final Zaaktype nieuwZaaktype) {

        final List<Resultaattype> nieuweResultaattypen = nieuwZaaktype.getResultaattypen().stream().map(rt -> ztcClientService.readResultaattype(rt)).toList();
        nieuweZaakafhandelParameters.setNietOntvankelijkResultaattype(
                mapVorigResultaattypeOpNieuwResultaattype(nieuweResultaattypen, vorigeZaakafhandelparameters.getNietOntvankelijkResultaattype()));

        final HashSet<ZaakbeeindigParameter> zaakbeeindigParametersCollection = new HashSet<>();
        vorigeZaakafhandelparameters.getZaakbeeindigParameters().forEach(vorigeZaakbeeindigParameter -> {
            final UUID nieuwResultaattypeUUID = mapVorigResultaattypeOpNieuwResultaattype(nieuweResultaattypen,
                                                                                          vorigeZaakbeeindigParameter.getResultaattype());
            if (nieuwResultaattypeUUID != null) {
                final ZaakbeeindigParameter nieuweZaakbeeindigParameters = new ZaakbeeindigParameter();
                nieuweZaakbeeindigParameters.setZaakbeeindigReden(vorigeZaakbeeindigParameter.getZaakbeeindigReden());
                nieuweZaakbeeindigParameters.setResultaattype(nieuwResultaattypeUUID);
                zaakbeeindigParametersCollection.add(nieuweZaakbeeindigParameters);
            }
        });
        nieuweZaakafhandelParameters.setZaakbeeindigParameters(zaakbeeindigParametersCollection);
    }

    private void mapMailtemplateKoppelingen(final ZaakafhandelParameters vorigeZaakafhandelparameters,
            final ZaakafhandelParameters nieuweZaakafhandelParameters) {
        final HashSet<MailtemplateKoppeling> mailtemplateKoppelingen = new HashSet<>();
        vorigeZaakafhandelparameters.getMailtemplateKoppelingen().forEach(mailtemplateKoppeling -> {
            final MailtemplateKoppeling nieuweMailtemplateKoppeling = new MailtemplateKoppeling();
            nieuweMailtemplateKoppeling.setMailTemplate(mailtemplateKoppeling.getMailTemplate());
            nieuweMailtemplateKoppeling.setZaakafhandelParameters(nieuweZaakafhandelParameters);
            mailtemplateKoppelingen.add(nieuweMailtemplateKoppeling);
        });
        nieuweZaakafhandelParameters.setMailtemplateKoppelingen(mailtemplateKoppelingen);
    }

    private UUID mapVorigResultaattypeOpNieuwResultaattype(final List<Resultaattype> nieuweResultaattypen, final UUID vorigResultaattypeUUID) {
        if (vorigResultaattypeUUID == null) {
            return null;
        }
        final Resultaattype vorigResultaattype = ztcClientService.readResultaattype(vorigResultaattypeUUID);
        return nieuweResultaattypen.stream()
                .filter(resultaattype -> resultaattype.getOmschrijving().equals(vorigResultaattype.getOmschrijving())).findAny()
                .map(resultaattype -> UriUtil.uuidFromURI(resultaattype.getUrl())).orElse(null);
    }
}
