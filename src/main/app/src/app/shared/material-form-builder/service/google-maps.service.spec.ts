/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { GoogleMapsService } from "./google-maps.service";

describe("GoogleMapsServiceService", () => {
  let service: GoogleMapsService;
  let mockMaterialFormBuilderConfig;
  let mockHttpClient;

  beforeEach(() => {
    mockMaterialFormBuilderConfig = jasmine.createSpyObj([""]);
    mockHttpClient = jasmine.createSpyObj(["jsonp"]);

    service = new GoogleMapsService(
      mockMaterialFormBuilderConfig,
      mockHttpClient,
    );
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
