import {Component, Input} from '@angular/core';
import {EnkelvoudigInformatieobject} from '../../informatie-objecten/model/enkelvoudig-informatieobject';
import {FileIcon} from '../../informatie-objecten/model/file-icon';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'zac-document-icon',
    templateUrl: './document-icon.component.html',
    styleUrls: ['./document-icon.component.less']
})
export class DocumentIconComponent {

    @Input() document: EnkelvoudigInformatieobject;

    constructor(private translate: TranslateService) {
    }

    getFileIcon(filename) {
        return FileIcon.getIconByBestandsnaam(filename);
    }

    getFileTooltip(filetype: string): string {
        return this.translate.instant('bestandstype', {type: filetype.toUpperCase()});
    }

}
