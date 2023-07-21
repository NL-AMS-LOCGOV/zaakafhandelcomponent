/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Directive, HostListener } from "@angular/core";
import { NavigationService } from "./navigation.service";

@Directive({
  selector: "[zacBackButton]",
})
export class BackButtonDirective {
  constructor(private navigation: NavigationService) {}

  @HostListener("click")
  onClick(): void {
    this.navigation.back();
  }
}
