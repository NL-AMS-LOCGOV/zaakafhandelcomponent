/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.cdi.spi;

import java.util.ServiceLoader;

import org.flowable.idm.engine.IdmEngine;

/**
 * <p>
 * Represents a strategy for building or looking up a {@link org.flowable.idm.engine.IdmEngine}.
 * </p>
 *
 * <p>
 * Implementations of this class are looked up using the Java SE 6 {@link ServiceLoader} facilities.
 * Users of this class that provide a custom implementation, must declare it in a file named
 * <code>META-INF/services/org.flowable.cdi.spi.IdmEngineLookup</code> in order for it to be found.
 * </p>
 *
 * <p>
 * Each implementation declares a "precedence".
 * The precedence controls the order in which the resolved implementations will be invoked. (See: getPrecedence().)
 * Implementations with a higher precedence will be invoked first.
 * </p>
 *
 * @author Andy Verberne
 */
public interface IdmEngineLookup {

    /**
     * Determines the ordering in which implementations are invoked.
     * Implementations with a high ordering are invoked first.
     */
    int getPrecedence();

    /**
     * This method will only be called once by the {@link org.flowable.cdi.impl.FlowableIdmExtension}, at startup
     *
     * @return a {@link IdmEngine}
     */
    IdmEngine getIdmEngine();

    /**
     * This method will only be called once by the {@link org.flowable.cdi.impl.FlowableIdmExtension}, at shutdown
     */
    void ungetIdmEngine();
}
