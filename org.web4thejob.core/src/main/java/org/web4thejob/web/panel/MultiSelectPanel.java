/*
 * Copyright (c) 2012-2014 Veniamin Isaias.
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

package org.web4thejob.web.panel;

import org.web4thejob.message.MessageAware;

import java.util.List;

/**
 * <p>Defines the api of a content panel for building a target list out of one or more items of a source list.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface MultiSelectPanel<S, T> extends ContentPanel, MessageAware, TargetTypeAware {

    public void deselect(T target);

    public void deselectAll();

    public List<T> getSelection();

    public T select(S source);

    public void selectAll();

    public void setSourceList(List<S> list);

    public void setTargetList(List<T> list);

}
