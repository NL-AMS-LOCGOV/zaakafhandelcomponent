import * as moment from 'moment/moment';

export class ClientMatcher {
    static matchDatum(dataField: string, filterField: { van: string, tot: string }): boolean {
        if (!dataField) {
            return false;
        }
        const inputDate: moment.Moment = moment(dataField);

        if (!!filterField.van && !!filterField.tot) {
            return inputDate.isSameOrAfter(moment(filterField.van)) && inputDate.isSameOrBefore(moment(filterField.tot));
        } else if (!!filterField.van) {
            return inputDate.isSameOrAfter(moment(filterField.van));
        } else if (!!filterField.tot) {
            return inputDate.isSameOrBefore(moment(filterField.tot));
        }
        return false;
    }

    static matchBoolean(dataField: boolean, filterField: boolean): boolean {
        return dataField === filterField;
    }
}


