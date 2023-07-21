/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export abstract class MenuItem {
  abstract readonly type: MenuItemType;
  abstract readonly title: string;
  abstract readonly icon?: string;
  disabled: boolean;
}

export enum MenuItemType {
  HEADER = "HEADER",
  LINK = "LINK",
  HREF = "HREF",
  BUTTON = "BUTTON",
}
