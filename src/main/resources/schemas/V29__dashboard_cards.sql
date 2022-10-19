/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
CREATE SEQUENCE ${schema}.sq_dashboard_card START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.dashboard_card
(
    id_dashboard_card   BIGINT  NOT NULL,
    id_medewerker       VARCHAR NOT NULL,
    dashboard_card_enum VARCHAR NOT NULL,
    volgorde            INTEGER NOT NULL,
    CONSTRAINT pk_dashboard_card PRIMARY KEY (id_dashboard_card),
    CONSTRAINT un_dashboard_card UNIQUE (id_medewerker, dashboard_card_enum)
);
CREATE INDEX ix_dashboard_card_medewerker ON ${schema}.dashboard_card USING btree (id_medewerker);
CREATE INDEX ix_dashboard_card_card ON ${schema}.dashboard_card USING btree (dashboard_card_enum);
CREATE INDEX ix_dashboard_card_volgorde ON ${schema}.dashboard_card USING btree (volgorde);
