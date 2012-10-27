/*
 * Copyright (c) 2012 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.orm;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>Service for writing {@link Entity} instances to the application datastore. Usually invoked through
 * {@link org.web4thejob.context.ContextUtil#getDWS() ContextUtil.getDWS()}.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Transactional
public interface DataWriterService {

    public <E extends Entity> void delete(E entity);

    public <E extends Entity> void save(E entity);

    public <E extends Entity> void save(List<E> entities);
}
