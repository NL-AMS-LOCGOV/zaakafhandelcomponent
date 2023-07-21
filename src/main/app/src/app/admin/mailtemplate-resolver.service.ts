/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { MailtemplateBeheerService } from "./mailtemplate-beheer.service";
import { Mailtemplate } from "./model/mailtemplate";

@Injectable({
  providedIn: "root",
})
export class MailtemplateResolver {
  constructor(private service: MailtemplateBeheerService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<Mailtemplate> {
    return this.service.readMailtemplate(route.paramMap.get("id"));
  }
}
