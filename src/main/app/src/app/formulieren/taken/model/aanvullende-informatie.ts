/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {DateFormFieldBuilder} from '../../../shared/material-form-builder/form-components/date/date-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InputFormFieldBuilder} from '../../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {CustomValidators} from '../../../shared/validators/customValidators';
import {Observable, of} from 'rxjs';
import {InformatieObjectenService} from '../../../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../../../taken/taken.service';
import * as moment from 'moment/moment';
import {RadioFormFieldBuilder} from '../../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {HiddenFormFieldBuilder} from '../../../shared/material-form-builder/form-components/hidden/hidden-form-field-builder';
import {DocumentenLijstFieldBuilder} from '../../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {InformatieobjectZoekParameters} from '../../../informatie-objecten/model/informatieobject-zoek-parameters';
import {HtmlEditorFormFieldBuilder} from '../../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder';
import {MailtemplateService} from '../../../mailtemplate/mailtemplate.service';
import {Mailtemplate} from '../../../admin/model/mailtemplate';
import {Mail} from '../../../admin/model/mail';
import {AbstractTaakFormulier} from '../abstract-taak-formulier';

export class AanvullendeInformatie extends AbstractTaakFormulier {

    fields = {
        EMAILADRES: 'emailadres',
        BODY: 'body',
        DATUMGEVRAAGD: 'datumGevraagd',
        DATUMGELEVERD: 'datumGeleverd',
        OPMERKINGEN: 'opmerkingen',
        AANVULLENDE_INFORMATIE: 'aanvullendeInformatie',
        BIJLAGEN: 'bijlagen'
    };

    taakinformatieMapping = {
        uitkomst: this.fields.AANVULLENDE_INFORMATIE,
        opmerking: this.fields.OPMERKINGEN
    };

    mailtemplate$: Observable<Mailtemplate>;

    constructor(translate: TranslateService, public takenService: TakenService,
                public informatieObjectenService: InformatieObjectenService,
                private mailtemplateService: MailtemplateService) {
        super(translate, informatieObjectenService);
    }

    _initStartForm() {
        this.humanTaskData.taakStuurGegevens.sendMail = true;
        this.mailtemplate$ = this.mailtemplateService.findMailtemplate(Mail.PROCES_AANVULLENDE_INFORMATIE, this.zaakUuid);
        this.humanTaskData.taakStuurGegevens.mail = Mail.PROCES_AANVULLENDE_INFORMATIE;
        const zoekparameters = new InformatieobjectZoekParameters();
        zoekparameters.zaakUUID = this.zaakUuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);
        const fields = this.fields;
        this.form.push(
            [new InputFormFieldBuilder()
            .id(fields.EMAILADRES)
            .label(fields.EMAILADRES)
            .validators(Validators.required, CustomValidators.emails)
            .build()],
            [new HtmlEditorFormFieldBuilder()
            .id(fields.BODY)
            .label(fields.BODY)
            .validators(Validators.required)
            .mailtemplateBody(this.mailtemplate$)
            .maxlength(1000)
            .build()],
            [new HiddenFormFieldBuilder(moment())
            .id(fields.DATUMGEVRAAGD)
            .label(fields.DATUMGEVRAAGD)
            .build()],
            [new DocumentenLijstFieldBuilder()
            .id(fields.BIJLAGEN)
            .label(fields.BIJLAGEN)
            .documenten(documenten).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        const aanvullendeInformatieDataElement = this.getDataElement(fields.AANVULLENDE_INFORMATIE);
        this.form.push(
            [new ReadonlyFormFieldBuilder(this.getDataElement(fields.EMAILADRES)).id(fields.EMAILADRES)
                                                                                 .label(fields.EMAILADRES)
                                                                                 .build()],
            [new ReadonlyFormFieldBuilder(this.getDataElement(fields.BODY)).id(fields.BODY)
                                                                           .label(fields.BODY)
                                                                           .build()],
            [new TextareaFormFieldBuilder(this.getDataElement(fields.OPMERKINGEN)).id(fields.OPMERKINGEN)
                                                                                  .label(fields.OPMERKINGEN)
                                                                                  .validators(Validators.required)
                                                                                  .readonly(this.readonly)
                                                                                  .maxlength(1000)
                                                                                  .build()],
            [
                new DateFormFieldBuilder(this.getDataElement(fields.DATUMGEVRAAGD)).id(fields.DATUMGEVRAAGD)
                                                                                   .label(fields.DATUMGEVRAAGD)
                                                                                   .readonly(true)
                                                                                   .build(),
                new DateFormFieldBuilder(this.getDataElement(fields.DATUMGELEVERD)).id(fields.DATUMGELEVERD)
                                                                                   .label(fields.DATUMGELEVERD)
                                                                                   .readonly(this.readonly)
                                                                                   .build()
            ],
            [new RadioFormFieldBuilder(this.readonly && aanvullendeInformatieDataElement ?
                this.translate.instant(aanvullendeInformatieDataElement) : aanvullendeInformatieDataElement).id(fields.AANVULLENDE_INFORMATIE)
                                                                                                            .label(fields.AANVULLENDE_INFORMATIE)
                                                                                                            .options(this.getAanvullendeInformatieOpties())
                                                                                                            .validators(Validators.required)
                                                                                                            .readonly(this.readonly)
                                                                                                            .build()]
        );
    }

    getStartTitel(): string {
        return this.translate.instant('title.taak.aanvullende-informatie.starten');
    }

    getBehandelTitel(): string {
        if (this.readonly) {
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

