package com.example.sasakimiho.samplegithubclient

data class Page<out ITEM>(val totalCount: Long,
                          val items: List<ITEM>)
