/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Subject } from "rxjs";

export class ActionIcon {
  constructor(
    public icon: string,
    public title: string,
    public iconClicked: Subject<any>,
  ) {}
}
