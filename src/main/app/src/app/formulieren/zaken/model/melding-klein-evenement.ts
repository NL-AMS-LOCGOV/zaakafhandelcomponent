import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { CustomValidators } from "../../../shared/validators/customValidators";
import { AbstractFormField } from "../../../shared/material-form-builder/model/abstract-form-field";
import { DateFormFieldBuilder } from "../../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { AbstractZaakFormulier } from "../abstract-zaak-formulier";
import { TranslateService } from "@ngx-translate/core";
import { TextareaFormFieldBuilder } from "../../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { HeadingFormFieldBuilder } from "../../../shared/material-form-builder/form-components/heading/heading-form-field-builder";
import { HeadingLevel } from "../../../shared/material-form-builder/form-components/heading/heading-form-field";

export class MeldingKleinEvenement extends AbstractZaakFormulier {
  fields = {
    NAAMEVENEMENT: "naamEvenement",
    OMSCHRIJVINGEVENEMENT: "omschrijvingEvenement",
    DATUMEVENEMENT: "datumEvenement",
    VERWACHTAANTALDEELNEMERS: "verwachtAantalDeelnemers",
    VOORNAMEN: "voornamen",
    ACHTERNAAM: "achternaam",
    TELEFOONNUMMER: "telefoonnummer",
    EMAILADRES: "emailadres",
  };

  form: Array<AbstractFormField[]> = [];

  constructor(translate: TranslateService) {
    super(translate);
  }

  _initForm(): void {
    const fields = this.fields;
    this.form.push(
      [
        new HeadingFormFieldBuilder()
          .id("melding_klein_evenement")
          .label(this.translate.instant("melding_klein_evenement.evenement"))
          .level(HeadingLevel.H4)
          .build(),
      ],
      [
        new InputFormFieldBuilder(this.getDataElement(fields.NAAMEVENEMENT))
          .id(fields.NAAMEVENEMENT)
          .label(fields.NAAMEVENEMENT)
          .build(),
      ],
      [
        new TextareaFormFieldBuilder(
          this.getDataElement(fields.OMSCHRIJVINGEVENEMENT),
        )
          .id(fields.OMSCHRIJVINGEVENEMENT)
          .label(fields.OMSCHRIJVINGEVENEMENT)
          .build(),
      ],
      [
        new DateFormFieldBuilder(this.getDataElement(fields.DATUMEVENEMENT))
          .id(fields.DATUMEVENEMENT)
          .label(fields.DATUMEVENEMENT)
          .build(),
      ],
      [
        new InputFormFieldBuilder(
          this.getDataElement(fields.VERWACHTAANTALDEELNEMERS),
        )
          .id(fields.VERWACHTAANTALDEELNEMERS)
          .label(fields.VERWACHTAANTALDEELNEMERS)
          .build(),
      ],
      [
        new HeadingFormFieldBuilder()
          .id("melding_klein_evenement")
          .label(
            this.translate.instant(
              "melding_klein_evenement.aanvrager.gegevens",
            ),
          )
          .level(HeadingLevel.H3)
          .build(),
      ],
      [
        new InputFormFieldBuilder(this.getDataElement(fields.VOORNAMEN))
          .id(fields.VOORNAMEN)
          .label(fields.VOORNAMEN)
          .build(),
      ],
      [
        new InputFormFieldBuilder(this.getDataElement(fields.ACHTERNAAM))
          .id(fields.ACHTERNAAM)
          .label(fields.ACHTERNAAM)
          .build(),
      ],
      [
        new InputFormFieldBuilder(this.getDataElement(fields.TELEFOONNUMMER))
          .id(fields.TELEFOONNUMMER)
          .label(fields.TELEFOONNUMMER)
          .build(),
      ],
      [
        new InputFormFieldBuilder(this.getDataElement(fields.EMAILADRES))
          .id(fields.EMAILADRES)
          .label(fields.EMAILADRES)
          .validators(CustomValidators.email)
          .build(),
      ],
    );
  }
}
