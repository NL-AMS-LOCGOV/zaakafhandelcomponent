package net.atos.zac.werklijst

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import input.user

werklijst_rechten := {
    "inbox": inbox,
    "ontkoppelde_documenten_verwijderen": ontkoppelde_documenten_verwijderen,
    "inbox_productaanvragen_verwijderen": inbox_productaanvragen_verwijderen,
    "zaken_taken": zaken_taken,
    "zaken_taken_verdelen": zaken_taken_verdelen
}

default inbox := false
inbox {
    { behandelaar, recordmanager }[_].rol in user.rollen
}

default ontkoppelde_documenten_verwijderen := false
ontkoppelde_documenten_verwijderen {
    recordmanager.rol in user.rollen
}

default inbox_productaanvragen_verwijderen := false
inbox_productaanvragen_verwijderen {
    recordmanager.rol in user.rollen
}

default zaken_taken := false
zaken_taken {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
}

default zaken_taken_verdelen := false
zaken_taken_verdelen {
    coordinator.rol in user.rollen
}
