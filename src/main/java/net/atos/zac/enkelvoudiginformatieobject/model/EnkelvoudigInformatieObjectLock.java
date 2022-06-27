package net.atos.zac.enkelvoudiginformatieobject.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.UUID;

import static net.atos.zac.util.FlywayIntegrator.SCHEMA;

@Entity
@Table(schema = SCHEMA, name = "enkelvoudiginformatieobject_lock")
@SequenceGenerator(schema = SCHEMA, name = "sq_enkelvoudiginformatieobject_lock", sequenceName =
        "sq_enkelvoudiginformatieobject_lock", allocationSize = 1)
public class EnkelvoudigInformatieObjectLock {

    @Id
    @GeneratedValue(generator = "sq_enkelvoudiginformatieobject_lock", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_enkelvoudiginformatieobject_lock")
    private Long id;

    @NotNull
    @Column(name = "uuid_enkelvoudiginformatieobject", nullable = false)
    private UUID enkelvoudiginformatieobjectUUID;

    @NotBlank
    @Column(name = "id_user", nullable = false)
    private String idUser;

    @NotBlank
    @Column(name = "lock", nullable = false)
    private String lock;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getEnkelvoudiginformatieobjectUUID() {
        return enkelvoudiginformatieobjectUUID;
    }

    public void setEnkelvoudiginformatieobjectUUID(final UUID enkelvoudiginformatieobjectUUID) {
        this.enkelvoudiginformatieobjectUUID = enkelvoudiginformatieobjectUUID;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(final String idUser) {
        this.idUser = idUser;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(final String lock) {
        this.lock = lock;
    }
}
