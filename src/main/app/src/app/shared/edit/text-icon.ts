/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ConditionalFn } from "./conditional-fn";

export class TextIcon {
  showIcon: ConditionalFn;
  icon: string;
  id: string;
  title: string;
  styleClass: string;
  outlined: boolean;

  constructor(
    showIcon: ConditionalFn,
    icon: string,
    id: string,
    title: string,
    styleClass: string,
    outlined?: boolean,
  ) {
    this.showIcon = showIcon;
    this.icon = icon;
    this.id = id;
    this.title = title;
    this.styleClass = styleClass;
    this.outlined = outlined;
  }
}
