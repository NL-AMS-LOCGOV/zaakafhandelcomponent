UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{REGISTRATIEDATUM}', '{ZAAKREGISTRATIEDATUM}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{REGISTRATIEDATUM}', '{ZAAKREGISTRATIEDATUM}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{STARTDATUM}', '{ZAAKSTARTDATUM}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{STARTDATUM}', '{ZAAKSTARTDATUM}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{STREEFDATUM}', '{ZAAKSTREEFDATUM}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{STREEFDATUM}', '{ZAAKSTREEFDATUM}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{FATALEDATUM}', '{ZAAKFATALEDATUM}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{FATALEDATUM}', '{ZAAKFATALEDATUM}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{OMSCHRIJVINGZAAK}', '{ZAAKOMSCHRIJVING}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{OMSCHRIJVINGZAAK}', '{ZAAKOMSCHRIJVING}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{TOELICHTINGZAAK}', '{ZAAKTOELICHTING}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{TOELICHTINGZAAK}', '{ZAAKTOELICHTING}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{INITIATOR}', '{ZAAKINITIATOR}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{INITIATOR}', '{ZAAKINITIATOR}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{ADRESINITIATOR}', '{ZAAKINITIATOR_ADRES}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{ADRESINITIATOR}', '{ZAAKINITIATOR_ADRES}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{TOEGEWEZENGROEPZAAK}', '{ZAAKBEHANDELAAR_GROEP}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{TOEGEWEZENGROEPZAAK}', '{ZAAKBEHANDELAAR_GROEP}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{TOEGEWEZENGEBRUIKERZAAK}', '{ZAAKBEHANDELAAR_MEDEWERKER}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{TOEGEWEZENGEBRUIKERZAAK}', '{ZAAKBEHANDELAAR_MEDEWERKER}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{TOEGEWEZENGROEPTAAK}', '{TAAKBEHANDELAAR_GROEP}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{TOEGEWEZENGROEPTAAK}', '{TAAKBEHANDELAAR_GROEP}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{TOEGEWEZENGEBRUIKERTAAK}', '{TAAKBEHANDELAAR_MEDEWERKER}');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{TOEGEWEZENGEBRUIKERTAAK}', '{TAAKBEHANDELAAR_MEDEWERKER}');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{ZAAK', '{ZAAK_');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{ZAAK', '{ZAAK_');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{TAAK', '{TAAK_');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{TAAK', '{TAAK_');

UPDATE ${schema}.mail_template
SET onderwerp=REPLACE(onderwerp, '{DOCUMENT', '{DOCUMENT_');
UPDATE ${schema}.mail_template
SET body=REPLACE(body, '{DOCUMENT', '{DOCUMENT_');
