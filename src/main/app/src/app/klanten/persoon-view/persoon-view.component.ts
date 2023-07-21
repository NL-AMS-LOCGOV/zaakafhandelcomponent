/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { UtilService } from "../../core/service/util.service";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Persoon } from "../model/personen/persoon";

@Component({
  templateUrl: "./persoon-view.component.html",
  styleUrls: ["./persoon-view.component.less"],
})
export class PersoonViewComponent implements OnInit {
  persoon: Persoon;

  constructor(
    private utilService: UtilService,
    private _route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.utilService.setTitle("persoonsgegevens");
    this._route.data.subscribe((data) => {
      this.persoon = data.persoon;
    });
  }
}
