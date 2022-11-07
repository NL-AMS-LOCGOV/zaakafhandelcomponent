INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Zaak ontvankelijk', 'Wij hebben uw verzoek in behandeling genomen (zaaknummer: {zaaknummer}',
        '<pre><code>Beste klant,

Uw verzoek over {zaaktypenaam} met zaaknummer {zaaknummer} is in behandeling genomen. Voor meer informatie gaat u naar Mijn loket.

Met vriendelijke groet,

Gemeente</code></pre>', 'ZAAK_ONTVANKELIJK');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Zaak niet ontvankelijk', 'Wij hebben uw verzoek niet ontvankelijk verklaard (zaaknummer: {zaaknummer}',
        '<pre><code>Beste klant,

Uw verzoek over {zaaktypenaam} met zaaknummer {zaaknummer} is niet ontvankelijk verklaard. Voor meer informatie gaat u naar Mijn loket.

Met vriendelijke groet,

Gemeente</code></pre>', 'ZAAK_NIET_ONTVANKELIJK');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Zaak afgehandeld', 'Wij hebben uw verzoek afgehandeld (zaaknummer: {zaaknummer}',
        '<pre><code>Beste klant,

Uw verzoek betreffende {zaaktypenaam} met zaaknummer {zaaknummer} is afgehandeld. Voor meer informatie gaat u naar Mijn loket.

Met vriendelijke groet,

Gemeente</code></pre>', 'ZAAK_AFGEHANDELD');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Proces aanvullende informatie', 'Aanvullende informatie nodig voor zaak',
        '<pre><code>Beste klant,

Voor het behandelen van de zaak hebben wij de volgende informatie van u nodig:
- omschrijf informatie 1
- omschrijf informatie 2

We ontvangen de informatie graag uiterlijk op datum x. U kunt dit aanleveren door deze per e-mail te sturen naar mailadres Y.
Vermeld op de informatie ook het zaaknummer van uw zaak.

Met vriendelijke groet,

Gemeente</code></pre>', 'PROCES_AANVULLENDE_INFORMATIE');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Proces ontvangsbevestiging', 'Ontvangstbevestiging van zaak {zaaknummer}',
        '<pre><code>Beste,

Dit is de ontvangstbevestiging van zaak {zaaknummer}.</code></pre>', 'PROCES_ONTVANGSTBEVESTIGING');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak document toegevoegd', 'Zaakdocument toegevoegd',
        'Er is een document aan uw zaak toegevoegd.', 'SIGNALERING_ZAAK_DOCUMENT_TOEGEVOEGD');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak op naam', 'Zaak op naam',
        'Er is een zaak op uw naam gezet.', 'SIGNALERING_ZAAK_OP_NAAM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak verlopend streefdatum', 'Zaak verloopt',
        'Uw zaak nadert de streefdatum.', 'SIGNALERING_ZAAK_VERLOPEND_STREEFDATUM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak verlopend fatale datum', 'Zaak verloopt',
        'Uw zaak nadert de fatale datum.', 'SIGNALERING_ZAAK_VERLOPEND_FATALE_DATUM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering taak op naam', 'Taak op naam',
        'Er is een taak op uw naam gezet.', 'SIGNALERING_TAAK_OP_NAAM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering taak verlopen', 'Taak verloopt',
        'Uw taak heeft de streefdatum bereikt.', 'SIGNALERING_TAAK_VERLOPEN');
