/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

UPDATE ${schema}.mail_template
SET body=REPLACE(body, 'uiterlijke datum', 'fatale datum')
WHERE mail_template_enum = 'SIGNALERING_TAAK_VERLOPEN';

UPDATE ${schema}.mail_template
SET mail_template_enum='TAAK_AANVULLENDE_INFORMATIE'
WHERE mail_template_enum='PROCES_AANVULLENDE_INFORMATIE';

UPDATE ${schema}.mail_template
SET mail_template_enum='TAAK_ONTVANGSTBEVESTIGING'
WHERE mail_template_enum='PROCES_ONTVANGSTBEVESTIGING';

UPDATE ${schema}.mail_template
SET mail_template_enum='TAAK_ADVIES_EXTERN'
WHERE mail_template_enum='PROCES_ADVIES';

UPDATE ${schema}.mail_template
SET mail_template_naam='Taak formulierdefinitie: Aanvullende informatie'
WHERE mail_template_enum='TAAK_AANVULLENDE_INFORMATIE' AND default_mailtemplate=true;

UPDATE ${schema}.mail_template
SET mail_template_naam='Ontvangstbevestiging'
WHERE mail_template_enum='TAAK_ONTVANGSTBEVESTIGING' AND default_mailtemplate=true;

UPDATE ${schema}.mail_template
SET mail_template_naam='Taak formulierdefinitie: Extern advies (met e-mail)'
WHERE mail_template_enum='TAAK_ADVIES_EXTERN' AND default_mailtemplate=true;
