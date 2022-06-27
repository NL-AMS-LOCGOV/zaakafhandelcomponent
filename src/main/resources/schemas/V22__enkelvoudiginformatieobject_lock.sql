/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.enkelvoudiginformatieobject_lock
(
    id_enkelvoudiginformatieobject_lock     BIGINT  NOT NULL,
    uuid_enkelvoudiginformatieobject        UUID    NOT NULL,
    id_user                                 VARCHAR NOT NULL,
    lock                                    VARCHAR NOT NULL,
    CONSTRAINT pk_enkelvoudiginformatieobject_lock PRIMARY KEY (id_enkelvoudiginformatieobject_lock),
    CONSTRAINT un_enkelvoudiginformatieobject_lock_uuid_enkelvoudiginformatieobject UNIQUE (uuid_enkelvoudiginformatieobject)
);

CREATE SEQUENCE ${schema}.sq_enkelvoudiginformatieobject_lock START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_enkelvoudiginformatieobject_lock_id_user ON ${schema}.enkelvoudiginformatieobject_lock USING btree (id_user);
