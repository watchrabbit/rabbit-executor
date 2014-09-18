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

import com.watchrabbit.commons.callback.CheckedConsumer;
import java.util.concurrent.Callable;

/**
 *
 * @author Mariusz
 */
public class CallbackInvokerWrapper<V> {

    private Callable<V> callable;

    private CheckedConsumer<V> successConsumer;

    private CheckedConsumer<Exception> errorConsumer;

    public Callable<V> getCallable() {
        return callable;
    }

    public void setCallable(Callable<V> callable) {
        this.callable = callable;
    }

    public CheckedConsumer<V> getSuccessConsumer() {
        return successConsumer;
    }

    public void setSuccessConsumer(CheckedConsumer<V> successConsumer) {
        this.successConsumer = successConsumer;
    }

    public CheckedConsumer<Exception> getErrorConsumer() {
        return errorConsumer;
    }

    public void setErrorConsumer(CheckedConsumer<Exception> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public static class Builder<V> {

        public Builder() {
            this.item = new CallbackInvokerWrapper();
        }

        private final CallbackInvokerWrapper item;

        public Builder withCallable(final Callable<V> callable) {
            this.item.callable = callable;
            return this;
        }

        public Builder withSuccessConsumer(final CheckedConsumer<V> successConsumer) {
            this.item.successConsumer = successConsumer;
            return this;
        }

        public Builder withErrorConsumer(final CheckedConsumer<Exception> errorConsumer) {
            this.item.errorConsumer = errorConsumer;
            return this;
        }

        public CallbackInvokerWrapper build() {
            return this.item;
        }
    }

}
