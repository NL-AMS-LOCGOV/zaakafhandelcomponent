/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { MatTableDataSource } from "@angular/material/table";
import { CustomValidators } from "../../../shared/validators/customValidators";
import { Bedrijf } from "../../model/bedrijven/bedrijf";
import { SelectFormFieldBuilder } from "../../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { ListBedrijvenParameters } from "../../model/bedrijven/list-bedrijven-parameters";
import { KlantenService } from "../../klanten.service";
import { AbstractFormControlField } from "../../../shared/material-form-builder/model/abstract-form-control-field";
import { MatSidenav } from "@angular/material/sidenav";
import { Router } from "@angular/router";
import { UtilService } from "../../../core/service/util.service";

@Component({
  selector: "zac-bedrijf-zoek",
  templateUrl: "./bedrijf-zoek.component.html",
  styleUrls: ["./bedrijf-zoek.component.less"],
})
export class BedrijfZoekComponent implements OnInit {
  @Output() bedrijf? = new EventEmitter<Bedrijf>();
  @Input() sideNav?: MatSidenav;
  bedrijven: MatTableDataSource<Bedrijf> = new MatTableDataSource<Bedrijf>();
  foutmelding: string;
  formGroup: FormGroup;
  bedrijfColumns: string[] = [
    "naam",
    "kvk",
    "vestigingsnummer",
    "type",
    "adres",
    "acties",
  ];
  loading = false;
  types = ["HOOFDVESTIGING", "NEVENVESTIGING", "RECHTSPERSOON"];

  kvkFormField: AbstractFormControlField;
  vestigingsnummerFormField: AbstractFormControlField;
  rsinFormField: AbstractFormControlField;
  handelsnaamFormField: AbstractFormControlField;
  typeFormField: AbstractFormControlField;
  postcodeFormField: AbstractFormControlField;
  huisnummerFormField: AbstractFormControlField;
  plaatsFormField: AbstractFormControlField;

  constructor(
    private klantenService: KlantenService,
    private utilService: UtilService,
    private formBuilder: FormBuilder,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.handelsnaamFormField = new InputFormFieldBuilder()
      .id("handelsnaam")
      .label("handelsnaam")
      .maxlength(100)
      .validators(CustomValidators.handelsnaam)
      .build();
    this.kvkFormField = new InputFormFieldBuilder()
      .id("kvknummer")
      .label("kvknummer")
      .validators(CustomValidators.kvk)
      .maxlength(8)
      .build();
    this.vestigingsnummerFormField = new InputFormFieldBuilder()
      .id("vestigingsnummer")
      .label("vestigingsnummer")
      .validators(CustomValidators.vestigingsnummer)
      .maxlength(12)
      .build();
    this.rsinFormField = new InputFormFieldBuilder()
      .id("rsin")
      .label("rsin")
      .validators(CustomValidators.rsin)
      .maxlength(9)
      .build();
    this.postcodeFormField = new InputFormFieldBuilder()
      .id("postcode")
      .label("postcode")
      .validators(CustomValidators.postcode)
      .maxlength(7)
      .build();
    this.typeFormField = new SelectFormFieldBuilder()
      .id("type")
      .label("type")
      .options(this.types)
      .build();
    this.huisnummerFormField = new InputFormFieldBuilder()
      .id("huisnummer")
      .label("huisnummer")
      .validators(
        Validators.min(1),
        Validators.max(99999),
        CustomValidators.huisnummer,
      )
      .maxlength(5)
      .build();
    this.plaatsFormField = new InputFormFieldBuilder()
      .id("plaats")
      .label("plaats")
      .maxlength(50)
      .build();
    this.formGroup = this.formBuilder.group({
      kvkNummer: this.kvkFormField.formControl,
      handelsnaam: this.handelsnaamFormField.formControl,
      vestigingsnummer: this.vestigingsnummerFormField.formControl,
      rsin: this.rsinFormField.formControl,
      postcode: this.postcodeFormField.formControl,
      huisnummer: this.huisnummerFormField.formControl,
      plaats: this.plaatsFormField.formControl,
      type: this.typeFormField.formControl,
    });
  }

  isValid(): boolean {
    if (!this.formGroup.valid) {
      return false;
    }
    const kvkNummer = this.kvkFormField.formControl.value;
    const handelsnaam = this.handelsnaamFormField.formControl.value;
    const vestigingsnummer = this.vestigingsnummerFormField.formControl.value;
    const rsin = this.rsinFormField.formControl.value;
    const postcode = this.postcodeFormField.formControl.value;
    const huisnummer = this.huisnummerFormField.formControl.value;

    return (
      kvkNummer ||
      handelsnaam ||
      vestigingsnummer ||
      rsin ||
      (postcode && huisnummer)
    );
  }

  createListParameters(): ListBedrijvenParameters {
    return this.removeEmpty(this.formGroup.value);
  }

  removeEmpty<T>(parameters: T): T {
    return Object.fromEntries(
      Object.entries(parameters).filter(([, v]) => !!v),
    ) as T;
  }

  zoekBedrijven() {
    this.loading = true;
    this.utilService.setLoading(true);
    this.bedrijven.data = [];
    this.klantenService
      .listBedrijven(this.createListParameters())
      .subscribe((bedrijven) => {
        this.bedrijven.data = bedrijven.resultaten;
        this.foutmelding = bedrijven.foutmelding;
        this.loading = false;
        this.utilService.setLoading(false);
      });
  }

  typeChanged(type: any): void {
    this.rsinFormField.required = type === "RECHTSPERSOON";
  }

  openBedrijfPagina(bedrijf: Bedrijf): void {
    this.sideNav?.close();
    this.router.navigate(["/bedrijf/", bedrijf.identificatie]);
  }

  wissen() {
    this.formGroup.reset();
  }
}
