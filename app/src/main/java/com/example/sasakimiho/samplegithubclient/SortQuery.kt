package com.example.sasakimiho.samplegithubclient

enum class SortQuery(val query: String) {
    FORK("forks"),
    STAR("stars"),
    UPDATED("updates"),
    NONE("none");

    companion object {
        fun getAllQueries(): Array<String> = arrayOf(SortQuery.FORK.query, SortQuery.STAR.query,
                SortQuery.UPDATED.query, SortQuery.NONE.query)
    }
}
