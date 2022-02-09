/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

-- Zaak-beeindig redenen
CREATE SEQUENCE ${schema}.sq_zaakbeeindigreden START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.zaakbeeindigreden
(
    id_zaakbeeindigreden BIGINT  NOT NULL,
    naam                 VARCHAR NOT NULL,
    CONSTRAINT pk_zaakbeeindigreden PRIMARY KEY (id_zaakbeeindigreden)
);
CREATE INDEX ix_beeindigreden_naam ON ${schema}.zaakbeeindigreden USING btree (naam);

INSERT INTO ${schema}.zaakbeeindigreden(id_zaakbeeindigreden, naam)
VALUES (-1, 'Verzoek is door initiator ingetrokken');
INSERT INTO ${schema}.zaakbeeindigreden(id_zaakbeeindigreden, naam)
VALUES (-2, 'Zaak is een duplicaat');
INSERT INTO ${schema}.zaakbeeindigreden(id_zaakbeeindigreden, naam)
VALUES (-3, 'Verzoek is bij verkeerde organisatie ingediend');

-- Zaak-beeindig parameters
CREATE SEQUENCE ${schema}.sq_zaakbeeindigparameter START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.zaakbeeindigparameter
(
    id_zaakbeeindigparameter  BIGINT  NOT NULL,
    id_zaakafhandelparameters BIGINT  NOT NULL,
    id_zaakbeeindigreden      BIGINT  NOT NULL,
    resultaattype_url         VARCHAR NOT NULL,
    CONSTRAINT pk_zaakbeeindigparameter PRIMARY KEY (id_zaakbeeindigparameter),
    CONSTRAINT un_sq_zaakbeeindigparameter UNIQUE (id_zaakafhandelparameters, id_zaakbeeindigreden),
    CONSTRAINT fk_beeindigparameter_afhandelparameters FOREIGN KEY (id_zaakafhandelparameters)
        REFERENCES ${schema}.zaakafhandelparameters (id_zaakafhandelparameters)
            MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_beeindigparameter_zaakbeeindigreden FOREIGN KEY (id_zaakbeeindigreden)
        REFERENCES ${schema}.zaakbeeindigreden (id_zaakbeeindigreden)
            MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX ix_beeindigparameter_afhandelparameters ON ${schema}.zaakbeeindigparameter USING btree (id_zaakafhandelparameters);
CREATE INDEX ix_beeindigparameter_zaakbeeindigreden ON ${schema}.zaakbeeindigparameter USING btree (id_zaakbeeindigreden);
CREATE INDEX ix_beeindigparameter_resultaattype ON ${schema}.zaakbeeindigparameter USING btree (resultaattype_url);
