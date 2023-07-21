/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ActionIcon } from "../../../shared/edit/action-icon";
import { Subject } from "rxjs";

export class ActionBarAction {
  public actionEnabled: () => boolean;

  constructor(
    public text: string,
    public entityType: ActionEntityType,
    public subText: string,
    public action: ActionIcon,
    public dissmis: Subject<void>,
    public disabledReden?: () => null | string,
  ) {
    if (!this.disabledReden) {
      this.disabledReden = () => null;
    }
    this.actionEnabled = () => this.disabledReden() == null;
  }
}

export enum ActionEntityType {
  DOCUMENT = "DOCUMENT",
  ZAAK = "ZAAK",
}
