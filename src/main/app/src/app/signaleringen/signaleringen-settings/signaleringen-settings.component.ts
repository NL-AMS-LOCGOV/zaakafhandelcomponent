/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { UtilService } from "../../core/service/util.service";
import { AfterViewInit, Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { SignaleringenSettingsService } from "../signaleringen-settings.service";
import { SignaleringSettings } from "../model/signalering-settings";

@Component({
  templateUrl: "./signaleringen-settings.component.html",
  styleUrls: ["./signaleringen-settings.component.less"],
})
export class SignaleringenSettingsComponent implements OnInit, AfterViewInit {
  isLoadingResults = true;
  columns: string[] = ["subjecttype", "type", "dashboard", "mail"];
  dataSource: MatTableDataSource<SignaleringSettings> =
    new MatTableDataSource<SignaleringSettings>();

  constructor(
    private service: SignaleringenSettingsService,
    private utilService: UtilService,
  ) {}

  ngOnInit(): void {
    this.utilService.setTitle("title.signaleringen.settings");
  }

  ngAfterViewInit(): void {
    this.service.list().subscribe((instellingen) => {
      this.dataSource.data = instellingen;
      this.isLoadingResults = false;
    });
  }

  changed(row: SignaleringSettings, column: string, checked: boolean): void {
    this.utilService.setLoading(true);
    row[column] = checked;
    this.service.put(row).subscribe(() => {
      this.utilService.setLoading(false);
    });
  }
}
