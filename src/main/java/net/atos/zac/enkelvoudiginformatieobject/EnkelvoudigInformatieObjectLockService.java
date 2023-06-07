package net.atos.zac.enkelvoudiginformatieobject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;
import net.atos.zac.util.UriUtil;

@ApplicationScoped
@Transactional
public class EnkelvoudigInformatieObjectLockService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZRCClientService zrcClientService;


    public EnkelvoudigInformatieObjectLock createLock(final UUID enkelvoudiginformatieobejctUUID, final String idUser) {
        final EnkelvoudigInformatieObjectLock enkelvoudigInformatieobjectLock = new EnkelvoudigInformatieObjectLock();
        enkelvoudigInformatieobjectLock.setEnkelvoudiginformatieobjectUUID(enkelvoudiginformatieobejctUUID);
        enkelvoudigInformatieobjectLock.setUserId(idUser);
        enkelvoudigInformatieobjectLock.setLock(
                drcClientService.lockEnkelvoudigInformatieobject(enkelvoudiginformatieobejctUUID));
        entityManager.persist(enkelvoudigInformatieobjectLock);
        return enkelvoudigInformatieobjectLock;
    }

    public Optional<EnkelvoudigInformatieObjectLock> findLock(final UUID enkelvoudiginformatieobjectUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EnkelvoudigInformatieObjectLock> query = builder.createQuery(
                EnkelvoudigInformatieObjectLock.class);
        final Root<EnkelvoudigInformatieObjectLock> root = query.from(EnkelvoudigInformatieObjectLock.class);
        query.select(root)
                .where(builder.equal(root.get("enkelvoudiginformatieobjectUUID"), enkelvoudiginformatieobjectUUID));
        final List<EnkelvoudigInformatieObjectLock> resultList = entityManager.createQuery(query).getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    public EnkelvoudigInformatieObjectLock readLock(final UUID enkelvoudiginformatieobjectUUID) {
        return findLock(enkelvoudiginformatieobjectUUID).orElseThrow(() -> new RuntimeException(
                "Lock for EnkelvoudigInformatieObject with uuid '%s' not found".formatted(
                        enkelvoudiginformatieobjectUUID)));
    }

    public void deleteLock(final UUID enkelvoudigInformatieObjectUUID) {
        findLock(enkelvoudigInformatieObjectUUID)
                .ifPresent(lock -> {
                    drcClientService.unlockEnkelvoudigInformatieobject(enkelvoudigInformatieObjectUUID, lock.getLock());
                    entityManager.remove(lock);
                });
    }

    public boolean hasLockedInformatieobjecten(final Zaak zaak) {
        final List<UUID> informatieobjectUUIDs = zrcClientService.listZaakinformatieobjecten(zaak).stream()
                .map(zaakInformatieobject -> UriUtil.uuidFromURI(zaakInformatieobject.getInformatieobject())).toList();
        if (CollectionUtils.isEmpty(informatieobjectUUIDs)) {
            return false;
        }
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EnkelvoudigInformatieObjectLock> query = builder.createQuery(
                EnkelvoudigInformatieObjectLock.class);
        final Root<EnkelvoudigInformatieObjectLock> root = query.from(EnkelvoudigInformatieObjectLock.class);
        query.select(root).where(root.get("enkelvoudiginformatieobjectUUID").in(informatieobjectUUIDs));
        final List<EnkelvoudigInformatieObjectLock> resultList = entityManager.createQuery(query).getResultList();
        return !resultList.isEmpty();
    }
}
