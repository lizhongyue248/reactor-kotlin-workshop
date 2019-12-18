package io.eddumelendez.reactorkotlin

import io.eddumelendez.reactorkotlin.domain.User
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import java.lang.RuntimeException
import java.time.Duration
import java.util.function.Supplier

class Part03StepVerifier {

    @Test
    fun expectElementsThenComplete() {
        expectFooBarComplete(Flux.just("foo", "bar"))
    }

    /**
     * Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then completes successfully.
     */
    private fun expectFooBarComplete(flux: Flux<String>) {
        flux.test()
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete()
    }

    @Test
    fun expect2ElementsThenError() {
        expectFooBarError(Flux.just("foo", "bar").concatWith(Mono.error(RuntimeException())))
    }

    /**
     * Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements
     * then a RuntimeException error.
     */
    private fun expectFooBarError(flux: Flux<String>) {
        flux.test()
                .expectNext("foo")
                .expectNext("bar")
                .verifyError(RuntimeException::class.java)
    }

    @Test
    fun expectElementsWithThenComplete() {
        expectSkylerJesseComplete(Flux.just(User("swhite", null, null), User("jpinkman", null, null)))
    }

    /**
     * Use StepVerifier to check that the flux parameter emits a User with "swhite" username
     * and another one with "jpinkman" then completes successfully.
     */
    private fun expectSkylerJesseComplete(flux: Flux<User>) {
        flux.test()
                .expectNextMatches { it.username == "swhite" }
                .expectNextMatches { it.username == "jpinkman" }
                .verifyComplete()
    }

    @Test
    fun count() {
        expect10Elements(Flux.interval(Duration.ofSeconds(1)).take(10))
    }

    /**
     * Expect 10 elements then complete and notice how long it takes for running the test.
     */
    private fun expect10Elements(flux: Flux<Long>) {
        flux.test()
                .expectNextCount(10)
                .verifyComplete()
    }

    @Test
    fun countWithVirtualTime() {
        expect3600Elements(Supplier<Flux<Long>> { Flux.interval(Duration.ofSeconds(1)).take(3600) })
    }

    /**
     * Expect 3600 elements then complete using the virtual time capabilities provided via
     * StepVerifier.withVirtualTime() and notice how long it takes for running the test
     */
    private fun expect3600Elements(supplier: Supplier<Flux<Long>>) {
        StepVerifier.withVirtualTime(supplier)
                .thenAwait(Duration.ofSeconds(1))
                .expectNextCount(3600)
                .verifyComplete()
    }

}
