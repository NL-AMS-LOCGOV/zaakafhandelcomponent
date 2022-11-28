/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.mail_template_koppelingen
(
    id_mail_template_koppelingen    BIGINT  NOT NULL,
    uuid_zaaktype                   UUID NOT NULL,
    id_mail_template                BIGINT NOT NULL,
    CONSTRAINT pk_mail_template_koppelingen PRIMARY KEY (id_mail_template_koppelingen),
    CONSTRAINT fk_id_mail_template FOREIGN KEY (id_mail_template) REFERENCES ${schema}.mail_template (id_mail_template)
        MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE SEQUENCE ${schema}.sq_mail_template_koppelingen START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_mail_template_koppelingen_uuid_zaaktype ON ${schema}.mail_template_koppelingen
    USING btree (uuid_zaaktype);
CREATE INDEX idx_mail_template_koppelingen_id_mail_template ON ${schema}.mail_template_koppelingen
    USING btree (id_mail_template);

ALTER TABLE IF EXISTS ${schema}.mail_template ADD default_mailtemplate BOOLEAN NOT NULL DEFAULT FALSE;
CREATE INDEX idx_mail_template_default_mailtemplate ON ${schema}.mail_template USING btree (default_mailtemplate);
UPDATE ${schema}.mail_template SET default_mailtemplate=TRUE WHERE parent IS NULL;
ALTER TABLE IF EXISTS ${schema}.mail_template DROP COLUMN parent;
