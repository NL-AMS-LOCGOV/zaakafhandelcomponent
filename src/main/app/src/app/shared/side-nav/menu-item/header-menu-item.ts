/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { MenuItem, MenuItemType } from "./menu-item";

export class HeaderMenuItem extends MenuItem {
  readonly type: MenuItemType = MenuItemType.HEADER;

  constructor(
    readonly title: string,
    readonly icon?: string,
  ) {
    super();
  }
}
