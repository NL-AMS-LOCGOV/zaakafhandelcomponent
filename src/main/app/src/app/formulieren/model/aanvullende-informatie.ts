/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {DateFormFieldBuilder} from '../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {CustomValidators} from '../../shared/validators/customValidators';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {TaakDocumentUploadFieldBuilder} from '../../shared/material-form-builder/form-components/taak-document-upload/taak-document-upload-field-builder';
import {Observable, of} from 'rxjs';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {EnkelvoudigInformatieObjectZoekParameters} from '../../informatie-objecten/model/enkelvoudig-informatie-object-zoek-parameters';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../../taken/taken.service';
import * as moment from 'moment/moment';
import {RadioFormFieldBuilder} from '../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {HiddenFormFieldBuilder} from '../../shared/material-form-builder/form-components/hidden/hidden-form-field-builder';

export class AanvullendeInformatie extends AbstractFormulier {

    private bodyTemplate: string =
        'Beste klant,\n' +
        '\n' +
        'Voor het behandelen van de zaak hebben wij de volgende informatie van u nodig:\n' +
        '- omschrijf informatie 1\n' +
        '- omschrijf informatie 2\n' +
        '\n' +
        'We ontvangen de informatie graag uiterlijk op datum x. U kunt dit aanleveren door deze per e-mail te sturen naar mailadres Y. ' +
        'Vermeld op de informatie ook het zaaknummer van uw zaak.\n' +
        '\n' +
        'Met vriendelijke groet,\n' +
        '\n' +
        'Gemeente';

    fields = {
        EMAILADRES: 'emailadres',
        BODY: 'body',
        DATUMGEVRAAGD: 'datumGevraagd',
        DATUMGELEVERD: 'datumGeleverd',
        OPMERKINGEN: 'opmerkingen',
        BIJLAGE: 'bijlage',
        AANVULLENDE_INFORMATIE: 'aanvullendeInformatie'
    };

    taakinformatieMapping = {
        uitkomst: this.fields.AANVULLENDE_INFORMATIE,
        opmerking: this.fields.OPMERKINGEN,
        bijlage: this.fields.BIJLAGE
    };

    constructor(translate: TranslateService, public takenService: TakenService,
                public informatieObjectenService: InformatieObjectenService) {
        super(translate);
    }

    _initStartForm() {
        this.planItem.taakStuurGegevens.sendMail = true;
        this.planItem.taakStuurGegevens.onderwerp = 'Aanvullende informatie nodig voor zaak';
        const fields = this.fields;
        this.form.push(
            [new InputFormFieldBuilder().id(fields.EMAILADRES).label(fields.EMAILADRES)
                                        .validators(Validators.required, CustomValidators.emails).build()],
            [new TextareaFormFieldBuilder().id(fields.BODY).label(fields.BODY).value(this.bodyTemplate)
                                           .validators(Validators.required).maxlength(1000).build()],
            [new HiddenFormFieldBuilder().id(fields.DATUMGEVRAAGD).label(fields.DATUMGEVRAAGD).value(moment())
                                         .build()]
        );
    }

    _initBehandelForm() {
        this.doDisablePartialSave();
        const fields = this.fields;
        const aanvullendeInformatieDataElement = this.getDataElement(fields.AANVULLENDE_INFORMATIE);
        this.form.push(
            [new ReadonlyFormFieldBuilder().id(fields.EMAILADRES)
                                           .label(fields.EMAILADRES)
                                           .value(this.getDataElement(fields.EMAILADRES))
                                           .build()],
            [new ReadonlyFormFieldBuilder().id(fields.BODY)
                                           .label(fields.BODY)
                                           .value(this.getDataElement(fields.BODY))
                                           .build()],
            [new TextareaFormFieldBuilder().id(fields.OPMERKINGEN)
                                           .label(fields.OPMERKINGEN)
                                           .value(this.getDataElement(fields.OPMERKINGEN))
                                           .validators(Validators.required)
                                           .readonly(this.isAfgerond())
                                           .maxlength(1000)
                                           .build()],
            [
                new DateFormFieldBuilder().id(fields.DATUMGEVRAAGD)
                                          .label(fields.DATUMGEVRAAGD)
                                          .value(this.getDataElement(fields.DATUMGEVRAAGD))
                                          .readonly(true)
                                          .build(),
                new DateFormFieldBuilder().id(fields.DATUMGELEVERD)
                                          .label(fields.DATUMGELEVERD)
                                          .value(this.getDataElement(fields.DATUMGELEVERD))
                                          .readonly(this.isAfgerond())
                                          .build()
            ],
            [new RadioFormFieldBuilder().id(fields.AANVULLENDE_INFORMATIE)
                                        .label(fields.AANVULLENDE_INFORMATIE)
                                        .value(this.isAfgerond() && aanvullendeInformatieDataElement ?
                                            this.translate.instant(aanvullendeInformatieDataElement) : aanvullendeInformatieDataElement)
                                        .options(this.getAanvullendeInformatieOpties())
                                        .validators(Validators.required)
                                        .readonly(this.isAfgerond())
                                        .build()]
        );
        if (this.isAfgerond()) {
            this.form.push(
                [new DocumentenLijstFieldBuilder().id(fields.BIJLAGE)
                                                  .label(fields.BIJLAGE)
                                                  .documenten(this.getDocumenten$(fields.BIJLAGE))
                                                  .readonly(true)
                                                  .build()]);
        } else {
            this.form.push(
                [new TaakDocumentUploadFieldBuilder().id(fields.BIJLAGE)
                                                     .label(fields.BIJLAGE)
                                                     .defaultTitel(this.translate.instant('advies'))
                                                     .uploadURL(this.takenService.getUploadURL(this.taak.id, fields.BIJLAGE))
                                                     .zaakUUID(this.taak.zaakUUID)
                                                     .readonly(this.isAfgerond())
                                                     .build()]);
        }
    }

    getDocumenten$(field: string): Observable<EnkelvoudigInformatieobject[]> {
        const dataElement = this.getDataElement(field);
        if (dataElement) {
            const zoekParameters = new EnkelvoudigInformatieObjectZoekParameters();
            zoekParameters.UUIDs = dataElement.split(';');
            return this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekParameters);
        } else {
            return of([]);
        }
    }

    getStartTitel(): string {
        return this.translate.instant('title.taak.aanvullende-informatie.starten');
    }

    getBehandelTitel(): string {
        if (this.isAfgerond()) {
            return this.translate.instant('title.taak.aanvullende-informatie.raadplegen');
        } else {
            return this.translate.instant('title.taak.aanvullende-informatie.behandelen');
        }
    }

    getAanvullendeInformatieOpties(): Observable<string[]> {
        return of([
            'aanvullende-informatie.geleverd-akkoord',
            'aanvullende-informatie.geleverd-niet-akkoord',
            'aanvullende-informatie.niet-geleverd'
        ]);
    }
}

