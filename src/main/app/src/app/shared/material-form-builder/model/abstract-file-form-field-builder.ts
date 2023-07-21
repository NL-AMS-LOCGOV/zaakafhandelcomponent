/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "./abstract-form-field-builder";
import { AbstractFileFormField } from "./abstract-file-form-field";
import { FormFieldHint } from "./form-field-hint";
import { Observable } from "rxjs";
import { FileIcon } from "../../../informatie-objecten/model/file-icon";

export abstract class AbstractFileFormFieldBuilder extends AbstractFormFieldBuilder {
  abstract readonly formField: AbstractFileFormField;

  constructor() {
    super();
  }

  uploadURL(url: string): this {
    this.formField.uploadURL = url;
    return this;
  }

  maxFileSizeMB(maxFileSizeMB$: Observable<number>): this {
    this.formField.maxFileSizeMB = 1; // Om de validate() te laten slagen.
    maxFileSizeMB$.subscribe((fileSizeMB) => {
      this.formField.maxFileSizeMB = fileSizeMB;
      this.updateHint();
    });
    return this;
  }

  additionalAllowedFileTypes(additionalFileTypes$: Observable<string[]>): this {
    additionalFileTypes$.subscribe((fileTypes) => {
      if (fileTypes.length > 0) {
        fileTypes
          .map(
            (fileType) =>
              new FileIcon(fileType.trim().toLowerCase(), "fa-file"),
          )
          .forEach((fileIcon) => this.formField.fileIcons.push(fileIcon));
        this.formField.fileIcons.sort((fileIconA, fileIconB) =>
          fileIconA.compare(fileIconB),
        );
        this.updateHint();
      }
    });
    return this;
  }

  validate(): void {
    super.validate();
    if (!this.formField.uploadURL) {
      throw new Error("Missing value for restURL");
    }
    if (!this.formField.maxFileSizeMB) {
      throw new Error("Missing value for maxFileSizeMB");
    }
  }

  protected updateHint() {
    this.formField.hint = new FormFieldHint(
      "Maximale bestandsgrootte: " +
        this.formField.maxFileSizeMB +
        " MB | Toegestane bestandstypen: " +
        this.formField.getAllowedFileTypes(),
      "end",
    );
  }
}
