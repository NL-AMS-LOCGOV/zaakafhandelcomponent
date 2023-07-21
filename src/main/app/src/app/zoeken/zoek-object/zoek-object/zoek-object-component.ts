/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component } from "@angular/core";
import { MatSidenav } from "@angular/material/sidenav";

@Component({ template: "" })
export abstract class ZoekObjectComponent {
  abstract sideNav: MatSidenav;

  constructor() {}
}
