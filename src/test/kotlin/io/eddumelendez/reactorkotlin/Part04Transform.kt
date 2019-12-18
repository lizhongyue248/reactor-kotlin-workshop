package io.eddumelendez.reactorkotlin

import io.eddumelendez.reactorkotlin.domain.User
import io.eddumelendez.reactorkotlin.repository.ReactiveUserRepository
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class Part04Transform {

    var repository = ReactiveUserRepository()

    @Test
    fun transformMono() {
        val mono = repository.findFirst()

        capitalizeOne(mono).test()
                .expectNext(User("SWHITE", "SKYLER", "WHITE"))
                .verifyComplete()
    }

    /**
     * Capitalize the user username, firstname and lastname
     */
    private fun capitalizeOne(mono: Mono<User>): Mono<User> {
        return mono.map { User(it.username.toUpperCase(), it.firstname?.toUpperCase(), it.lastname?.toUpperCase()) }
    }

    @Test
    fun transformFlux() {
        val flux = repository.findAll()

        capitalizeMany(flux).test()
                .expectNext(
                        User("SWHITE", "SKYLER", "WHITE"),
                        User("JPINKMAN", "JESSE", "PINKMAN"),
                        User("WWHITE", "WALTER", "WHITE"),
                        User("SGOODMAN", "SAUL", "GOODMAN"))
                .verifyComplete()
    }

    /**
     * Capitalize the users username, firstName and lastName
     */
    private fun capitalizeMany(flux: Flux<User>): Flux<User> {
        return flux.map { User(it.username.toUpperCase(), it.firstname?.toUpperCase(), it.lastname?.toUpperCase()) }
    }

    @Test
    fun asyncTransformFlux() {
        val flux = repository.findAll()

        asyncCapitalizeMany(flux).test()
                .expectNext(
                        User("SWHITE", "SKYLER", "WHITE"),
                        User("JPINKMAN", "JESSE", "PINKMAN"),
                        User("WWHITE", "WALTER", "WHITE"),
                        User("SGOODMAN", "SAUL", "GOODMAN"))
                .verifyComplete()
    }

    /**
     *  Capitalize the users username, firstName and lastName using asyncCapitalizeUser()
     */
    private fun asyncCapitalizeMany(flux: Flux<User>): Flux<User> {
        return flux.flatMap { asyncCapitalizeUser(it) }
    }

    private fun asyncCapitalizeUser(user: User): Mono<User> {
        return User(user.username.toUpperCase(), user.firstname?.toUpperCase(), user.lastname?.toUpperCase()).toMono()
    }

}
