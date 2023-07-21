/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FormConfig } from "./form-config";

export class FormConfigBuilder {
  private readonly formConfig: FormConfig;

  constructor() {
    this.formConfig = new FormConfig();
  }

  partialText(text: string): this {
    this.formConfig.partialButtonText = text;
    return this;
  }

  partialIcon(icon: string): this {
    this.formConfig.partialButtonIcon = icon;
    return this;
  }

  saveText(text: string): this {
    this.formConfig.saveButtonText = text;
    return this;
  }

  saveIcon(icon: string): this {
    this.formConfig.saveButtonIcon = icon;
    return this;
  }

  cancelText(text: string): this {
    this.formConfig.cancelButtonText = text;
    return this;
  }

  cancelIcon(icon: string): this {
    this.formConfig.cancelButtonIcon = icon;
    return this;
  }

  build(): FormConfig {
    return this.formConfig;
  }
}
