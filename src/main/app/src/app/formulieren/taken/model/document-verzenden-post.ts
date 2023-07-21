/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractTaakFormulier } from "../abstract-taak-formulier";
import { TranslateService } from "@ngx-translate/core";
import { TakenService } from "../../../taken/taken.service";
import { InformatieObjectenService } from "../../../informatie-objecten/informatie-objecten.service";
import { TextareaFormFieldBuilder } from "../../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { Validators } from "@angular/forms";
import { DocumentenLijstFieldBuilder } from "../../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder";
import { InformatieobjectZoekParameters } from "../../../informatie-objecten/model/informatieobject-zoek-parameters";
import { DateFormFieldBuilder } from "../../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { ParagraphFormFieldBuilder } from "../../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder";
import { Observable, of } from "rxjs";
import { EnkelvoudigInformatieobject } from "../../../informatie-objecten/model/enkelvoudig-informatieobject";

export class DocumentVerzendenPost extends AbstractTaakFormulier {
  fields = {
    DOCUMENTEN_VERZENDEN_POST: "documentenVerzendenPost",
    TOELICHTING: AbstractTaakFormulier.TOELICHTING_FIELD,
    VERZENDDATUM: "verzenddatum",
  };

  taakinformatieMapping = {
    uitkomst: "verzonden",
  };

  constructor(
    translate: TranslateService,
    public takenService: TakenService,
    public informatieObjectenService: InformatieObjectenService,
  ) {
    super(translate, informatieObjectenService);
  }

  protected _initStartForm() {
    const fields = this.fields;
    this.form.push(
      [
        new DocumentenLijstFieldBuilder()
          .id(fields.DOCUMENTEN_VERZENDEN_POST)
          .label(fields.DOCUMENTEN_VERZENDEN_POST)
          .removeColumn("status")
          .validators(Validators.required)
          .documenten(
            this.informatieObjectenService.listInformatieobjectenVoorVerzenden(
              this.zaak.uuid,
            ),
          )
          .openInNieuweTab()
          .build(),
      ],
      [
        new TextareaFormFieldBuilder()
          .id(fields.TOELICHTING)
          .label(fields.TOELICHTING)
          .maxlength(1000)
          .build(),
      ],
    );
  }

  protected _initBehandelForm() {
    const fields = this.fields;
    const verzenddatum = this.getDataElement(fields.VERZENDDATUM);
    this.form.push(
      [
        new ParagraphFormFieldBuilder()
          .text(
            this.translate.instant("msg.document.verzenden.post.behandelen"),
          )
          .build(),
      ],
      [
        new DocumentenLijstFieldBuilder()
          .id(fields.DOCUMENTEN_VERZENDEN_POST)
          .label(fields.DOCUMENTEN_VERZENDEN_POST)
          .removeColumn("select")
          .removeColumn("status")
          .documenten(this.getDocumenten$(fields.DOCUMENTEN_VERZENDEN_POST))
          .documentenChecked(
            this.getDocumentenChecked(fields.DOCUMENTEN_VERZENDEN_POST),
          )
          .readonly(true)
          .build(),
      ],
      [
        new DateFormFieldBuilder(verzenddatum ? verzenddatum : new Date())
          .id(fields.VERZENDDATUM)
          .label(fields.VERZENDDATUM)
          .validators(Validators.required)
          .readonly(this.readonly)
          .build(),
      ],
    );
  }

  private getDocumenten$(
    field: string,
  ): Observable<EnkelvoudigInformatieobject[]> {
    const dataElement = this.getDataElement(field);
    if (dataElement) {
      const zoekParameters = new InformatieobjectZoekParameters();
      if (dataElement) {
        zoekParameters.zaakUUID = this.zaak.uuid;
        zoekParameters.informatieobjectUUIDs = dataElement.split(
          AbstractTaakFormulier.TAAK_DATA_MULTIPLE_VALUE_JOIN_CHARACTER,
        );
        return this.informatieObjectenService.listEnkelvoudigInformatieobjecten(
          zoekParameters,
        );
      }
    }
    return of([]);
  }

  private getDocumentenChecked(field: string): string[] {
    const dataElement = this.getDataElement(field);
    if (dataElement) {
      return dataElement.split(
        AbstractTaakFormulier.TAAK_DATA_MULTIPLE_VALUE_JOIN_CHARACTER,
      );
    }
    return [];
  }
}
