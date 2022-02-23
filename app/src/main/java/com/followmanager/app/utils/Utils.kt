package com.followmanager.app.utils

import com.followmanager.app.api.User
import com.google.gson.Gson

object Utils {

    fun mapToJson(map: Map<String, User>): String {
        var gson = Gson()
        return gson.toJson(mapToArray(map))
    }

    fun jsonToMap(json: String?): Map<String, User>? {
        if (json == null) return null

        var gson = Gson()

        val array = gson.fromJson(json, Array<User>::class.java) ?: return null

        var map = mutableMapOf<String, User>()
        for (value in array) {
            map[value.pk] = value
        }

        return map.toMap()
    }

    fun mapToArray(map: Map<String, User>): Array<User> {
        var array: Array<User> =
            Array<User>(map.size) { i -> User("", "", "", "", "", 0, 0, false, false) }

        var i = 0
        for (v in map.values) {
            array[i] = v
            i++
        }

        return array
    }

    fun userToJson(user: User): String {
        var gson = Gson()

        return gson.toJson(user)
    }

    fun jsonToUser(json: String): User {
        var gson = Gson()
        return gson.fromJson(json, User::class.java)
    }

    // Show K if number is higher than 1000
    fun formatNumber(number: Int): String {
        if (number > 1000) {
            return "${number / 1000}K"
        } else {
            return number.toString()
        }
    }
}