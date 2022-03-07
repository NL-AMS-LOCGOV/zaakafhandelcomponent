/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

-- ISO 639-2/B taalcodes (https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes)
CREATE SEQUENCE ${schema}.sq_taal START WITH 6 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE ${schema}.taal
(
    id_taal BIGINT  NOT NULL,
    code    VARCHAR NOT NULL,
    naam    VARCHAR NOT NULL,
    name    VARCHAR NOT NULL,
    native  VARCHAR NOT NULL,
    CONSTRAINT pk_taal PRIMARY KEY (id_taal),
    CONSTRAINT un_taal_code UNIQUE (code)
);
CREATE INDEX ix_taal_naam ON ${schema}.taal USING btree (naam);
CREATE INDEX ix_taal_name ON ${schema}.taal USING btree (name);
CREATE INDEX ix_taal_native ON ${schema}.taal USING btree (native);

INSERT INTO ${schema}.taal(id_taal, code, naam, name, native)
VALUES (1, 'dut', 'Nederlands', 'Dutch', 'Nederlands');
INSERT INTO ${schema}.taal(id_taal, code, naam, name, native)
VALUES (2, 'fre', 'Frans', 'French', 'français');
INSERT INTO ${schema}.taal(id_taal, code, naam, name, native)
VALUES (3, 'eng', 'Engels', 'English', 'English');
INSERT INTO ${schema}.taal(id_taal, code, naam, name, native)
VALUES (4, 'ger', 'Duits', 'German', 'Deutsch');
INSERT INTO ${schema}.taal(id_taal, code, naam, name, native)
VALUES (5, 'fry', 'Fries', 'Frisian', 'Frysk');
