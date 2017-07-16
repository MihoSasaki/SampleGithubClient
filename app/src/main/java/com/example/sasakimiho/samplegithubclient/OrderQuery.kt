package com.example.sasakimiho.samplegithubclient

enum class OrderQuery(val query: String) {
    ASC("asc"),
    DESC("desc"),
    NONE("none");

    companion object {
        fun getAllQueries(): Array<String> = arrayOf(OrderQuery.ASC.query, OrderQuery.DESC.query,
                OrderQuery.NONE.query)
    }
}
