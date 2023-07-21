/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input, OnInit } from "@angular/core";
import { TaakStatus } from "../../taken/model/taak-status.enum";

@Component({
  selector: "zac-status-label",
  templateUrl: "./status-label.component.html",
  styleUrls: ["./status-label.component.less"],
})
export class StatusLabelComponent implements OnInit {
  @Input() taakStatus: TaakStatus;

  constructor() {}

  ngOnInit(): void {}

  bepaalStatusLabelKleur(taakStatus: TaakStatus): string {
    if (taakStatus === TaakStatus.Toegekend) {
      return "status-label-primary";
    } else if (taakStatus === TaakStatus.Afgerond) {
      return "status-label-success";
    } else {
      return "status-label-default";
    }
  }
}
