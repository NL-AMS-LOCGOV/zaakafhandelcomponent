package net.atos.zac.app

import future.keywords
import input.zaak
import input.user
import data.net.atos.zac.rol.beheerder

app_acties := {
    "aanmaken_zaak": aanmaken_zaak,
    "beheren": beheren,
    "zoeken": zoeken,
    "zaken": zaken,
    "taken": taken,
    "documenten": documenten
}

default aanmaken_zaak := true

default beheren:= false
beheren {
    beheerder.rol in user.rollen
}

default zoeken := true

default zaken := true

default taken := true

default documenten := true
