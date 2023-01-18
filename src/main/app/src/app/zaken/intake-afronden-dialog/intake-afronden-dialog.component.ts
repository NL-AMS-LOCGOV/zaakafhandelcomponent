/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Zaak} from '../model/zaak';
import {UserEventListenerData} from '../../plan-items/model/user-event-listener-data';
import {UserEventListenerActie} from '../../plan-items/model/user-event-listener-actie-enum';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {MailService} from '../../mail/mail.service';
import {MailGegevens} from '../../mail/model/mail-gegevens';
import {ZaakStatusmailOptie} from '../model/zaak-statusmail-optie';
import {MailtemplateService} from '../../mailtemplate/mailtemplate.service';
import {Mail} from '../../admin/model/mail';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CustomValidators} from '../../shared/validators/customValidators';
import {TranslateService} from '@ngx-translate/core';
import {KlantenService} from '../../klanten/klanten.service';
import {ActionIcon} from '../../shared/edit/action-icon';
import {Observable, Subject} from 'rxjs';
import {Klant} from '../../klanten/model/klanten/klant';

@Component({
    templateUrl: 'intake-afronden-dialog.component.html',
    styleUrls: ['./intake-afronden-dialog.component.less']
})
export class IntakeAfrondenDialogComponent implements OnInit {

    loading = false;
    mailBeschikbaar = false;
    sendMailDefault = false;
    initiatorEmail: string;
    initiatorToevoegenIcon: ActionIcon = new ActionIcon('person', 'actie.initiator.email.toevoegen',
        new Subject<void>());
    formGroup: FormGroup;

    constructor(public dialogRef: MatDialogRef<IntakeAfrondenDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: { zaak: Zaak, planItem: PlanItem },
                private formBuilder: FormBuilder,
                private translateService: TranslateService,
                private planItemsService: PlanItemsService,
                private mailService: MailService,
                private mailtemplateService: MailtemplateService,
                private klantenService: KlantenService) {
    }

    ngOnInit(): void {
        const zap = this.data.zaak.zaaktype.zaakafhandelparameters;
        this.mailBeschikbaar = zap.intakeMail !== ZaakStatusmailOptie.NIET_BESCHIKBAAR;
        this.sendMailDefault = zap.intakeMail === ZaakStatusmailOptie.BESCHIKBAAR_AAN;


        if (this.data.zaak.initiatorIdentificatieType && this.data.zaak.initiatorIdentificatie) {
            this.klantenService.ophalenContactGegevens(this.data.zaak.initiatorIdentificatieType,
                this.data.zaak.initiatorIdentificatie).subscribe(gegevens => {
                if (gegevens.emailadres) {
                    this.initiatorEmail = gegevens.emailadres;
                }
            });
        }

        this.formGroup = this.formBuilder.group({
            ontvankelijk: [null, [Validators.required]],
            reden: '',
            sendMail: this.sendMailDefault,
            ontvanger: ['', (this.sendMailDefault ? [Validators.required, CustomValidators.email] : null)]
        });
        this.formGroup.get('ontvankelijk').valueChanges.subscribe(value => {
            this.formGroup.get('reden').setValidators(value ? null : Validators.required);
            this.formGroup.get('reden').updateValueAndValidity();
        });
        this.formGroup.get('sendMail').valueChanges.subscribe(value => {
            this.formGroup.get('ontvanger').setValidators(value ? [Validators.required, CustomValidators.email] : null);
            this.formGroup.get('ontvanger').updateValueAndValidity();
        });
    }

    getError(fc: AbstractControl, label: string) {
        return CustomValidators.getErrorMessage(fc, label, this.translateService);
    }

    setInitatorEmail() {
        this.formGroup.get('ontvanger').setValue(this.initiatorEmail);
    }

    close(): void {
        this.dialogRef.close();
    }

    afronden(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        const values = this.formGroup.value;

        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.IntakeAfronden, this.data.planItem.id, this.data.zaak.uuid);
        userEventListenerData.zaakOntvankelijk = values.ontvankelijk;
        userEventListenerData.resultaatToelichting = values.reden;
        if (values.sendMail) {
            this.mailtemplateService.findMailtemplate(userEventListenerData.zaakOntvankelijk ?
                Mail.ZAAK_ONTVANKELIJK : Mail.ZAAK_NIET_ONTVANKELIJK, this.data.zaak.uuid)
                .subscribe(mailtemplate => {
                    if (mailtemplate) {
                        const mailObject: MailGegevens = new MailGegevens();
                        mailObject.createDocumentFromMail = true;
                        mailObject.onderwerp = mailtemplate.onderwerp;
                        mailObject.body = mailtemplate.body;
                        mailObject.ontvanger = values.ontvanger;
                        this.mailService.sendMail(this.data.zaak.uuid, mailObject).subscribe(() => {});
                    }
                });
        }
        this.planItemsService.doUserEventListenerPlanItem(userEventListenerData).subscribe({
            next: () => this.dialogRef.close(true),
            error: () => this.dialogRef.close(false)
        });
    }
}
