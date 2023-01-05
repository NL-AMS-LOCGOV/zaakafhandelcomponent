package net.atos.zac.zaak

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.rol.rollen
import data.net.atos.zac.domein.ALLE_DOMEINEN
import input.zaak
import input.user

zaak_rechten := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "toekennen": toekennen,
    "behandelen": behandelen,
    "afbreken": afbreken,
    "heropenen": heropenen,
    "wijzigenDoorlooptijd": wijzigenDoorlooptijd
}

default domein_allowed := false
domein_allowed {
    ALLE_DOMEINEN in user.rollen
}
domein_allowed {
    not zaak.domein in rollen
    zaak.domein in user.rollen
}

default lezen := false
lezen {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    domein_allowed == true
}

default wijzigen := false
wijzigen {
    behandelaar.rol in user.rollen
    domein_allowed == true
    zaak.open == true
}
wijzigen {
    recordmanager.rol in user.rollen
    domein_allowed == true
}

default toekennen := false
toekennen {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    domein_allowed == true
}

default behandelen := false
behandelen {
    behandelaar.rol in user.rollen
    domein_allowed == true
}

default afbreken := false
afbreken {
    { behandelaar, recordmanager }[_].rol in user.rollen
    domein_allowed == true
}

default heropenen := false
heropenen {
    recordmanager.rol in user.rollen
    domein_allowed == true
}

default wijzigenDoorlooptijd := false
wijzigenDoorlooptijd {
    recordmanager.rol in user.rollen
    domein_allowed == true
}
