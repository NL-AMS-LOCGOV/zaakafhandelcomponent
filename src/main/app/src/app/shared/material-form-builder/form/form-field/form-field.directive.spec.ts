/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormFieldDirective } from "./form-field.directive";

describe("FormFieldDirective", () => {
  let directive;
  let mockViewContainerRef;

  beforeEach(() => {
    mockViewContainerRef = jasmine.createSpyObj([""]);

    directive = new FormFieldDirective(mockViewContainerRef);
  });

  it("should create an instance", () => {
    expect(directive).toBeTruthy();
  });
});
