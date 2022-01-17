/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

CREATE TABLE ${schema}.signaleringtype
(
    signaleringtype_enum VARCHAR NOT NULL, -- bijvoorbeeld: ZAAK_OP_NAAM
    subjecttype_enum     VARCHAR NOT NULL, -- bijvoorbeeld: ZAAK, TAAK, DOCUMENT
    CONSTRAINT pk_signaleringtype PRIMARY KEY (signaleringtype_enum)
);
CREATE INDEX ix_signaleringtype_subjecttype ON ${schema}.signaleringtype USING btree (subjecttype_enum);

INSERT INTO ${schema}.signaleringtype(signaleringtype_enum, subjecttype_enum)
VALUES ('ZAAK_OP_NAAM', 'ZAAK');

CREATE SEQUENCE ${schema}.sq_signalering START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.signalering
(
    id_signalering       BIGINT                   NOT NULL,
    signaleringtype_enum VARCHAR                  NOT NULL, -- bijvoorbeeld: ZAAK_OP_NAAM
    targettype_enum      VARCHAR                  NOT NULL, -- bijvoorbeeld: GROEP, MEDEWERKER
    target               VARCHAR                  NOT NULL, -- bijvoorbeeld: groep.id, medewerker.gebruikersnaam
    subject              VARCHAR                  NOT NULL, -- bijvoorbeeld: zaak.uuid, taak.id, document.uuid
    tijdstip             TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_signalering PRIMARY KEY (id_signalering),
    CONSTRAINT fk_signalering_type FOREIGN KEY (signaleringtype_enum)
        REFERENCES ${schema}.signaleringtype (signaleringtype_enum)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX ix_signalering_type ON ${schema}.signalering USING btree (signaleringtype_enum);
CREATE INDEX ix_signalering_targettype ON ${schema}.signalering USING btree (targettype_enum);
CREATE INDEX ix_signalering_target ON ${schema}.signalering USING btree (target);
CREATE INDEX ix_signalering_subject ON ${schema}.signalering USING btree (subject);
CREATE INDEX ix_signalering_tijdstip ON ${schema}.signalering USING btree (tijdstip);
