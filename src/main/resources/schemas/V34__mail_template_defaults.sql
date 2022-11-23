INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Zaak ontvankelijk', 'Wij hebben uw verzoek in behandeling genomen (zaaknummer: {zaaknummer})',
        '<p>Beste klant,</p><p></p><p>Uw verzoek over {zaaktypenaam} met zaaknummer {zaaknummer} is in behandeling genomen. Voor meer informatie gaat u naar Mijn Loket.</p><p></p><p>Met vriendelijke groet,</p><p>Gemeente</p>',
        'ZAAK_ONTVANKELIJK');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Zaak niet ontvankelijk', '<p>Wij hebben uw verzoek niet in behandeling genomen (zaaknummer: {ZAAKNUMMER})</p>',
        '<p>Beste {INITIATOR},</p><p></p><p>Uw verzoek over {ZAAKTYPE} met zaaknummer {ZAAKNUMMER} wordt niet in behandeling genomen. Voor meer informatie gaat u naar Mijn Loket.</p><p></p><p>Met vriendelijke groet,</p><p></p><p>Gemeente Dommeldam</p>',
        'ZAAK_NIET_ONTVANKELIJK');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Zaak afgehandeld', '<p>Wij hebben uw verzoek afgehandeld (zaaknummer: {ZAAKNUMMER})</p>',
        '<p>Beste {INITIATOR},</p><p></p><p>Uw verzoek over {ZAAKTYPE} met zaaknummer {ZAAKNUMMER} is afgehandeld. Voor meer informatie gaat u naar Mijn Loket.</p><p></p><p></p><p>Met vriendelijke groet,</p><p></p><p>Gemeente Dommeldam</p>',
        'ZAAK_AFGEHANDELD');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Proces aanvullende informatie', '<p>Aanvullende informatie nodig voor zaak {ZAAKNUMMER}</p>',
        '<p>Beste {INITIATOR},</p><p></p><p>Voor het behandelen van de zaak over {OMSCHRIJVINGZAAK} hebben wij de volgende informatie van u nodig:</p><ul><li><p>Omschrijf informatie 1</p></li><li><p>Omschrijf informatie 2</p></li></ul><p>We ontvangen de informatie graag uiterlijk op datum [vul datum in]. U kunt dit aanleveren door deze per e-mail te sturen naar [Vul email in]. Vermeld op de informatie ook het zaaknummer van uw zaak, dit is:{ZAAKNUMMER}</p><p></p><p>Met vriendelijke groet,</p><p></p><p>Gemeente Dommeldam</p>',
        'PROCES_AANVULLENDE_INFORMATIE');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Proces advies', '<p>Advies nodig voor zaak {ZAAKNUMMER}</p>',
        '<p>Beste ,</p><p></p><p>Wij hebben uw advies nodig over een zaak die wij behandelen.</p><p>{Vul adviesvraag in}</p><p></p><p>Met vriendelijke groet,</p><p></p><p>Gemeente Dommeldam</p>',
        'PROCES_ADVIES');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Proces ontvangsbevestiging', '<p>Ontvangstbevestiging van zaak {ZAAKNUMMER}</p>',
        '<p>Beste {INITIATOR},</p><p></p><p>Wij hebben uw verzoek ontvangen en deze op {REGISTRATIEDATUM} geregistreerd als zaak {ZAAKNUMMER}. U kunt dit kenmerk noemen als u contact heeft over de zaak.</p><p></p><p></p><p>Met vriendelijke groet,</p><p></p><p>Gemeente Dommeldam</p>',
        'PROCES_ONTVANGSTBEVESTIGING');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak document toegevoegd', '<p>Nieuw document aan zaak ‘{OMSCHRIJVINGZAAK}’ toegevoegd</p>',
        '<p>Hoi!</p><p>Er is een document aan zaak {ZAAKNUMMER} over {OMSCHRIJVINGZAAK} toegevoegd.</p><p>Klik om de zaak te bekijken {ZAAKURL}</p><p></p>',
        'SIGNALERING_ZAAK_DOCUMENT_TOEGEVOEGD');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak op naam', '<p>Nieuwe zaak op jouw naam met omschrijving: {OMSCHRIJVINGZAAK}</p>',
        '<p>Hoi!</p><p></p><p>Er is een {ZAAKTYPE}-zaak aan jou toegekend met zaaknummer {ZAAKNUMMER}.</p><p>Klik om naar de zaak afhandelcomponent te gaan: {ZAAKURL}</p>',
        'SIGNALERING_ZAAK_OP_NAAM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak verlopend streefdatum', '<p>We willen graag dat zaak {ZAAKNUMMER} op {STREEFDATUM} is afgehandeld</p>',
        '<p>Zaak {ZAAKNUMMER} over {OMSCHRIJVINGZAAK} staat op jouw naam. Deze zaak willen we uiterlijk op {STREEFDATUM} hebben afgehandeld omdat onze klant dat verdient.</p><p>Wij verzoeken je om de juiste acties te nemen om deze zaak tijdig af te handelen.</p>',
        'SIGNALERING_ZAAK_VERLOPEND_STREEFDATUM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering zaak verlopend fatale datum', '<p>Graag actie, op {FATALEDATUM} moet zaak {ZAAKNUMMER} afgehandeld zijn</p>',
        '<p>Zaak {ZAAKNUMMER} over {OMSCHRIJVINGZAAK} staat op jouw naam. Deze zaak moet uiterlijk op {FATALEDATUM} zijn afgehandeld.</p><p>Wij verzoeken je om de juiste acties te nemen om deze zaak tijdig af te handelen.</p>',
        'SIGNALERING_ZAAK_VERLOPEND_FATALE_DATUM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering taak op naam', '<p>Nieuwe taak op jouw naam voor een {ZAAKTYPE}-zaak</p>',
        '<p>Hoi!</p><p>Er is een taak voor een {ZAAKTYPE}-zaak op jouw naam gezet.</p><p>Klik om naar de taak te gaan {TAAKURL}</p>', 'SIGNALERING_TAAK_OP_NAAM');

INSERT INTO ${schema}.mail_template(id_mail_template, mail_template_naam, onderwerp, body, mail_template_enum)
VALUES (nextval('${schema}.sq_mail_template'), 'Signalering taak verlopen', '<p>Actie nodig, handel jouw taak voor zaak {ZAAKNUMMER} spoedig af</p>',
        '<p>Voor zaak {ZAAKNUMMER} over {OMSCHRIJVINGZAAK} staat een belangrijke een taak op jouw naam. De uiterlijke datum voor het afhandelen is verstreken.</p><p>We vragen je om jouw taak spoedig af te handelen. Klik om naar de taak te gaan {TAAKURL}</p>',
        'SIGNALERING_TAAK_VERLOPEN');
