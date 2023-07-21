/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnInit, ViewChild } from "@angular/core";
import { UtilService } from "../../core/service/util.service";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { Group } from "../../identity/model/group";
import { IdentityService } from "../../identity/identity.service";
import { Observable } from "rxjs";
import { MatTableDataSource } from "@angular/material/table";
import { SignaleringSettings } from "../../signaleringen/model/signalering-settings";
import { SignaleringenSettingsBeheerService } from "../signaleringen-settings-beheer.service";
import { AdminComponent } from "../admin/admin.component";

@Component({
  templateUrl: "./groep-signaleringen.component.html",
  styleUrls: ["./groep-signaleringen.component.less"],
})
export class GroepSignaleringenComponent
  extends AdminComponent
  implements OnInit
{
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;

  isLoadingResults = false;
  groepen: Observable<Group[]>;
  groepId: string;
  columns: string[] = ["subjecttype", "type", "dashboard", "mail"];
  dataSource: MatTableDataSource<SignaleringSettings> =
    new MatTableDataSource<SignaleringSettings>();

  constructor(
    private identityService: IdentityService,
    private service: SignaleringenSettingsBeheerService,
    public utilService: UtilService,
  ) {
    super(utilService);
  }

  ngOnInit(): void {
    this.setupMenu("title.signaleringen.settings.groep");
    this.groepen = this.identityService.listGroups();
  }

  laadSignaleringSettings(groep: Group): void {
    this.isLoadingResults = true;
    this.service.list(groep.id).subscribe((instellingen) => {
      this.dataSource.data = instellingen;
      this.groepId = groep.id;
      this.isLoadingResults = false;
    });
  }

  changed(row: SignaleringSettings, column: string, checked: boolean): void {
    this.utilService.setLoading(true);
    row[column] = checked;
    this.service.put(this.groepId, row).subscribe(() => {
      this.utilService.setLoading(false);
    });
  }
}
