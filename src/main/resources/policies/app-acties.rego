package net.atos.zac

import future.keywords
import input.zaak
import input.user
import data.net.atos.zac.rollen

app_acties := {
    "aanmaken_zaak": aanmaken_zaak,
    "beheren": beheren,
}

default aanmaken_zaak := true

default beheren:= false
beheren {
    rollen.beheerder.id in user.rollen
}
