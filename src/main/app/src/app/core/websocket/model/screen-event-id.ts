/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class ScreenEventId {
  resource: string;
  detail: string;

  constructor(objectId: string) {
    this.resource = objectId;
  }
}
