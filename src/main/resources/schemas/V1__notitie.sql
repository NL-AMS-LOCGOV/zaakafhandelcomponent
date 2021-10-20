/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

create sequence ${schema}.notitie_sq start with 1 increment by 1 no maxvalue no minvalue cache 1;

create table ${schema}.notitie
(
    id                         bigint  not null,
    zaak_uuid                  uuid,
    tekst                      text    not null,
    tijdstip_laatste_wijziging date    not null,
    gebruikersnaam_medewerker  varchar not null,
    constraint notitie_pk primary key (id)
);
