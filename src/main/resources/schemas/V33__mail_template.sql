/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.mail_template
(
    id_mail_template                 BIGINT  NOT NULL,
    mail_template_naam               VARCHAR NOT NULL,
    onderwerp                        VARCHAR NOT NULL,
    body                             VARCHAR NOT NULL,
    mail_template_enum               VARCHAR NOT NULL,
    parent                           BIGINT,
    CONSTRAINT pk_mail_template PRIMARY KEY (id_mail_template),
    CONSTRAINT fk_mail_template_parent FOREIGN KEY (parent)
        REFERENCES ${schema}.mail_template (id_mail_template)
        MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT un_mail_template_enum_naam UNIQUE (mail_template_enum, mail_template_naam)
);

CREATE SEQUENCE ${schema}.sq_mail_template START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE INDEX idx_mail_template_mail_template_enum ON ${schema}.mail_template USING btree (mail_template_enum);
CREATE INDEX idx_mail_template_mail_template_naam ON ${schema}.mail_template USING btree (mail_template_naam);
CREATE INDEX idx_mail_template_parent ON ${schema}.mail_template USING btree (parent);
