package me.nikitaklimkin.rest.plugin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


fun Application.configureSerialization() {
//    install(ContentNegotiation) {
//        json(Json {
//            serializersModule = SerializersModule {
//                polymorphic(List::class) {
//                    val it: KSerializer<ArrayList<Any>> = ArrayListSerializer(PolymorphicSerializer(Any::class))
//                    subclass(ArrayList::class, it)
//                }
//            }
//        })
//    }
    install(ContentNegotiation) {
        json()
    }
}