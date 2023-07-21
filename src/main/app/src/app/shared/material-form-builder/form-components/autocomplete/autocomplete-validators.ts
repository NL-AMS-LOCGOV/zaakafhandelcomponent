/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Observable, of } from "rxjs";
import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
  ValidatorFn,
} from "@angular/forms";
import { map } from "rxjs/operators";

export class AutocompleteValidators {
  static asyncOptionInList(options: Observable<any[]>): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors> => {
      if (!control.value) {
        return of(null);
      }

      return options.pipe(
        map((o) => {
          const find: any = o.find((option) =>
            AutocompleteValidators.equals(option, control.value),
          );
          return find ? null : { match: true };
        }),
      );
    };
  }

  static optionInList(options: any[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors => {
      if (!control.value) {
        return null;
      }
      const find: any = options.find((option) =>
        AutocompleteValidators.equals(option, control.value),
      );
      return find ? null : { match: true };
    };
  }

  static equals(object1: any, object2: any): boolean {
    if (typeof object1 === "string") {
      return object1 === object2;
    }
    if (object1 && object2) {
      if (object1.hasOwnProperty("key")) {
        return object1.key === object2.key;
      } else if (object1.hasOwnProperty("uuid")) {
        return object1.uuid === object2.uuid;
      } else if (object1.hasOwnProperty("identificatie")) {
        return object1.identificatie === object2.identificatie;
      } else if (object1.hasOwnProperty("id")) {
        return object1.id === object2.id;
      } else if (object1.hasOwnProperty("naam")) {
        return object1.naam === object2.naam;
      } else if (object1.hasOwnProperty("name")) {
        return object1.name === object2.name;
      }

      throw new Error("Er is geen property aanwezig om te kunnen vergelijken");
    }
    return false;
  }
}
