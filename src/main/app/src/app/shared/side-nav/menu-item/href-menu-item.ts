/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { MenuItem, MenuItemType } from "./menu-item";

export class HrefMenuItem extends MenuItem {
  readonly type: MenuItemType = MenuItemType.HREF;

  constructor(
    readonly title: string,
    readonly url: string,
    readonly icon?: string,
  ) {
    super();
  }
}
