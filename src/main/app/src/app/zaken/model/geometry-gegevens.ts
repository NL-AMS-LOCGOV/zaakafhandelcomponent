/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Geometry } from "./geometry";

export class GeometryGegevens {
  constructor(
    public geometry: Geometry,
    public reden: string,
  ) {}
}
