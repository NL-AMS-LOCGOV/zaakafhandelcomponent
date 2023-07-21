/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, Input, OnInit, ViewChild } from "@angular/core";
import { Notitie } from "./model/notitie";
import { User } from "../identity/model/user";
import { IdentityService } from "../identity/identity.service";
import { NotitieService } from "./notities.service";

@Component({
  selector: "zac-notities",
  templateUrl: "./notities.component.html",
  styleUrls: ["./notities.component.less"],
})
export class NotitiesComponent implements OnInit {
  @Input() uuid: string;
  @Input() type: string;

  @ViewChild("notitieTekst") notitieTekst;

  ingelogdeMedewerker: User;

  aantalNotities = 0;
  laatNotitiesSchermZien = true;
  geselecteerdeNotitieId: number;
  notities: Notitie[] = [];
  maxLengteTextArea = 1000;

  constructor(
    private identityService: IdentityService,
    private notitieService: NotitieService,
  ) {}

  ngOnInit(): void {
    this.identityService.readLoggedInUser().subscribe((ingelogdeMedewerker) => {
      this.ingelogdeMedewerker = ingelogdeMedewerker;
    });
    this.haalNotitiesOp();
  }

  toggleNotitieContainer() {
    this.laatNotitiesSchermZien = !this.laatNotitiesSchermZien;
  }

  pasNotitieAan(id: number) {
    this.geselecteerdeNotitieId = id;
  }

  haalNotitiesOp() {
    this.notitieService
      .listNotities(this.type, this.uuid)
      .subscribe((notities) => {
        this.notities = notities;
        this.notities
          .sort((a, b) =>
            a.tijdstipLaatsteWijziging.localeCompare(
              b.tijdstipLaatsteWijziging,
            ),
          )
          .reverse();
        this.aantalNotities = this.notities.length;
      });
  }

  maakNotitieAan(tekst: string) {
    if (tekst.length <= this.maxLengteTextArea) {
      const notitie: Notitie = new Notitie();
      notitie.zaakUUID = this.uuid;
      notitie.tekst = tekst;
      notitie.gebruikersnaamMedewerker = this.ingelogdeMedewerker.id;

      this.notitieService.createNotitie(notitie).subscribe((notitie) => {
        this.notities.splice(0, 0, notitie);
        this.notitieTekst.nativeElement.value = "";
        this.aantalNotities = this.notities.length;
      });
    }
  }

  updateNotitie(notitie: Notitie, notitieTekst: string) {
    if (notitieTekst.length <= this.maxLengteTextArea) {
      notitie.tekst = notitieTekst;
      this.notitieService.updateNotitie(notitie).subscribe(() => {
        this.geselecteerdeNotitieId = null;
      });
    }
  }

  annuleerUpdateNotitie() {
    this.notitieTekst.nativeElement.value = "";
    this.geselecteerdeNotitieId = null;
  }

  verwijderNotitie(id: number) {
    this.notitieService.deleteNotitie(id).subscribe(() => {
      this.notities.splice(
        this.notities.findIndex((n) => n.id === id),
        1,
      );
      this.aantalNotities = this.notities.length;
    });
  }
}
