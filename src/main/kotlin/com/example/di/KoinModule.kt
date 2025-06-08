package com.example.di

import com.example.domain.repository.SignUpRepository
import com.example.domain.repository.TokenRepository
import com.example.repository.Repository
import com.example.repository.SignUpRepositoryImpl
import com.example.repository.TokenRepositoryImpl
import com.example.security.hashing.HashingServices
import com.example.security.hashing.HashingServicesImpl
import com.example.security.token.JwtServices
import io.ktor.server.application.*
import org.koin.dsl.module

val userModule = { application: Application ->
    module {
        single<SignUpRepository> { SignUpRepositoryImpl() }
        single<HashingServices> { HashingServicesImpl() }
        single { JwtServices(application, get<SignUpRepository>()) }
        single<TokenRepository> { TokenRepositoryImpl() }

        factory {
            Repository(
                get<SignUpRepository>(),
                get<HashingServices>(),
                get<JwtServices>(),
                get<TokenRepository>()
            )
        }
    }
}