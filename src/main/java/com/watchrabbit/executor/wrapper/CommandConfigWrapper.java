/*
 * Copyright 2014 Mariusz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package com.watchrabbit.executor.wrapper;

/**
 *
 * @author Mariusz
 */
public class CommandConfigWrapper {

    private boolean dedicatedThreadPool;

    private String commandName = "";

    public boolean isDedicatedThreadPool() {
        return dedicatedThreadPool;
    }

    public void setDedicatedThreadPool(boolean dedicatedThreadPool) {
        this.dedicatedThreadPool = dedicatedThreadPool;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public static class Builder {

        public Builder() {
            this.item = new CommandConfigWrapper();
        }

        private final CommandConfigWrapper item;

        public Builder withDedicatedThreadPool(final boolean dedicatedThreadPool) {
            this.item.dedicatedThreadPool = dedicatedThreadPool;
            return this;
        }

        public Builder withCommandName(final String commandName) {
            this.item.commandName = commandName;
            return this;
        }

        public CommandConfigWrapper build() {
            return this.item;
        }
    }

}
