/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { Klant } from "../../model/klanten/klant";
import { SelectFormFieldBuilder } from "../../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { SelectFormField } from "../../../shared/material-form-builder/form-components/select/select-form-field";
import { KlantenService } from "../../klanten.service";
import { KlantGegevens } from "../../model/klanten/klant-gegevens";
import { InputFormField } from "../../../shared/material-form-builder/form-components/input/input-form-field";
import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { MatDrawer } from "@angular/material/sidenav";

@Component({
  selector: "zac-klant-koppel",
  templateUrl: "./klant-koppel.component.html",
  styleUrls: ["./klant-koppel.component.less"],
})
export class KlantKoppelComponent implements OnInit {
  @Input() initiator = false;
  @Input() zaaktypeUUID: string;
  @Input() sideNav: MatDrawer;
  @Output() klantGegevens = new EventEmitter<KlantGegevens>();
  betrokkeneRoltype: SelectFormField;
  betrokkeneToelichting: InputFormField;
  formGroup: FormGroup;

  constructor(
    private klantenService: KlantenService,
    private formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (!this.initiator) {
      this.betrokkeneRoltype = new SelectFormFieldBuilder()
        .id("betrokkeneType")
        .label("betrokkeneRoltype")
        .optionLabel("naam")
        .options(this.klantenService.listBetrokkeneRoltypen(this.zaaktypeUUID))
        .validators(Validators.required)
        .build();
      this.betrokkeneToelichting = new InputFormFieldBuilder()
        .id("betrokkenToelichting")
        .label("toelichting")
        .validators(Validators.required)
        .maxlength(75)
        .build();
      this.formGroup = this.formBuilder.group({
        rol: this.betrokkeneRoltype.formControl,
        toelichting: this.betrokkeneToelichting.formControl,
      });
    }
  }

  klantGeselecteerd(klant: Klant): void {
    if (this.initiator) {
      this.klantGegevens.emit(new KlantGegevens(klant));
    } else {
      if (this.formGroup.invalid) {
        this.betrokkeneRoltype.formControl.markAsTouched();
        this.betrokkeneToelichting.formControl.markAsTouched();
      } else {
        const klantGegevens: KlantGegevens = new KlantGegevens(klant);
        klantGegevens.betrokkeneRoltype =
          this.betrokkeneRoltype.formControl.value;
        klantGegevens.betrokkeneToelichting =
          this.betrokkeneToelichting.formControl.value;
        this.klantGegevens.emit(klantGegevens);
      }
    }
  }
}
