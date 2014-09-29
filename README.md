[![Maven central][maven img]][maven]
[![][travis img]][travis]
[![][coverage img]][coverage]

Watchrabbit - Executor
======================

Executor is a latency and fault tolerance library. Designed to manage and isolate access points of remote systems, services and libraries can stop cascading failure and increase performance. Executor implements [Circuit Breaker](http://martinfowler.com/bliki/CircuitBreaker.html) pattern with several useful improvements. 

## Current release
29/09/2014 rabbit-executor **1.1.1** released! Should appear in maven central shortly.

## Download and install
```
<dependency>
  <groupId>com.watchrabbit</groupId>
  <artifactId>rabbit-executor</artifactId>
  <version>1.1.1</version>
</dependency>
```

Executions
----------

Executor supports three diffrent types of command execution: synchronous, asynchronous and callback. Each one supports each feature described in usage section, such as circuit-breaker, thread pooling or cacheing.

## Synchronous 

To invoke some command synchronously write:
```java
public class Foo {

    public void bar() throws ExecutionException {
        V returnedValue = executor("foo-system")
                .invoke(()
                        -> // do something in foo-system
                            ...
                );
    }
}

```
"`foo-system`" is a name of remote system accessed in callback method. When there is more then one access point to remote system each one access point should create executor using `executor(foo-system)` to provide common circuit breaker for whole remote system.


## Asynchronous
To execute some command asynchronously instead of `invoke` use `queue`.
```java
public class Foo {

    public void bar() {
        Future<V> returned = executor("foo-system")
                .queue(()
                        -> // do something in foo-system
                            ...
                );
    }
}
```
Invoke methods immediately returns with `Feature`, and executes callback asynchronously.

## Callback
Executor supports also callback mode. To run callback in this mode use `observe`, with successCallback as required parameter and errorCallback as optional parameter. 
```java
public class Foo {

    public void bar() {
          executor("foo-system")
                .observe(() -> { // do something in foo-system
                            ...
                        }, (returnedValue) -> { // this success callback method consumes 
                                                // value returned by callback
                            ...
                        }, (exception) -> { // this error callback method consumes 
                                            // exception thrown by callback
                            ...
                        }
                );
    }
}
```

Usage
-----
Each one of Executor features provides few customization options and introduces some default values.

## Circuit breaker
Circuit breakers are created by `circuitName` provided in executor creation. When a executor with `foo` circuit name opens each other executor with name `foo` opens. Open circuit skips command execution and throws `CircuitOpenException`. When `breakerRetryTimeout` elapses breaker closes circuit. Value `-1` disables this feature. Default value of `breakerRetryTimeout` is 100 miliseconds.

## Errors
Errors processing depends on execution method. Method `invoke` throws `ExecutionException` wrapping exception thrown by command. Method `queue` throws exception thrown by command wrapped in `ExecutionException` when `get` method is called on returned `Future<V>`. Finally, `observe` method with `onSuccess` callback suppress exception, and in variant with error callback passes exception to `onFailure` method.

## Fail silent
Fail silent mode suppress exceptions thrown by invoke method. To enable silent mode just before invoke method use `silentFailMode`.

## Request cache
Executor supports callback results caching. To enable this feature configure cache via `withCache` method. 
```java
public class Foo {

    public void bar() throws ExecutionException {
        V returnedValue = executor("foo-system")
                .withCache(
                        cache("cacheName", "key")
                )
                .invoke(()
                        -> // do something in foo-system
                            ...
                );
    }
}

```
Method `cache` takes cache name and key. Cache name should be unique for each command. Key is used to store returned value in cache. Method `withCacheSize` in cache builder configures size of cache map,  and `withExpireTime` method sets timeout - when this timeout elapses result should be removed from cache.

## Request retries

## Request batching - todo


[coverage]:https://coveralls.io/r/watchrabbit/rabbit-executor
[coverage img]:https://img.shields.io/coveralls/watchrabbit/rabbit-executor.png
[travis]:https://travis-ci.org/watchrabbit/rabbit-executor
[travis img]:https://travis-ci.org/watchrabbit/rabbit-executor.svg?branch=master
[maven]:https://maven-badges.herokuapp.com/maven-central/com.watchrabbit/rabbit-executor
[maven img]:https://maven-badges.herokuapp.com/maven-central/com.watchrabbit/rabbit-executor/badge.svg
