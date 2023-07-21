/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input } from "@angular/core";
import { MatSidenav } from "@angular/material/sidenav";
import { ZoekObjectComponent } from "../zoek-object/zoek-object-component";
import { DocumentZoekObject } from "../../model/documenten/document-zoek-object";

@Component({
  selector: "zac-document-zoek-object",
  styleUrls: ["../zoek-object/zoek-object.component.less"],
  templateUrl: "./document-zoek-object.component.html",
})
export class DocumentZoekObjectComponent extends ZoekObjectComponent {
  @Input() document: DocumentZoekObject;
  @Input() sideNav: MatSidenav;
}
