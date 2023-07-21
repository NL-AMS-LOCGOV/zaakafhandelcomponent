/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component } from "@angular/core";
import { MatSidenav } from "@angular/material/sidenav";
import { ViewComponent } from "./view-component";

@Component({ template: "" })
export abstract class ActionsViewComponent
  extends ViewComponent
  implements AfterViewInit
{
  abstract actionsSidenav: MatSidenav;

  protected constructor() {
    super();
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
    this.actionsSidenav.closedStart.subscribe(() => {
      this.sideNavContainer.hasBackdrop = false;
    });
    this.actionsSidenav.openedStart.subscribe(() => {
      this.sideNavContainer.hasBackdrop = true;
    });
  }
}
