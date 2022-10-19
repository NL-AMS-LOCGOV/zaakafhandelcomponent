package net.atos.zac.app.admin.model;

import java.util.List;

public record RESTFormulierDefinitie(
        String id,
        List<RESTFormulierVeldDefinitie> veldDefinities
) {}
