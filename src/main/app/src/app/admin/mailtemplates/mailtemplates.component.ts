/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {AdminComponent} from '../admin/admin.component';
import {MatSidenav, MatSidenavContainer} from '@angular/material/sidenav';
import {MatTableDataSource} from '@angular/material/table';
import {Mailtemplate} from '../model/mailtemplate';
import {ConfirmDialogComponent, ConfirmDialogData} from '../../shared/confirm-dialog/confirm-dialog.component';
import {IdentityService} from '../../identity/identity.service';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {UtilService} from '../../core/service/util.service';
import {MailtemplateBeheerService} from '../mailtemplate-beheer.service';
import {MailtemplateKoppelingService} from '../mailtemplate-koppeling.service';
import {MailtemplateKoppeling} from '../model/mailtemplate-koppeling';
import {forkJoin} from 'rxjs';

@Component({
    templateUrl: './mailtemplates.component.html',
    styleUrls: ['./mailtemplates.component.less']
})
export class MailtemplatesComponent extends AdminComponent implements OnInit, AfterViewInit {

    @ViewChild('sideNavContainer') sideNavContainer: MatSidenavContainer;
    @ViewChild('menuSidenav') menuSidenav: MatSidenav;

    isLoadingResults: boolean = false;
    columns: string[] = ['mailTemplateNaam', 'mail', 'defaultMailtemplate', 'id'];
    dataSource: MatTableDataSource<Mailtemplate> = new MatTableDataSource<Mailtemplate>();
    mailKoppelingen: MailtemplateKoppeling[];

    constructor(private identityService: IdentityService,
                private service: MailtemplateBeheerService,
                public dialog: MatDialog,
                private translate: TranslateService,
                public utilService: UtilService,
                private mailtemplateKoppelingService: MailtemplateKoppelingService) {
        super(utilService);
    }

    ngOnInit(): void {
        this.setupMenu('title.mailtemplates');
        this.laadMailtemplates();
    }

    laadMailtemplates(): void {
        this.isLoadingResults = true;
        forkJoin([
            this.service.listMailtemplates(),
            this.mailtemplateKoppelingService.listMailtemplateKoppelingen()
        ]).subscribe(([mailtemplates, koppelingen]) => {
            this.dataSource.data = mailtemplates;
            this.mailKoppelingen = koppelingen;
            this.isLoadingResults = false;
        });
    }

    isDisabled(mailtemplate: Mailtemplate): boolean {
        return this.getMailtemplateKoppeling(mailtemplate) != null;
    }

    getDisabledTitle(mailtemplate: Mailtemplate): string {
        return this.translate.instant('msg.mailtemplate.verwijderen.disabled',
            {zaaktype: this.getMailtemplateKoppeling(mailtemplate)
                    .zaakafhandelParameters.zaaktype.omschrijving});
    }

    verwijderMailtemplate(mailtemplate: Mailtemplate): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: new ConfirmDialogData(
                this.translate.instant('msg.mailtemplate.verwijderen.bevestigen'),
                this.service.deleteMailtemplate(mailtemplate.id)
            )
        }).afterClosed().subscribe(result => {
            if (result) {
                this.utilService.openSnackbar('msg.mailtemplate.verwijderen.uitgevoerd');
                this.laadMailtemplates();
            }
        });
    }

    private getMailtemplateKoppeling(mailtemplate: Mailtemplate) {
        return this.mailKoppelingen.find(koppeling => koppeling.mailtemplate.id === mailtemplate.id);
    }
}
