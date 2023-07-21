/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
} from "@angular/core";

import { Besluit } from "../model/besluit";
import { Observable, of } from "rxjs";
import { MatTableDataSource } from "@angular/material/table";
import { HistorieRegel } from "../../shared/historie/model/historie-regel";
import { ZakenService } from "../zaken.service";
import { TextIcon } from "../../shared/edit/text-icon";
import { Conditionals } from "../../shared/edit/conditional-fn";
import { DocumentenLijstFormField } from "../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-form-field";
import { DocumentenLijstFieldBuilder } from "../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder";
import { IndicatiesLayout } from "../../shared/indicaties/indicaties.component";
import { DialogData } from "../../shared/dialog/dialog-data";
import { Validators } from "@angular/forms";
import * as moment from "moment";
import { DialogComponent } from "../../shared/dialog/dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { TranslateService } from "@ngx-translate/core";
import { InputFormField } from "../../shared/material-form-builder/form-components/input/input-form-field";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { DateFormFieldBuilder } from "../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { DateFormField } from "../../shared/material-form-builder/form-components/date/date-form-field";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { SelectFormField } from "../../shared/material-form-builder/form-components/select/select-form-field";
import { ConfiguratieService } from "../../configuratie/configuratie.service";
import { VervalReden } from "../model/vervalReden";
import { HiddenFormField } from "../../shared/material-form-builder/form-components/hidden/hidden-form-field";
import { HiddenFormFieldBuilder } from "../../shared/material-form-builder/form-components/hidden/hidden-form-field-builder";
import { UtilService } from "../../core/service/util.service";
import { MessageFormField } from "../../shared/material-form-builder/form-components/message/message-form-field";
import { MessageFormFieldBuilder } from "../../shared/material-form-builder/form-components/message/message-form-field-builder";
import { MessageLevel } from "../../shared/material-form-builder/form-components/message/message-level.enum";

@Component({
  selector: "zac-besluit-view",
  templateUrl: "./besluit-view.component.html",
  styleUrls: ["./besluit-view.component.less"],
})
export class BesluitViewComponent implements OnInit, OnChanges {
  @Input() besluiten: Besluit[];
  @Input() readonly: boolean;
  @Output() besluitWijzigen = new EventEmitter<Besluit>();
  @Output() doIntrekking: EventEmitter<any> = new EventEmitter<any>();
  readonly indicatiesLayout = IndicatiesLayout;
  histories: Record<string, MatTableDataSource<HistorieRegel>> = {};

  besluitInformatieobjecten: Record<string, DocumentenLijstFormField> = {};
  toolTipIcon = new TextIcon(
    Conditionals.always,
    "info",
    "toolTip_icon",
    "",
    "pointer",
    true,
  );

  constructor(
    private zakenService: ZakenService,
    private dialog: MatDialog,
    private configuratieService: ConfiguratieService,
    private translate: TranslateService,
    private utilService: UtilService,
  ) {}

  ngOnInit(): void {
    if (this.besluiten.length > 0) {
      this.loadBesluitData(this.besluiten[0].uuid);
    }
  }

  ngOnChanges() {
    for (const key in this.besluitInformatieobjecten) {
      if (this.besluitInformatieobjecten.hasOwnProperty(key)) {
        this.besluitInformatieobjecten[key].updateDocumenten(
          of(this.getBesluit(key).informatieobjecten),
        );
      }
    }

    for (const historieKey in this.histories) {
      if (this.histories.hasOwnProperty(historieKey)) {
        this.loadHistorie(historieKey);
      }
    }
  }

  loadBesluitData(uuid) {
    if (!this.histories[uuid]) {
      this.loadHistorie(uuid);
    }

    if (!this.besluitInformatieobjecten[uuid]) {
      const besluit = this.getBesluit(uuid);
      this.besluitInformatieobjecten[uuid] = new DocumentenLijstFieldBuilder()
        .id("documenten")
        .label("documenten")
        .documenten(of(besluit.informatieobjecten))
        .removeColumn("status")
        .readonly(true)
        .build();
    }
  }

  private loadHistorie(uuid) {
    this.zakenService.listBesluitHistorie(uuid).subscribe((historie) => {
      this.histories[uuid] = new MatTableDataSource<HistorieRegel>();
      this.histories[uuid].data = historie;
    });
  }

  private getBesluit(uuid: string): Besluit {
    return this.besluiten.find((value) => value.uuid === uuid);
  }

  isReadonly(besluit: Besluit) {
    return this.readonly || besluit.isIngetrokken;
  }

  intrekken(besluit: Besluit) {
    const dialogData = new DialogData(
      [
        this.maakIdField(besluit),
        this.maakVervaldatumField(besluit),
        this.maakVervalredenField(besluit),
        this.maakToelichtingField(),
        this.maakMessageField(besluit),
      ],
      (results: any[]) => this.saveIntrekking(results),
      this.translate.instant("msg.besluit.intrekken"),
    );
    dialogData.confirmButtonActionKey = "actie.besluit.intrekken";
    this.dialog.open(DialogComponent, {
      data: dialogData,
    });
  }

  saveIntrekking(results: any[]): Observable<void> {
    this.doIntrekking.emit(results);
    return of(null);
  }

  private maakIdField(besluit: Besluit): HiddenFormField {
    return new HiddenFormFieldBuilder(besluit.uuid).id("uuid").build();
  }

  private maakVervaldatumField(besluit: Besluit): DateFormField {
    return new DateFormFieldBuilder(besluit.vervaldatum)
      .id("vervaldatum")
      .label("vervaldatum")
      .minDate(moment(besluit.ingangsdatum, moment.ISO_8601).toDate())
      .validators(Validators.required)
      .build();
  }

  private maakVervalredenField(besluit: Besluit): SelectFormField {
    const vervalRedenen = this.utilService.getEnumAsSelectListExceptFor(
      "besluit.vervalreden",
      VervalReden,
      [VervalReden.TIJDELIJK],
    );
    const vervalReden = besluit.vervalreden
      ? {
          label: this.translate.instant(
            "besluit.vervalreden." + besluit.vervalreden,
          ),
          value: besluit.vervalreden,
        }
      : null;
    return new SelectFormFieldBuilder(vervalReden)
      .id("vervalreden")
      .label("besluit.vervalreden")
      .optionLabel("label")
      .options(vervalRedenen)
      .validators(Validators.required)
      .build();
  }

  private maakToelichtingField(): InputFormField {
    return new InputFormFieldBuilder()
      .id("toelichting")
      .label("toelichting")
      .validators(Validators.required)
      .build();
  }

  private maakMessageField(besluit: Besluit): MessageFormField {
    const documentenVerstuurd: boolean = besluit.informatieobjecten.some(
      (document) => {
        return document.verzenddatum != null;
      },
    );
    return new MessageFormFieldBuilder(documentenVerstuurd)
      .id("documentenverstuurd")
      .level(MessageLevel.WARNING)
      .text("msg.besluit.documenten.verstuurd")
      .build();
  }
}
