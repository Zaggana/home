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

package org.web4thejob.command;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.security.SecuredResource;
import org.web4thejob.util.L10nUtil;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractCommand implements Command {
    private final CommandEnum id;
    private final CommandAware owner;
    private Map<String, Object> args;
    private Message stateCache; //may be this should be a map in order to store multiple messages
    private final Set<MessageAware> listeners = new LinkedHashSet<MessageAware>();
    private Object value;
    private boolean active = false;
    private boolean registered = false;

    private static String toLocalizedName(CommandEnum id) {
        return L10nUtil.getMessage(id.getClass(), id.name(), id.name());
    }

    @Override
    public String getName() {
        return toLocalizedName(id);
    }

    protected AbstractCommand(CommandEnum id, CommandAware owner) {
        this.id = id;
        this.owner = owner;
    }

    @Override
    public int compareTo(Command o) {
        return id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        return obj instanceof Command && hashCode() == obj.hashCode();

    }

    @Override
    public CommandEnum getId() {
        return id;
    }

    @Override
    public CommandAware getOwner() {
        return owner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (owner == null ? 0 : owner.hashCode());
        return result;
    }

    @Override
    public void process() throws CommandProcessingException {
        if (isActive()) {
            owner.process(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (owner != null) {
            sb.append(owner.toString()).append("\\");
        }
        sb.append(getName());
        return sb.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // serves as a convenient pointcut for various purposes (e.g.
        // security)
    }

    @Override
    public boolean addMessageListener(MessageAware messageAware) {
        if (listeners.add(messageAware)) {
            if (stateCache != null) {
                messageAware.dispatchMessage(stateCache);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getSid() {
        StringBuilder sb = new StringBuilder();
        if (owner instanceof SecuredResource) {
            sb.append(((SecuredResource) owner).getSid()).append(SecuredResource.SECURITY_PATH_DELIM);
        }
        sb.append(Command.class.getCanonicalName()).append(".").append(getId().name());
        return sb.toString();
    }

    @Override
    public void dispatchMessage(Message message) {
        stateCache = message;
        // we need new LinkedHashSet to solve ConcurrentModificationException
        for (final MessageAware messageDispatcher : new LinkedHashSet<MessageAware>(listeners)) {
            messageDispatcher.dispatchMessage(message);
        }
    }

    @Override
    public boolean removeMessageListener(MessageAware messageAware) {
        return listeners.remove(messageAware);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActivated(boolean active) {
        if (this.active != active) {
            this.active = active;

            if (this.active) {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.ACTIVATED, this));
            } else {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.DEACTIVATED, this));
            }


/*
            for (CommandEnum subid : id.getSubcommands()) {
                Command subcommand = owner.getCommand(subid);
                if (subcommand != null) {
                    if (subcommand.isActive() && !this.active) {
                        subcommand.setActivated(this.active);
                    }
                }
            }
*/
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        dispatchMessage(ContextUtil.getMessage(MessageEnum.HIGHLIGHT, this, MessageArgEnum.ARG_ITEM, highlighted));
    }

    @Override
    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value; //always apply new value since a change could reside in attributes attached to value (e.g
        // . value is an Entity)
        if (!new EqualsBuilder().append(oldValue, value).isEquals()) {
            dispatchMessage(ContextUtil.getMessage(MessageEnum.VALUE_CHANGED, this));
        }
    }

    @Override
    public void removeArgs() {
        if (args != null) {
            args = null;
        }
    }

    @Override
    public <T> void setArg(String key, T value) {
        if (args == null) {
            args = new HashMap<String, Object>(1);
        }
        args.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeArg(String key) {
        if (args != null) {
            return (T) args.remove(key);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getArg(String key, Class<T> clazz) {
        if (args != null) {
            return (T) args.get(key);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasArg(String key) {
        return args != null && args.containsKey(key);
    }

    @Override
    public void processMessage(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRegistered() {
        return registered;
    }

    @Override
    public void setRegistered(boolean registered) {
        if (this.registered != registered) {
            this.registered = registered;

            if (this.registered) {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.AFTER_ADD, this));
            } else {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.AFTER_REMOVE, this));
            }
        }
    }

    @Override
    public Set<MessageAware> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }

}
