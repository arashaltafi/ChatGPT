package com.arash.altafi.chatgptsimple.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*


fun <T> flowIO(
    block: suspend FlowCollector<T>.() -> Unit,
) = flow { block() }
    .flowOn(Dispatchers.IO)
    .buffer()
    .cancellable()

fun <T> channelFlowIO(
    block: suspend ProducerScope<T>.() -> Unit,
) = channelFlow { block() }
    .flowOn(Dispatchers.IO)
    .buffer()
    .cancellable()

fun <T> flowCompute(
    block: suspend FlowCollector<T>.() -> Unit,
) = flow { block() }
    .flowOn(Dispatchers.Default)
    .buffer()
    .cancellable()

fun <T> flowMain(
    block: suspend FlowCollector<T>.() -> Unit,
) = flow { block() }
    .flowOn(Dispatchers.Main.immediate)
    .buffer()
    .cancellable()