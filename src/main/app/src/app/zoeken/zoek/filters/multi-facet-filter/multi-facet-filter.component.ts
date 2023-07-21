/*
 * SPDX-FileCopyrightText: 2021-2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { FilterResultaat } from "../../../model/filter-resultaat";
import { FilterParameters } from "../../../model/filter-parameters";

@Component({
  selector: "zac-multi-facet-filter",
  templateUrl: "./multi-facet-filter.component.html",
  styleUrls: ["./multi-facet-filter.component.less"],
})
export class MultiFacetFilterComponent implements OnInit {
  formGroup: FormGroup;
  @Input() filter: FilterParameters;
  @Input() opties: FilterResultaat[];
  @Input() label: string;
  @Output() changed = new EventEmitter<FilterParameters>();

  inverse: boolean;
  selected: string[];

  /* veld: prefix */
  public VERTAALBARE_FACETTEN = {
    TAAK_STATUS: "taak.status.",
    TYPE: "type.",
    TOEGEKEND: "zoeken.filter.jaNee.",
    ZAAK_INDICATIES: "indicatie.",
    DOCUMENT_INDICATIES: "indicatie.",
    DOCUMENT_STATUS: "informatieobject.status.",
    ZAAK_VERTROUWELIJKHEIDAANDUIDING: "vertrouwelijkheidaanduiding.",
    ZAAK_ARCHIEF_NOMINATIE: "archiefNominatie.",
  };

  constructor(private _formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.inverse = this.filter?.inverse === "true";
    this.formGroup = this._formBuilder.group({});
    this.selected = this.filter?.waarden ? this.filter.waarden : [];
    this.opties.forEach((value) => {
      this.formGroup.addControl(
        value.naam,
        new FormControl(!!this.selected.find((s) => s === value.naam)),
      );
    });
  }

  checkboxChange(): void {
    const checked: string[] = [];
    Object.keys(this.formGroup.controls).forEach((key) => {
      if (this.formGroup.controls[key].value) {
        checked.push(key);
      }
    });
    this.changed.emit(new FilterParameters(checked, this.inverse));
  }

  isVertaalbaar(veld: string): boolean {
    return this.VERTAALBARE_FACETTEN[veld] !== undefined;
  }

  invert() {
    this.inverse = !this.inverse;
    if (Object.values(this.formGroup.value).includes(true)) {
      this.checkboxChange();
    }
  }
}
