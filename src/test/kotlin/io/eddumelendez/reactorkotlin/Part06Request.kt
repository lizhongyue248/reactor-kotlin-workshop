package io.eddumelendez.reactorkotlin

import io.eddumelendez.reactorkotlin.domain.User
import io.eddumelendez.reactorkotlin.repository.ReactiveUserRepository
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.kotlin.test.test

class Part06Request {

    val repository = ReactiveUserRepository()

    @Test
    fun requestAll() {
        val flux = repository.findAll()

        val verifier = requestAllExpectFour(flux)
        verifier.verify()
    }

    /**
     * Create a StepVerifier that requests initially all values and expect a 4 values to be received
     */
    private fun requestAllExpectFour(flux: Flux<User>): StepVerifier {
        return StepVerifier.create(flux)
                .expectNextCount(4)
                .expectComplete()
    }

    @Test
    fun requestOneByOne() {
        val flux = repository.findAll()

        val verifier = requestOneExpectSkylerThenRequestOneExpectJesse(flux)
        verifier.verify()
    }

    /**
     * Create a StepVerifier that requests initially 1 value and expects {@link User.SKYLER}
     * then requests another value and expects {@link User.JESSE}.
     */
    private fun requestOneExpectSkylerThenRequestOneExpectJesse(flux: Flux<User>): StepVerifier {
        return StepVerifier.create(flux, 1)
                .expectNext(User.SKYLER)
                .thenRequest(1)
                .expectNext(User.JESSE)
                .thenCancel()
    }

    @Test
    fun experimentWithLog() {
        val flux = fluxWithLog()

        flux.test(0)
                .thenRequest(1)
                .expectNextMatches({ true })
                .thenRequest(1)
                .expectNextMatches({ true })
                .thenRequest(2)
                .expectNextMatches({ true })
                .expectNextMatches({ true })
                .verifyComplete()
    }

    /**
     * Return a Flux with all users stored in the repository that prints
     * automatically logs for all Reactive Streams signals
     */
    private fun fluxWithLog(): Flux<User> {
        return repository.findAll().log()
    }

    @Test
    fun experimentWithDoOn() {
        val flux = fluxWithDoOnPrintln()

        flux.test()
                .expectNextCount(4)
                .verifyComplete()
    }

    /**
     * Return a Flux with all users stored in the repository that prints
     * "Starring:" on subscribe, "firstname lastname" for all values and "The end!" on complete
     */
    fun fluxWithDoOnPrintln(): Flux<User> {
        return repository.findAll()
                .doOnSubscribe { println("Starring:") }
                .doOnNext { println(it.firstname + " " + it.lastname) }
                .doOnComplete { println("The end!") }
    }

}
