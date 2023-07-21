/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Observable, Subject } from "rxjs";
import { AbstractFormControlField } from "./abstract-form-control-field";
import { FileIcon } from "../../../informatie-objecten/model/file-icon";

export abstract class AbstractFileFormField extends AbstractFormControlField {
  fileIcons = [...FileIcon.fileIcons];
  maxFileSizeMB: number;
  uploadURL: string;
  uploadError: string;
  fileUploaded$ = new Subject<string>();
  reset$ = new Subject<void>();

  protected constructor() {
    super();
  }

  isBestandstypeToegestaan(file: File): boolean {
    if (!file.name.includes(".")) {
      return false;
    } else {
      const type = this.getType(file);
      return this.fileIcons.some((fileIcon) => fileIcon.type === type);
    }
  }

  isBestandsgrootteToegestaan(file: File): boolean {
    return file.size <= this.maxFileSizeMB * 1000000;
  }

  getBestandsextensie(file: File) {
    if (file.name.indexOf(".") < 1) {
      return "-";
    }
    return "." + this.getType(file);
  }

  getBestandsgrootteMB(file: File): string {
    return parseFloat(String(file.size / 1000000)).toFixed(2);
  }

  get fileUploaded(): Observable<string> {
    return this.fileUploaded$.asObservable();
  }

  reset() {
    this.reset$.next();
  }

  getAllowedFileTypes(): string {
    return this.fileIcons
      .map((fileIcon) => fileIcon.getBestandsextensie())
      .join(", ");
  }

  private getType(file: File): string {
    return file.name.split(".").pop().toLowerCase();
  }
}
