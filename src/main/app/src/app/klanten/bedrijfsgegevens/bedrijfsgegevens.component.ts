import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {KlantenService} from '../klanten.service';
import {SessionStorageService} from '../../shared/storage/session-storage.service';
import {Bedrijf} from '../model/bedrijven/bedrijf';

@Component({
    selector: 'zac-bedrijfsgegevens',
    templateUrl: './bedrijfsgegevens.component.html'
})
export class BedrijfsgegevensComponent implements OnInit, AfterViewInit {

    @Input() vestigingsnummer;
    @Output() delete = new EventEmitter<Bedrijf>();

    bedrijf: Bedrijf;
    klantExpanded: boolean = sessionStorage.getItem('klantExpanded') === 'true';
    viewInitialized = false;
    loading = true;

    constructor(private klantenService: KlantenService, public sessionStorageService: SessionStorageService) {
    }

    ngOnInit(): void {
        this.klantExpanded = this.sessionStorageService.getSessionStorage('klantExpanded', true);
        this.klantenService.readBedrijf(this.vestigingsnummer).subscribe(bedrijf => {
            this.bedrijf = bedrijf;
            this.loading = false;
        });
    }

    klantExpandedChanged($event: boolean): void {
        if (this.viewInitialized) {
            this.sessionStorageService.setSessionStorage('klantExpanded', $event ? 'true' : 'false');
        }
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
    }

}
