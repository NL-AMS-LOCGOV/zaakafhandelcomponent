/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public final class ValidationUtil {

    /**
     * Valideert het opgegeven object
     * Gooit een {@link ConstraintViolationException} met daarin de fouten indien het object niet valide is
     *
     * @param object           het te valideren object
     * @param validationGroups de optionele validatie groepen
     */
    public static void valideerObject(final Object object, final Class<?>... validationGroups) {
        final Set<ConstraintViolation<Object>> violations = valideer(object, validationGroups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }

    private static Set<ConstraintViolation<Object>> valideer(final Object object, final Class<?>... validationGroups) {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        return validator.validate(object, validationGroups);
    }
}
