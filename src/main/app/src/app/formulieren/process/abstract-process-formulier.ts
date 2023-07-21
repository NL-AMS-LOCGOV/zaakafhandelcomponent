/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { ProcessTaskData } from "../../plan-items/model/process-task-data";

export abstract class AbstractProcessFormulier {
  getData(): ProcessTaskData {
    return null;
  }
}
