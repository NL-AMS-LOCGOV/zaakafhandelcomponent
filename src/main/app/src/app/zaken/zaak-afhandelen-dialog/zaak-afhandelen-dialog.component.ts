/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AbstractFormField} from '../../shared/material-form-builder/model/abstract-form-field';
import {SelectFormFieldBuilder} from '../../shared/material-form-builder/form-components/select/select-form-field-builder';
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
import {ZakenService} from '../zaken.service';
import {ReadonlyFormField} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {SelectFormField} from '../../shared/material-form-builder/form-components/select/select-form-field';
import {takeUntil} from 'rxjs/operators';
import {Resultaattype} from '../model/resultaattype';
import {Subject} from 'rxjs';
import {ZaakafhandelParametersService} from '../../admin/zaakafhandel-parameters.service';
import {ZaakStatusmailOptie} from '../model/zaak-statusmail-optie';

@Component({
    templateUrl: 'zaak-afhandelen-dialog.component.html',
    styleUrls: ['./zaak-afhandelen-dialog.component.less']
})
export class ZaakAfhandelenDialogComponent implements OnInit, OnDestroy {

    loading: boolean;
    toelichtingFormField: AbstractFormField;
    resultaatFormField: SelectFormField | ReadonlyFormField;
    besluitFormField: ReadonlyFormField;
    sendMailFormField: AbstractFormField;
    ontvangerFormField: AbstractFormField;
    private ngDestroy = new Subject<void>();
    besluitVastleggen = false;

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
                private zakenService: ZakenService,
                private planItemsService: PlanItemsService,
                private mailService: MailService,
                private zaakafhandelParametersService: ZaakafhandelParametersService) {
    }

    ngOnInit(): void {
        this.onderwerp = this.onderwerp.replace('{zaaknr}', this.data.zaak.identificatie);
        this.body = this.body.replace('{zaaktype naam}', this.data.zaak.zaaktype.identificatie)
                        .replace('{zaaknr}', this.data.zaak.identificatie);

        if (this.data.zaak.besluit) {
            this.resultaatFormField = new ReadonlyFormFieldBuilder(this.data.zaak.resultaat.resultaattype.naam).id('resultaattype')
                                                                    .label('resultaat')
                                                                    .build();

            this.besluitFormField = new ReadonlyFormFieldBuilder(this.data.zaak.besluit.besluittype.naam).id('besluit')
                                                                  .label('besluit')
                                                                  .build();
        } else {
            this.resultaatFormField = new SelectFormFieldBuilder().id('resultaattype')
                                                                  .label('resultaat')
                                                                  .optionLabel('naam')
                                                                  .options(this.zakenService.listResultaattypes(this.data.zaak.zaaktype.uuid))
                                                                  .validators(Validators.required)
                                                                  .build();
        }
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
        this.sendMailFormField.formControl.disable();

        if (!this.data.zaak.besluit) {
            this.resultaatFormField.formControl.valueChanges.pipe(takeUntil(this.ngDestroy)).subscribe(value => {
                this.besluitVastleggen = (value as Resultaattype).besluitVerplicht;

                if (this.besluitVastleggen) {
                    this.toelichtingFormField.formControl.disable();
                    this.sendMailFormField.formControl.disable();
                } else {
                    this.toelichtingFormField.formControl.enable();
                    this.sendMailFormField.formControl.enable();
                }
            });
        }
        this.zaakafhandelParametersService.readZaakafhandelparameters(this.data.zaak.zaaktype.uuid).subscribe(zaakafhandelParameters => {
            this.sendMailFormField.formControl.setValue(zaakafhandelParameters.afrondenMail === ZaakStatusmailOptie.BESCHIKBAAR_AAN);
            if(zaakafhandelParameters.afrondenMail !== ZaakStatusmailOptie.NIET_BESCHIKBAAR) {
                this.sendMailFormField.formControl.enable();
            }
        });
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
        this.planItemsService.doUserEventListenerPlanItem(userEventListenerData).subscribe({
            next: () => this.dialogRef.close(true),
            error: () => this.dialogRef.close(false)
        });
    }

    formFieldsInvalid(): boolean {
        return this.resultaatFormField.formControl.invalid || this.toelichtingFormField.formControl.invalid ||
            (this.sendMailFormField.formControl.value ? this.ontvangerFormField.formControl.invalid : false);
    }

    ngOnDestroy(): void {
        this.ngDestroy.next();
        this.ngDestroy.complete();
    }

    openBesluitVastleggen(): void {
        this.dialogRef.close('openBesluitVastleggen');

    }
}
