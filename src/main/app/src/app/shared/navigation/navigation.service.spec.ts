/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { NavigationService } from "./navigation.service";
import { TestBed } from "@angular/core/testing";
import { UtilService } from "../../core/service/util.service";
import { Location } from "@angular/common";
import { RouterTestingModule } from "@angular/router/testing";

describe("NavigationService", () => {
  let service: NavigationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Location, UtilService],
      imports: [RouterTestingModule.withRoutes([])],
    }).compileComponents();
    service = TestBed.inject(NavigationService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
