/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { User } from "./user";

export class LoggedInUser extends User {
  groupIds: string[];
}
