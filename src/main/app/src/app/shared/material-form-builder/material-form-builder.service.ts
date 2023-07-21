/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Injectable, Type } from "@angular/core";
import { FieldType } from "./model/field-type.enum";
import { FormItem } from "./model/form-item";
import { AbstractFormField } from "./model/abstract-form-field";
import { InputComponent } from "./form-components/input/input.component";
import { FormComponent } from "./model/form-component";
import { DateComponent } from "./form-components/date/date.component";
import { TextareaComponent } from "./form-components/textarea/textarea.component";
import { HeadingComponent } from "./form-components/heading/heading.component";
import { SelectComponent } from "./form-components/select/select.component";
import { GoogleMapsComponent } from "./form-components/google-maps/google-maps.component";
import { ReadonlyComponent } from "./form-components/readonly/readonly.component";
import { FileComponent } from "./form-components/file/file.component";
import { AutocompleteComponent } from "./form-components/autocomplete/autocomplete.component";
import { CheckboxComponent } from "./form-components/checkbox/checkbox.component";
import { TaakDocumentUploadComponent } from "./form-components/taak-document-upload/taak-document-upload.component";
import { RadioComponent } from "./form-components/radio/radio.component";
import { ParagraphComponent } from "./form-components/paragraph/paragraph.component";
import { MedewerkerGroepComponent } from "./form-components/medewerker-groep/medewerker-groep.component";
import { DividerComponent } from "./form-components/divider/divider.component";
import { HiddenComponent } from "./form-components/hidden/hidden.component";
import { HtmlEditorComponent } from "./form-components/html-editor/html-editor.component";
import { DocumentenLijstComponent } from "./form-components/documenten-lijst/documenten-lijst.component";
import { DocumentenOndertekenenComponent } from "./form-components/documenten-ondertekenen/documenten-ondertekenen.component";
import { MessageComponent } from "./form-components/message/message.component";

@Injectable({
  providedIn: "root",
})
export class MaterialFormBuilderService {
  constructor() {}

  public getFormItem(formField: AbstractFormField): FormItem {
    return new FormItem(
      MaterialFormBuilderService.getType(formField.fieldType),
      formField,
    );
  }

  private static getType(type: FieldType): Type<FormComponent> {
    switch (type) {
      case FieldType.PARAGRAPH:
        return ParagraphComponent;
      case FieldType.RADIO:
        return RadioComponent;
      case FieldType.AUTOCOMPLETE:
        return AutocompleteComponent;
      case FieldType.READONLY:
        return ReadonlyComponent;
      case FieldType.DIVIDER:
        return DividerComponent;
      case FieldType.DATE:
        return DateComponent;
      case FieldType.INPUT:
        return InputComponent;
      case FieldType.FILE:
        return FileComponent;
      case FieldType.TEXTAREA:
        return TextareaComponent;
      case FieldType.HEADING:
        return HeadingComponent;
      case FieldType.HIDDEN:
        return HiddenComponent;
      case FieldType.HTML_EDITOR:
        return HtmlEditorComponent;
      case FieldType.SELECT:
        return SelectComponent;
      case FieldType.MEDEWERKER_GROEP:
        return MedewerkerGroepComponent;
      case FieldType.CHECKBOX:
        return CheckboxComponent;
      case FieldType.GOOGLEMAPS:
        return GoogleMapsComponent;
      case FieldType.TAAK_DOCUMENT_UPLOAD:
        return TaakDocumentUploadComponent;
      case FieldType.DOCUMENTEN_LIJST:
        return DocumentenLijstComponent;
      case FieldType.DOCUMENTEN_ONDERTEKENEN:
        return DocumentenOndertekenenComponent;
      case FieldType.MESSAGE:
        return MessageComponent;
      default:
        throw new Error(`Unknown type: '${type}'`);
    }
  }
}
