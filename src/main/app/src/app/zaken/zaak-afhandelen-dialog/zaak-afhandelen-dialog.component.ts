/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
import {Validators} from '@angular/forms';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';
import {Zaak} from '../model/zaak';
import {UserEventListenerData} from '../../plan-items/model/user-event-listener-data';
import {UserEventListenerActie} from '../../plan-items/model/user-event-listener-actie-enum';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {MailService} from '../../mail/mail.service';
import {MailObject} from '../../mail/model/mailobject';
import {CustomValidators} from '../../shared/validators/customValidators';

@Component({
    templateUrl: 'zaak-afhandelen-dialog.component.html',
    styleUrls: ['./zaak-afhandelen-dialog.component.less']
})
export class ZaakAfhandelenDialogComponent implements OnInit {

    loading: boolean;
    toelichtingFormField: AbstractFormField;
    resultaatFormField: AbstractFormField;
    sendMailFormField: AbstractFormField;
    ontvangerFormField: AbstractFormField;

    onderwerp: string = 'Wij hebben uw verzoek afgehandeld (zaaknummer: {zaaknr}';
    body: string = 'Beste klant,\n' +
        '\n' +
        'Uw verzoek betreffende {zaaktype naam} met zaaknummer {zaaknr} is afgehandeld. Voor meer informatie gaat u naar Mijn loket.\n' +
        '\n' +
        'Met vriendelijke groet,\n' +
        '\n' +
        'Gemeente';

    constructor(public dialogRef: MatDialogRef<ZaakAfhandelenDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: { zaak: Zaak, planItem: PlanItem },
                private zaakafhandelParametersService: ZaakafhandelParametersService,
                private planItemsService: PlanItemsService,
                private mailService: MailService) {
    }

    ngOnInit(): void {
        this.onderwerp = this.onderwerp.replace('{zaaknr}', this.data.zaak.identificatie);
        this.body = this.body.replace('{zaaktype naam}', this.data.zaak.zaaktype.identificatie)
                        .replace('{zaaknr}', this.data.zaak.identificatie);

        this.resultaatFormField = new SelectFormFieldBuilder().id('resultaattype')
                                                              .label('resultaat')
                                                              .optionLabel('naam')
                                                              .options(this.zaakafhandelParametersService.listZaakResultaten(this.data.zaak.zaaktype.uuid))
                                                              .validators(Validators.required)
                                                              .build();
        this.toelichtingFormField = new InputFormFieldBuilder().id('toelichting')
                                                               .label('toelichting')
                                                               .maxlength(80)
                                                               .build();
        this.sendMailFormField = new CheckboxFormFieldBuilder().id('sendMail')
                                                               .label('sendMail')
                                                               .build();
        this.ontvangerFormField = new InputFormFieldBuilder().id('ontvanger')
                                                             .label('ontvanger')
                                                             .validators(Validators.required, CustomValidators.email)
                                                             .maxlength(200)
                                                             .build();
    }

    close(): void {
        this.dialogRef.close();
    }

    afhandelen(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;

        if (this.sendMailFormField.formControl.value) {
            const mailObject: MailObject = new MailObject();
            mailObject.createDocumentFromMail = true;
            mailObject.onderwerp = this.onderwerp;
            mailObject.body = this.body;
            mailObject.ontvanger = this.ontvangerFormField.formControl.value;
            this.mailService.sendMail(this.data.zaak.uuid, mailObject).subscribe(() => {});
        }

        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.ZaakAfhandelen, this.data.planItem.id, this.data.zaak.uuid);
        userEventListenerData.resultaattypeUuid = this.resultaatFormField.formControl.value.id;
        userEventListenerData.resultaatToelichting = this.toelichtingFormField.formControl.value;
        this.planItemsService.doUserEventListenerPlanItem(userEventListenerData).subscribe(() => {
            this.dialogRef.close(true);
        });
    }

    formFieldsInvalid(): boolean {
        return this.resultaatFormField.formControl.invalid || this.toelichtingFormField.formControl.invalid ||
            this.sendMailFormField.formControl.value ? this.ontvangerFormField.formControl.invalid : false;
    }
}
