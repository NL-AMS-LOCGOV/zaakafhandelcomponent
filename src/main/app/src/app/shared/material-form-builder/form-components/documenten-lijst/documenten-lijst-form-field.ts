/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FieldType } from "../../model/field-type.enum";
import { Observable } from "rxjs";
import { EnkelvoudigInformatieobject } from "../../../../informatie-objecten/model/enkelvoudig-informatieobject";
import { AbstractFormControlField } from "../../model/abstract-form-control-field";

export class DocumentenLijstFormField extends AbstractFormControlField {
  fieldType = FieldType.DOCUMENTEN_LIJST;
  documenten: Observable<EnkelvoudigInformatieobject[]>;
  documentenChecked: string[];
  columns: string[] = [
    "select",
    "titel",
    "documentType",
    "status",
    "versie",
    "auteur",
    "creatiedatum",
    "bestandsomvang",
    "indicaties",
    "url",
  ];
  selectLabel = "";
  openInNieuweTab = false;

  constructor() {
    super();
  }

  hasReadonlyView() {
    return true;
  }

  updateDocumenten(documenten: Observable<EnkelvoudigInformatieobject[]>) {
    this.documenten = documenten;
  }

  removeColumn(id: string) {
    this.columns.splice(this.columns.indexOf(id), 1);
  }
}
