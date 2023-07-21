/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Inject, OnDestroy, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { Zaak } from "../model/zaak";
import { UserEventListenerData } from "../../plan-items/model/user-event-listener-data";
import { UserEventListenerActie } from "../../plan-items/model/user-event-listener-actie-enum";
import { PlanItemsService } from "../../plan-items/plan-items.service";
import { PlanItem } from "../../plan-items/model/plan-item";
import { MailService } from "../../mail/mail.service";
import { MailGegevens } from "../../mail/model/mail-gegevens";
import { ZaakStatusmailOptie } from "../model/zaak-statusmail-optie";
import { MailtemplateService } from "../../mailtemplate/mailtemplate.service";
import { Mail } from "../../admin/model/mail";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators,
} from "@angular/forms";
import { CustomValidators } from "../../shared/validators/customValidators";
import { TranslateService } from "@ngx-translate/core";
import { Mailtemplate } from "../../admin/model/mailtemplate";
import { Observable, Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { KlantenService } from "../../klanten/klanten.service";
import { ActionIcon } from "../../shared/edit/action-icon";
import { ZaakAfzender } from "../../admin/model/zaakafzender";
import { ZakenService } from "../zaken.service";

@Component({
  templateUrl: "intake-afronden-dialog.component.html",
  styleUrls: ["./intake-afronden-dialog.component.less"],
})
export class IntakeAfrondenDialogComponent implements OnInit, OnDestroy {
  loading = false;
  zaakOntvankelijkMail: Mailtemplate;
  zaakNietOntvankelijkMail: Mailtemplate;
  mailBeschikbaar = false;
  sendMailDefault = false;
  initiatorEmail: string;
  initiatorToevoegenIcon: ActionIcon = new ActionIcon(
    "person",
    "actie.initiator.email.toevoegen",
    new Subject<void>(),
  );
  formGroup: FormGroup;
  afzenders: Observable<ZaakAfzender[]>;
  private ngDestroy = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<IntakeAfrondenDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { zaak: Zaak; planItem: PlanItem },
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private planItemsService: PlanItemsService,
    private mailService: MailService,
    private mailtemplateService: MailtemplateService,
    private klantenService: KlantenService,
    private zakenService: ZakenService,
  ) {
    this.afzenders = this.zakenService.listAfzendersVoorZaak(
      this.data.zaak.uuid,
    );
    this.mailtemplateService
      .findMailtemplate(Mail.ZAAK_ONTVANKELIJK, this.data.zaak.uuid)
      .subscribe((mailtemplate) => {
        this.zaakOntvankelijkMail = mailtemplate;
      });
    this.mailtemplateService
      .findMailtemplate(Mail.ZAAK_NIET_ONTVANKELIJK, this.data.zaak.uuid)
      .subscribe((mailtemplate) => {
        this.zaakNietOntvankelijkMail = mailtemplate;
      });
  }

  ngOnInit(): void {
    const zap = this.data.zaak.zaaktype.zaakafhandelparameters;
    this.mailBeschikbaar =
      zap.intakeMail !== ZaakStatusmailOptie.NIET_BESCHIKBAAR;
    this.sendMailDefault =
      zap.intakeMail === ZaakStatusmailOptie.BESCHIKBAAR_AAN;

    if (
      this.data.zaak.initiatorIdentificatieType &&
      this.data.zaak.initiatorIdentificatie
    ) {
      this.klantenService
        .ophalenContactGegevens(
          this.data.zaak.initiatorIdentificatieType,
          this.data.zaak.initiatorIdentificatie,
        )
        .subscribe((gegevens) => {
          if (gegevens.emailadres) {
            this.initiatorEmail = gegevens.emailadres;
          }
        });
    }

    this.formGroup = this.formBuilder.group({
      ontvankelijk: [null, [Validators.required]],
      reden: "",
      sendMail: this.sendMailDefault,
      verzender: [null, this.sendMailDefault ? [Validators.required] : null],
      ontvanger: [
        "",
        this.sendMailDefault
          ? [Validators.required, CustomValidators.email]
          : null,
      ],
    });
    this.zakenService
      .readDefaultAfzenderVoorZaak(this.data.zaak.uuid)
      .subscribe((afzender) => {
        this.formGroup.get("verzender").setValue(afzender);
      });
    this.formGroup
      .get("ontvankelijk")
      .valueChanges.pipe(takeUntil(this.ngDestroy))
      .subscribe((value) => {
        this.formGroup
          .get("reden")
          .setValidators(value ? null : Validators.required);
        this.formGroup.get("reden").updateValueAndValidity();
      });
    this.formGroup
      .get("sendMail")
      .valueChanges.pipe(takeUntil(this.ngDestroy))
      .subscribe((value) => {
        this.formGroup
          .get("verzender")
          .setValidators(value ? [Validators.required] : null);
        this.formGroup.get("verzender").updateValueAndValidity();
        this.formGroup
          .get("ontvanger")
          .setValidators(
            value ? [Validators.required, CustomValidators.email] : null,
          );
        this.formGroup.get("ontvanger").updateValueAndValidity();
      });
  }

  getError(fc: AbstractControl, label: string) {
    return CustomValidators.getErrorMessage(fc, label, this.translateService);
  }

  setInitatorEmail() {
    this.formGroup.get("ontvanger").setValue(this.initiatorEmail);
  }

  close(): void {
    this.dialogRef.close();
  }

  afronden(): void {
    this.dialogRef.disableClose = true;
    this.loading = true;
    const values = this.formGroup.value;
    const userEventListenerData = new UserEventListenerData(
      UserEventListenerActie.IntakeAfronden,
      this.data.planItem.id,
      this.data.zaak.uuid,
    );
    userEventListenerData.zaakOntvankelijk = values.ontvankelijk;
    userEventListenerData.resultaatToelichting = values.reden;
    this.planItemsService
      .doUserEventListenerPlanItem(userEventListenerData)
      .subscribe({
        next: () => {
          const mailtemplate = values.ontvankelijk
            ? this.zaakOntvankelijkMail
            : this.zaakNietOntvankelijkMail;
          if (values.sendMail && mailtemplate) {
            const mailObject: MailGegevens = new MailGegevens();
            mailObject.verzender = values.verzender.mail;
            mailObject.replyTo = values.verzender.replyTo;
            mailObject.ontvanger = values.ontvanger;
            mailObject.onderwerp = mailtemplate.onderwerp;
            mailObject.body = mailtemplate.body;
            mailObject.createDocumentFromMail = true;
            this.mailService
              .sendMail(this.data.zaak.uuid, mailObject)
              .subscribe(() => {});
          }
          this.dialogRef.close(true);
        },
        error: () => this.dialogRef.close(false),
      });
  }

  ngOnDestroy(): void {
    this.ngDestroy.next();
    this.ngDestroy.complete();
  }

  compareObject(object1: any, object2: any): boolean {
    if (typeof object1 === "string") {
      return object1 === object2;
    }
    if (object1 && object2) {
      if (object1.hasOwnProperty("key")) {
        return object1.key === object2.key;
      } else if (object1.hasOwnProperty("id")) {
        return object1.id === object2.id;
      } else if (object1.hasOwnProperty("naam")) {
        return object1.naam === object2.naam;
      } else if (object1.hasOwnProperty("name")) {
        return object1.name === object2.name;
      }
      return object1 === object2;
    }
    return false;
  }
}
