import * as moment from "moment/moment";
import { DatumRange } from "../../../zoeken/model/datum-range";

export class ClientMatcher {
  static matchDatum(dataField: string, filterField: DatumRange): boolean {
    if (!dataField) {
      return false;
    }
    const inputDate: moment.Moment = moment(dataField);

    if (!!filterField.van && !!filterField.tot) {
      return (
        inputDate.isSameOrAfter(moment(filterField.van)) &&
        inputDate.isSameOrBefore(moment(filterField.tot))
      );
    } else if (filterField.van) {
      return inputDate.isSameOrAfter(moment(filterField.van));
    } else if (filterField.tot) {
      return inputDate.isSameOrBefore(moment(filterField.tot));
    }
    return false;
  }

  static matchBoolean(dataField: boolean, filterField: boolean): boolean {
    return dataField === filterField;
  }

  static matchObject<T>(dataField: T, filterField: T, key: string): boolean {
    return dataField && dataField[key] === filterField[key];
  }
}
