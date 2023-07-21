/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormComponent } from "../../model/form-component";
import { TranslateService } from "@ngx-translate/core";
import { Editor, Toolbar } from "ngx-editor";
import { HtmlEditorFormField } from "./html-editor-form-field";
import { MailtemplateVariabele } from "../../../../admin/model/mailtemplate-variabele";

@Component({
  templateUrl: "./html-editor.component.html",
  styleUrls: ["./html-editor.component.less"],
})
export class HtmlEditorComponent
  extends FormComponent
  implements OnInit, OnDestroy
{
  data: HtmlEditorFormField;
  editor: Editor;
  toolbar: Toolbar = [
    ["bold", "italic", "underline"],
    ["blockquote"],
    ["ordered_list", "bullet_list"],
    [{ heading: ["h1", "h2", "h3", "h4", "h5", "h6"] }],
    ["link", "image"],
    ["text_color", "background_color"],
    ["align_left", "align_center", "align_right", "align_justify"],
    [],
  ];

  constructor(public translate: TranslateService) {
    super();
  }

  ngOnInit(): void {
    this.editor = new Editor();
    if (this.data.mailtemplateBody$) {
      this.data.mailtemplateBody$.subscribe((mailtemplate) => {
        this.data.value(mailtemplate.body);
        this.data.variabelen = mailtemplate.variabelen;
      });
    }
    if (this.data.mailtemplateOnderwerp$) {
      this.data.mailtemplateOnderwerp$.subscribe((mailtemplate) => {
        this.data.value(mailtemplate.onderwerp);
        this.data.variabelen = mailtemplate.variabelen;
      });
    }
    if (this.data.emptyToolbar) {
      this.toolbar = [];
    }
  }

  ngOnDestroy(): void {
    this.editor.destroy();
  }
}
