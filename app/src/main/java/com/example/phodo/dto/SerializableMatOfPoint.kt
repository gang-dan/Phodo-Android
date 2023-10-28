package com.example.phodo.dto

import kotlinx.serialization.Serializable

@Serializable
data class SerializableMatOfPoint(val points: List<SerializablePoint>)