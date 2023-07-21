/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component } from "@angular/core";
import { MenuItem } from "../../shared/side-nav/menu-item/menu-item";
import { HeaderMenuItem } from "../../shared/side-nav/menu-item/header-menu-item";
import { ViewComponent } from "../../shared/abstract-view/view-component";
import { LinkMenuItem } from "../../shared/side-nav/menu-item/link-menu-item";
import { UtilService } from "../../core/service/util.service";

@Component({ template: "" })
export abstract class AdminComponent extends ViewComponent {
  menu: MenuItem[] = [];
  activeMenu: string;

  constructor(public utilService: UtilService) {
    super();
  }

  setupMenu(title: string, params?: {}): void {
    this.utilService.setTitle((this.activeMenu = title), params);
    this.menu = [];
    this.menu.push(new HeaderMenuItem("actie.admin"));
    this.menu.push(
      this.getMenuLink(
        "title.signaleringen.settings.groep",
        "/admin/groepen",
        "notifications_active",
      ),
    );
    this.menu.push(
      this.getMenuLink(
        "title.referentietabellen",
        "/admin/referentietabellen",
        "schema",
      ),
    );
    this.menu.push(
      this.getMenuLink("title.mailtemplates", "/admin/mailtemplates", "mail"),
    );
    this.menu.push(
      this.getMenuLink("title.parameters", "/admin/parameters", "tune"),
    );
    this.menu.push(
      this.getMenuLink(
        "title.formulierdefinities",
        "/admin/formulierdefinities",
        "design_services",
      ),
    );
    this.menu.push(
      this.getMenuLink(
        "title.inrichtingscheck",
        "/admin/check",
        "health_and_safety",
      ),
    );
  }

  private getMenuLink(title: string, url: string, icon: string): MenuItem {
    const menuItem: MenuItem = new LinkMenuItem(title, url, icon);
    menuItem.disabled = this.activeMenu === title;
    return menuItem;
  }
}
