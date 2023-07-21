/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class FormFieldHint {
  label: string;
  align: "start" | "end";

  constructor(label: string, align?: "start" | "end") {
    this.label = label;
    this.align = align ? align : "end";
  }
}
