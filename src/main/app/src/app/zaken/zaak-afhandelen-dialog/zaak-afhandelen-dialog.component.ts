/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Zaak} from '../model/zaak';
import {UserEventListenerData} from '../../plan-items/model/user-event-listener-data';
import {UserEventListenerActie} from '../../plan-items/model/user-event-listener-actie-enum';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {MailService} from '../../mail/mail.service';
import {MailGegevens} from '../../mail/model/mail-gegevens';
import {CustomValidators} from '../../shared/validators/customValidators';
import {ZakenService} from '../zaken.service';
import {takeUntil} from 'rxjs/operators';
import {Resultaattype} from '../model/resultaattype';
import {Observable, Subject} from 'rxjs';
import {ZaakStatusmailOptie} from '../model/zaak-statusmail-optie';
import {MailtemplateService} from '../../mailtemplate/mailtemplate.service';
import {Mail} from '../../admin/model/mail';
import {Mailtemplate} from '../../admin/model/mailtemplate';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: 'zaak-afhandelen-dialog.component.html',
    styleUrls: ['./zaak-afhandelen-dialog.component.less']
})
export class ZaakAfhandelenDialogComponent implements OnInit, OnDestroy {

    loading = false;
    mailBeschikbaar = false;
    sendMailDefault = false;
    formGroup: FormGroup;
    besluitVastleggen = false;
    mailtemplate: Mailtemplate;
    zaak: Zaak;
    planItem: PlanItem;
    resultaattypes: Observable<Resultaattype[]>;
    private ngDestroy = new Subject<void>();

    constructor(public dialogRef: MatDialogRef<ZaakAfhandelenDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: { zaak: Zaak, planItem: PlanItem },
                private formBuilder: FormBuilder,
                private translateService: TranslateService,
                private zakenService: ZakenService,
                private planItemsService: PlanItemsService,
                private mailService: MailService,
                private mailtemplateService: MailtemplateService) {
        this.zaak = data.zaak;
        this.planItem = data.planItem;
        this.resultaattypes = this.zakenService.listResultaattypes(this.data.zaak.zaaktype.uuid);
        this.mailtemplateService.findMailtemplate(Mail.ZAAK_AFGEHANDELD, this.data.zaak.uuid).subscribe(mailtemplate => {
            this.mailtemplate = mailtemplate;
        });
    }

    ngOnInit(): void {
        const zap = this.zaak.zaaktype.zaakafhandelparameters;
        this.mailBeschikbaar = zap.afrondenMail !== ZaakStatusmailOptie.NIET_BESCHIKBAAR;
        this.sendMailDefault = zap.afrondenMail === ZaakStatusmailOptie.BESCHIKBAAR_AAN;

        this.formGroup = this.formBuilder.group({
            resultaattype: [null, this.zaak.resultaat ? null : [Validators.required]],
            toelichting: '',
            sendMail: this.sendMailDefault,
            ontvanger: ['', (this.sendMailDefault ? [Validators.required, CustomValidators.email] : null)]
        });

        this.formGroup.get('sendMail').valueChanges.pipe(takeUntil(this.ngDestroy)).subscribe(value => {
            this.formGroup.get('ontvanger').setValidators(value ? [Validators.required, CustomValidators.email] : null);
            this.formGroup.get('ontvanger').updateValueAndValidity();
        });
        this.formGroup.get('resultaattype').valueChanges.pipe(takeUntil(this.ngDestroy)).subscribe((value: Resultaattype) => {
            this.besluitVastleggen = value.besluitVerplicht;
            if (this.besluitVastleggen) {
                this.formGroup.get('toelichting').disable();
                this.formGroup.get('sendMail').disable();
                this.formGroup.get('ontvanger').disable();
            } else {
                this.formGroup.get('toelichting').enable();
                this.formGroup.get('sendMail').enable();
                this.formGroup.get('ontvanger').enable();
            }
        });
    }

    close(): void {
        this.dialogRef.close();
    }

    afhandelen(): void {
        this.dialogRef.disableClose = true;
        this.loading = true;
        const values = this.formGroup.value;

        if (values.sendMail && this.mailtemplate) {
            const mailGegevens: MailGegevens = new MailGegevens();
            mailGegevens.createDocumentFromMail = true;
            mailGegevens.onderwerp = this.mailtemplate.onderwerp;
            mailGegevens.body = this.mailtemplate.body;
            mailGegevens.ontvanger = values.ontvanger;
            this.mailService.sendMail(this.zaak.uuid, mailGegevens).subscribe(() => {});
        }

        const userEventListenerData = new UserEventListenerData(UserEventListenerActie.ZaakAfhandelen, this.planItem.id, this.zaak.uuid);
        userEventListenerData.resultaattypeUuid = this.zaak.resultaat ? this.zaak.resultaat.resultaattype.id : values.resultaattype.id;
        userEventListenerData.resultaatToelichting = values.toelichting;
        this.planItemsService.doUserEventListenerPlanItem(userEventListenerData).subscribe({
            next: () => this.dialogRef.close(true),
            error: () => this.dialogRef.close(false)
        });
    }

    getError(fc: AbstractControl, label: string) {
        return CustomValidators.getErrorMessage(fc, label, this.translateService);
    }

    ngOnDestroy(): void {
        this.ngDestroy.next();
        this.ngDestroy.complete();
    }

    openBesluitVastleggen(): void {
        this.dialogRef.close('openBesluitVastleggen');
    }
}
