package com.example.sasakimiho.samplegithubclient


class Repository(val id: Long,
                 val fullName: String,
                 val description: String,
                 val htmlUrl: String,
                 val starganzersCount: Int,
                 val owner: User,
                 val language: String?,
                 val score: Float)