package net.atos.zac.taak

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.rol.rollen
import data.net.atos.zac.domein.ALLE_DOMEINEN
import input.user
import input.taak

taak_rechten := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "toekennen": toekennen,
}

default domein_allowed := false
domein_allowed {
    ALLE_DOMEINEN in user.rollen
}
domein_allowed {
    not taak.domein in rollen
    taak.domein in user.rollen
}

default lezen := false
lezen {
    { behandelaar, recordmanager }[_].rol in user.rollen
    domein_allowed == true
}

default wijzigen := false
wijzigen {
    { behandelaar, recordmanager }[_].rol in user.rollen
    domein_allowed == true
}

default toekennen := false
toekennen {
    behandelaar.rol in user.rollen
    domein_allowed == true
}
