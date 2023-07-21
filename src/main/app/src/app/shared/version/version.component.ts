import { Component, Input, OnInit } from "@angular/core";
import { BuildInformatie } from "../../admin/model/build-informatie";
import { HealthCheckService } from "../../admin/health-check.service";

export enum VersionLayout {
  VERBOSE = "VERBOSE",
  NORMAL = "NORMAL",
}

@Component({
  selector: "zac-version",
  templateUrl: "./version.component.html",
  styleUrls: ["./version.component.less"],
})
export class VersionComponent implements OnInit {
  versionLayout = VersionLayout;
  @Input() layout: VersionLayout;
  buildInformatie: BuildInformatie = new BuildInformatie();

  constructor(private healtCheckService: HealthCheckService) {}

  ngOnInit(): void {
    this.healtCheckService
      .readBuildInformatie()
      .subscribe((buildInformatie) => {
        this.buildInformatie = buildInformatie;
      });
  }
}
