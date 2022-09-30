package net.atos.zac.werklijst

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import input.user

werklijst_acties := {
    "documenten_inbox": documenten_inbox,
    "documenten_ontkoppeld": documenten_ontkoppeld,
    "documenten_ontkoppeld_verwijderen": documenten_ontkoppeld_verwijderen,
    "zaken_taken": zaken_taken,
    "zaken_taken_verdelen": zaken_taken_verdelen
}

default documenten_inbox := false
documenten_inbox {
    recordmanager.rol in user.rollen
}

default documenten_ontkoppeld := false
documenten_ontkoppeld {
    { behandelaar, recordmanager }[_].rol in user.rollen
}

default documenten_ontkoppeld_verwijderen := false
verdelen_en_vrijgeven {
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
