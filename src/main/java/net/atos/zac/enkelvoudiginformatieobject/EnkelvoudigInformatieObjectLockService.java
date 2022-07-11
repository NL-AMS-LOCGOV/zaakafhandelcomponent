package net.atos.zac.enkelvoudiginformatieobject;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;

@ApplicationScoped
@Transactional
public class EnkelvoudigInformatieObjectLockService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private DRCClientService drcClientService;

    public EnkelvoudigInformatieObjectLock createLock(final UUID enkelvoudiginformatieobejctUUID, final String idUser) {
        final EnkelvoudigInformatieObjectLock enkelvoudigInformatieobjectLock = new EnkelvoudigInformatieObjectLock();
        enkelvoudigInformatieobjectLock.setEnkelvoudiginformatieobjectUUID(enkelvoudiginformatieobejctUUID);
        enkelvoudigInformatieobjectLock.setUserId(idUser);
        enkelvoudigInformatieobjectLock.setLock(drcClientService.lockEnkelvoudigInformatieobject(enkelvoudiginformatieobejctUUID));
        entityManager.persist(enkelvoudigInformatieobjectLock);
        return enkelvoudigInformatieobjectLock;
    }

    public EnkelvoudigInformatieObjectLock findLock(final UUID enkelvoudiginformatieobjectUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EnkelvoudigInformatieObjectLock> query = builder.createQuery(EnkelvoudigInformatieObjectLock.class);
        final Root<EnkelvoudigInformatieObjectLock> root = query.from(EnkelvoudigInformatieObjectLock.class);
        query.select(root).where(builder.equal(root.get("enkelvoudiginformatieobjectUUID"), enkelvoudiginformatieobjectUUID));
        final List<EnkelvoudigInformatieObjectLock> resultList = entityManager.createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public void deleteLock(final UUID enkelvoudigInformatieObjectUUID) {
        final EnkelvoudigInformatieObjectLock enkelvoudigInformatieObjectLock = findLock(enkelvoudigInformatieObjectUUID);
        if (enkelvoudigInformatieObjectLock != null) {
            drcClientService.unlockEnkelvoudigInformatieobject(enkelvoudigInformatieObjectUUID, enkelvoudigInformatieObjectLock.getLock());
            entityManager.remove(enkelvoudigInformatieObjectLock);
        }
    }
}
