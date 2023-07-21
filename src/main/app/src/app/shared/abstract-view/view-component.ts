/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AfterViewInit, Component, OnDestroy } from "@angular/core";
import {
  MatDrawerMode,
  MatSidenav,
  MatSidenavContainer,
} from "@angular/material/sidenav";
import { Subscription } from "rxjs";
import { SideNavUtil } from "../side-nav/side-nav.util";

@Component({ template: "" })
export abstract class ViewComponent implements OnDestroy, AfterViewInit {
  abstract sideNavContainer: MatSidenavContainer;
  abstract menuSidenav: MatSidenav;
  protected subscriptions$: Subscription[] = [];
  sideNaveMode: MatDrawerMode = SideNavUtil.getMode();

  protected constructor() {}

  ngAfterViewInit(): void {
    this.menuSidenav.openedStart.subscribe(() => {
      this.sideNavContainer.hasBackdrop = false;
    });
  }

  menuModeChanged(mode: string): void {
    this.sideNaveMode = mode as MatDrawerMode;
    this.updateMargins();
  }

  ngOnDestroy(): void {
    this.subscriptions$.forEach((subscription$) => subscription$.unsubscribe());
  }

  protected updateMargins(): void {
    setTimeout(() => this.sideNavContainer.updateContentMargins(), 300);
  }
}
