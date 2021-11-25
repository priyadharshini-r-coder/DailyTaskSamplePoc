package com.example.dailytasksamplepoc.kotlinomnicure.media



interface PackableEx : Packable {
    fun unmarshal(`in`: ByteBuf?)
}