/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE SEQUENCE ${schema}.sq_signalering_instellingen START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.signalering_instellingen
(
    id_signalering_instellingen BIGINT  NOT NULL,
    signaleringtype_enum        VARCHAR NOT NULL,
    id_groep                    VARCHAR,
    id_medewerker               VARCHAR,
    dashboard                   BOOLEAN NOT NULL DEFAULT FALSE,
    mail                        BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_signalering_instellingen PRIMARY KEY (id_signalering_instellingen),
    CONSTRAINT fk_signalering_instellingen_type FOREIGN KEY (signaleringtype_enum)
        REFERENCES ${schema}.signaleringtype (signaleringtype_enum)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT re_signalering_instellingen_owner CHECK ((id_groep IS NULL AND id_medewerker IS NOT NULL) OR (id_groep IS NOT NULL AND id_medewerker IS NULL)),
    CONSTRAINT un_signalering_instellingen UNIQUE (signaleringtype_enum, id_groep, id_medewerker)
);
CREATE INDEX ix_signalering_instellingen_type ON ${schema}.signalering_instellingen USING btree (signaleringtype_enum);
CREATE INDEX ix_signalering_instellingen_groep ON ${schema}.signalering_instellingen USING btree (id_groep);
CREATE INDEX ix_signalering_instellingen_medewerker ON ${schema}.signalering_instellingen USING btree (id_medewerker);
