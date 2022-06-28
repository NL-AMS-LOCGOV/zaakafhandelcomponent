package net.atos.zac.enkelvoudiginformatieobject;

import net.atos.client.zgw.drc.DRCClientService;
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Transactional
public class EnkelvoudigInformatieObjectLockService {

    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private EntityManager entityManager;

    @Inject
    private DRCClientService drcClientService;

    public EnkelvoudigInformatieObjectLock create(final UUID enkelvoudiginformatieobejctUUID, final String idUser) {
        final EnkelvoudigInformatieObjectLock enkelvoudigInformatieobjectLock = new EnkelvoudigInformatieObjectLock();
        enkelvoudigInformatieobjectLock.setEnkelvoudiginformatieobjectUUID(enkelvoudiginformatieobejctUUID);
        enkelvoudigInformatieobjectLock.setUserId(idUser);
        enkelvoudigInformatieobjectLock.setLock(drcClientService.lockEnkelvoudigInformatieobject(enkelvoudiginformatieobejctUUID));
        entityManager.persist(enkelvoudigInformatieobjectLock);
        return enkelvoudigInformatieobjectLock;
    }

    public EnkelvoudigInformatieObjectLock find(final UUID enkelvoudiginformatieobjectUUID) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EnkelvoudigInformatieObjectLock> query = builder.createQuery(EnkelvoudigInformatieObjectLock.class);
        final Root<EnkelvoudigInformatieObjectLock> root = query.from(EnkelvoudigInformatieObjectLock.class);
        query.select(root).where(builder.equal(root.get("enkelvoudiginformatieobjectUUID"), enkelvoudiginformatieobjectUUID));
        final List<EnkelvoudigInformatieObjectLock> resultList = entityManager.createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public void delete(final UUID enkelvoudigInformatieObjectUUID, final String idUser) throws IllegalAccessException {
        final EnkelvoudigInformatieObjectLock enkelvoudigInformatieObjectLock = find(enkelvoudigInformatieObjectUUID);
        if (enkelvoudigInformatieObjectLock != null && enkelvoudigInformatieObjectLock.getUserId().equals(idUser)) {
            drcClientService.unlockEnkelvoudigInformatieobject(enkelvoudigInformatieObjectUUID,
                                                               enkelvoudigInformatieObjectLock.getLock());
            entityManager.remove(enkelvoudigInformatieObjectLock);
        } else {
            throw new IllegalAccessException("Niet toegestaan om document te wijzigen");
        }
    }
}
