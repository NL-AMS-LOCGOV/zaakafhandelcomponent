/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {Validators} from '@angular/forms';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {Zaak} from '../model/zaak';
import {UserEventListenerData} from '../../plan-items/model/user-event-listener-data';
import {UserEventListenerActie} from '../../plan-items/model/user-event-listener-actie-enum';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {CheckboxFormFieldBuilder} from '../../shared/material-form-builder/form-components/checkbox/checkbox-form-field-builder';
import {MailService} from '../../mail/mail.service';
import {MailObject} from '../../mail/model/mailobject';
import {CustomValidators} from '../../shared/validators/customValidators';
import {of, Subject} from 'rxjs';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';
import {ZaakStatusmailOptie} from '../model/zaak-statusmail-optie';
import {RadioFormFieldBuilder} from '../../shared/material-form-builder/form-components/radio/radio-form-field-builder';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';

@Component({
    templateUrl: 'intake-afronden-dialog.component.html',
    styleUrls: ['./intake-afronden-dialog.component.less']
})
export class IntakeAfrondenDialogComponent implements OnInit, OnDestroy {

    loading: boolean;
    radioFormField: AbstractFormField;
    redenFormField: AbstractFormField;
    sendMailFormField: AbstractFormField;
    ontvangerFormField: AbstractFormField;
    private ngDestroy = new Subject<void>();

    onderwerp: string = 'Wij hebben uw verzoek in behandeling genomen (zaaknummer: {zaaknr}';
    body: string = 'Beste klant,\n' +
        '\n' +
        'Uw verzoek over {zaaktype naam} met zaaknummer {zaaknr} is in behandeling genomen. Voor meer informatie gaat u naar Mijn loket.\n' +
        '\n' +
        'Met vriendelijke groet,\n' +
        '\n' +
        'Gemeente';

    constructor(public dialogRef: MatDialogRef<IntakeAfrondenDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: { zaak: Zaak, planItem: PlanItem },
                private planItemsService: PlanItemsService,
                private mailService: MailService,
                private zaakafhandelParametersService: ZaakafhandelParametersService) {
    }

    ngOnInit(): void {
        this.onderwerp = this.onderwerp.replace('{zaaknr}', this.data.zaak.identificatie);
        this.body = this.body.replace('{zaaktype naam}', this.data.zaak.zaaktype.identificatie)
                        .replace('{zaaknr}', this.data.zaak.identificatie);

        this.radioFormField = new RadioFormFieldBuilder().id('ontvankelijk')
                                                 .label('zaakOntvankelijk')
                                                 .optionLabel('key')
                                                 .options(of([
                                                     {value: true, key: 'actie.ja'},
                                                     {value: false, key: 'actie.nee'}]))
                                                 .validators(Validators.required)
                                                 .build();
        this.redenFormField = new TextareaFormFieldBuilder().id('reden')
                                                    .label('redenNietOntvankelijk')
                                                    .validators(Validators.required)
                                                    .maxlength(100)
                                                    .build();
        this.sendMailFormField = new CheckboxFormFieldBuilder().id('sendMail')
                                                       .label('sendMail')
                                                       .build();
        this.ontvangerFormField = new InputFormFieldBuilder().id('ontvanger')
                                                              .label('ontvanger')
                                                              .validators(Validators.required, CustomValidators.email)
                                                              .maxlength(200)
                                                              .build();

        this.redenFormField.formControl.disable();

        this.radioFormField.formControl.valueChanges.subscribe(value => {
            if (value) {
                if (value.value) {
                    this.redenFormField.formControl.disable();
                } else {
                    this.redenFormField.formControl.enable();
                }
            }
        });

        this.sendMailFormField.formControl.valueChanges.subscribe(value => {
            if (!value) {
                this.ontvangerFormField.formControl.disable();
            } else {
                this.ontvangerFormField.formControl.enable();
            }
        });

        this.zaakafhandelParametersService.readZaakafhandelparameters(this.data.zaak.zaaktype.uuid).subscribe(zaakafhandelParameters => {
            if (zaakafhandelParameters.intakeMail !== ZaakStatusmailOptie.NIET_BESCHIKBAAR) {
                this.sendMailFormField.formControl.enable();
            }
            this.sendMailFormField.formControl.setValue(zaakafhandelParameters.intakeMail === ZaakStatusmailOptie.BESCHIKBAAR_AAN);
        });
    }

    close(): void {
        this.dialogRef.close();
    }

    afronden(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;

        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.IntakeAfronden, this.data.planItem.id, this.data.zaak.uuid);
        userEventListenerData.zaakOntvankelijk = this.radioFormField.formControl.value.value;
        userEventListenerData.resultaatToelichting = this.redenFormField.formControl.value;

        if (this.sendMailFormField.formControl.value) {
            const mailObject: MailObject = new MailObject();
            mailObject.createDocumentFromMail = true;
            mailObject.onderwerp = this.onderwerp;
            mailObject.body = this.body;
            mailObject.ontvanger = this.ontvangerFormField.formControl.value;
            this.mailService.sendMail(this.data.zaak.uuid, mailObject).subscribe(() => {});
        }

        this.planItemsService.doUserEventListenerPlanItem(userEventListenerData).subscribe({
            next: () => this.dialogRef.close(true),
            error: () => this.dialogRef.close(false)
        });
    }

    formFieldsInvalid(): boolean {
        return this.radioFormField.formControl.invalid || this.redenFormField.formControl.invalid ||
            (this.sendMailFormField.formControl.value ? this.ontvangerFormField.formControl.invalid : false);
    }

    ngOnDestroy(): void {
        this.ngDestroy.next();
        this.ngDestroy.complete();
    }
}
