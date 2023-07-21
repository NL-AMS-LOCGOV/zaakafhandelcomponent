/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { AbstractFormFieldBuilder } from "../../model/abstract-form-field-builder";
import { ActionIcon } from "../../../edit/action-icon";
import { HtmlEditorFormField } from "./html-editor-form-field";
import { Observable } from "rxjs";
import { Mailtemplate } from "../../../../admin/model/mailtemplate";
import { MailtemplateVariabele } from "../../../../admin/model/mailtemplate-variabele";

export class HtmlEditorFormFieldBuilder extends AbstractFormFieldBuilder {
  readonly formField: HtmlEditorFormField;

  constructor(value?: any) {
    super();
    this.formField = new HtmlEditorFormField();
    this.formField.initControl(value ? value : "");
  }

  mailtemplateBody(mailtemplate$: Observable<Mailtemplate>): this {
    this.formField.mailtemplateBody$ = mailtemplate$;
    return this;
  }

  mailtemplateOnderwerp(mailtemplate$: Observable<Mailtemplate>): this {
    this.formField.mailtemplateOnderwerp$ = mailtemplate$;
    return this;
  }

  variabelen(variabelen: MailtemplateVariabele[]): this {
    this.formField.variabelen = variabelen;
    return this;
  }

  emptyToolbar(): this {
    this.formField.emptyToolbar = true;
    return this;
  }

  icon(icon: ActionIcon): this {
    this.formField.icons = [icon];
    return this;
  }

  icons(icons: ActionIcon[]): this {
    this.formField.icons = icons;
    return this;
  }

  maxlength(maxlength: number, showCount = true): this {
    this.formField.maxlength = maxlength;
    this.formField.showCount = showCount;
    return this;
  }
}
