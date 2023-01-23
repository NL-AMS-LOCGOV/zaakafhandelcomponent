/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum, default_mailtemplate)
VALUES (nextval('${schema}.sq_mail_template'), 'Algemene e-mail', 'Informatie over zaak {ZAAK_NUMMER}',
        '<p>Beste,</p><p></p><p>Schrijf hier uw bericht</p><p></p><p>Met vriendelijke groet,</p><p>Gemeente</p>',
        'ZAAK_ALGEMEEN', true);
