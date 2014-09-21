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

import com.watchrabbit.commons.clock.Clock;
import com.watchrabbit.commons.clock.SystemClock;
import java.time.Instant;

/**
 *
 * @author Mariusz
 */
public class CircutBreaker {

    private static final Long RETRY_TIMEOUT = 10000l;

    private final Clock clock = SystemClock.getInstance();

    private String commandName;

    private boolean closed = true;

    private Instant instant;

    public CircutBreaker(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public boolean isClosed() {
        if (!closed) {
            closed = instant.plusMillis(RETRY_TIMEOUT)
                    .isBefore(clock.getInstant());
        }
        return closed;
    }

    public void open() {
        this.instant = clock.getInstant();
        this.closed = false;
    }

}
