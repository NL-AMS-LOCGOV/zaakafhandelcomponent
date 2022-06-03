/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
CREATE SEQUENCE ${schema}.sq_signalering_verzonden START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.signalering_verzonden
(
    id_signalering_verzonden BIGINT                   NOT NULL,
    signaleringtype_enum     VARCHAR                  NOT NULL, -- bijvoorbeeld: ZAAK_VERLOPEND, TAAK_VERLOPEN
    subjectfield_enum        VARCHAR                  NOT NULL, -- bijvoorbeeld: DUE, FATAL
    targettype_enum          VARCHAR                  NOT NULL, -- bijvoorbeeld: GROEP, MEDEWERKER
    target                   VARCHAR                  NOT NULL, -- bijvoorbeeld: groep.id, medewerker.gebruikersnaam
    subject                  VARCHAR                  NOT NULL, -- bijvoorbeeld: zaak.uuid, taak.id
    tijdstip                 TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_signalering_verzonden PRIMARY KEY (id_signalering_verzonden),
    CONSTRAINT fk_signalering_type FOREIGN KEY (signaleringtype_enum)
        REFERENCES ${schema}.signaleringtype (signaleringtype_enum)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX ix_signalering_verzonden_type ON ${schema}.signalering_verzonden USING btree (signaleringtype_enum);
CREATE INDEX ix_signalering_verzonden_subjectfield ON ${schema}.signalering_verzonden USING btree (subjectfield_enum);
CREATE INDEX ix_signalering_verzonden_targettype ON ${schema}.signalering_verzonden USING btree (targettype_enum);
CREATE INDEX ix_signalering_verzonden_target ON ${schema}.signalering_verzonden USING btree (target);
CREATE INDEX ix_signalering_verzonden_subject ON ${schema}.signalering_verzonden USING btree (subject);
CREATE INDEX ix_signalering_verzonden_tijdstip ON ${schema}.signalering_verzonden USING btree (tijdstip);
