import { Component, Input } from "@angular/core";

import { Editor } from "ngx-editor";

@Component({
  selector: "variabelen-kies-menu",
  templateUrl: "./html-editor-variabelen-kies-menu.component.html",
  styleUrls: ["./html-editor-variabelen-kies-menu.component.less"],
})
export class HtmlEditorVariabelenKiesMenuComponent {
  @Input() editor: Editor;
  @Input() variabelen: string[];

  plakExpressie(variabele: string) {
    this.editor.commands.insertText("{" + variabele + "}").exec();
  }
}
