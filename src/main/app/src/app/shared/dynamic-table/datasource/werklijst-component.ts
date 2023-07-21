/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit } from "@angular/core";
import { GebruikersvoorkeurenService } from "../../../gebruikersvoorkeuren/gebruikersvoorkeuren.service";
import { ActivatedRoute } from "@angular/router";
import { PageEvent } from "@angular/material/paginator";
import { Werklijst } from "../../../gebruikersvoorkeuren/model/werklijst";

import { WerklijstRechten } from "../../../policy/model/werklijst-rechten";
import { TabelGegevens } from "../model/tabel-gegevens";

@Component({ template: "" })
export abstract class WerklijstComponent implements OnInit {
  abstract gebruikersvoorkeurenService: GebruikersvoorkeurenService;
  abstract route: ActivatedRoute;
  aantalPerPagina: number;
  pageSizeOptions: number[];
  werklijstRechten: WerklijstRechten;

  protected constructor() {}

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      const tabelGegevens: TabelGegevens = data.tabelGegevens;
      this.aantalPerPagina = tabelGegevens.aantalPerPagina;
      this.pageSizeOptions = tabelGegevens.pageSizeOptions;
      this.werklijstRechten = tabelGegevens.werklijstRechten;
    });
  }

  paginatorChanged($event: PageEvent): void {
    if (this.aantalPerPagina !== $event.pageSize) {
      this.aantalPerPagina = $event.pageSize;
      this.gebruikersvoorkeurenService
        .updateAantalPerPagina(this.getWerklijst(), this.aantalPerPagina)
        .subscribe();
    }
  }

  abstract getWerklijst(): Werklijst;
}
