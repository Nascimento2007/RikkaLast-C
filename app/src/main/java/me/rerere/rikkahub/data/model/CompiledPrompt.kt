package me.rere.rikkahub.data.model

data class CompiledPrompt(
    val systemPrompt: String,
    val tokenEstimate: Int
)
