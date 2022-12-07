UPDATE ${schema}.mail_template
SET body=REPLACE(body, 'uiterlijke datum', 'fatale datum')
WHERE mail_template_naam = 'SIGNALERING_TAAK_VERLOPEN';
