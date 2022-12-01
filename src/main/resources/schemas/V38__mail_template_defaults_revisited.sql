UPDATE ${schema}.mail_template
SET body=REPLACE(body, 'Klik om de zaak te bekijken {ZAAKURL}', '{DOCUMENTLINK}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, 'Klik om naar de zaak afhandelcomponent te gaan: {ZAAKURL}', '{ZAAKLINK}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, 'Klik om naar de taak te gaan {TAAKURL}', '{TAAKLINK}');
