/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, OnInit, Output } from "@angular/core";
import { Klant } from "../../model/klanten/klant";

@Component({
  selector: "zac-klant-zoek",
  templateUrl: "./klant-zoek.component.html",
  styleUrls: ["./klant-zoek.component.less"],
})
export class KlantZoekComponent implements OnInit {
  @Output() klant = new EventEmitter<Klant>();

  constructor() {}

  ngOnInit(): void {}

  klantGeselecteerd(klant: Klant): void {
    this.klant.emit(klant);
  }
}
