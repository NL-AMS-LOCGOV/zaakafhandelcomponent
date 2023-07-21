/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { InjectionToken } from "@angular/core";

export interface MaterialFormBuilderConfig {
  googleMapsApiKey?: string;
}

export const BUILDER_CONFIG = new InjectionToken<MaterialFormBuilderConfig>(
  "BuilderConfig",
);
