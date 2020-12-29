package com.puuuuh.ingressmap.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class PortalData(
    var lat: Double,
    var lng: Double,
    var lvl: Int,
    var energy: Int,
    var pic: String,
    var name: String,
    var team: String,
    var specials: Set<String>,
    var mods: List<Mod>,
    var resonators: List<Resonator>,
    var owner: String,
) {
    constructor(
        lat: Double,
        lng: Double,
        lvl: Int,
        energy: Int,
        pic: String,
        name: String,
        team: String,
        specials: Set<String>) : this(lat, lng, lvl, energy, pic, name, team, specials, emptyList(), emptyList(), "")

    constructor(
        name: String,
        lat: Double,
        lng: Double
    ) : this(lat, lng, 0, 0, "", name, "", setOf())
}

class PortalDeserializer : JsonDeserializer<PortalData> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PortalData {
        val entityData = json.asJsonArray
        val team = entityData[1].asString
        val lat = entityData[2].asDouble / 1000000
        val lng = entityData[3].asDouble / 1000000
        val lvl = entityData[4].asInt

        val pic = if (entityData[7].isJsonNull) {
            ""
        } else {
            entityData[7].asString
        }
        val name = entityData[8].asString
        val specials = entityData[9].asJsonArray.map { it.asString }.toHashSet()
        if (entityData.size() > 14) {
            val rawModArr = entityData[14].asJsonArray
            val mods = rawModArr.map {
                context.deserialize<Mod>(it, Mod::class.java)
            }

            val rawResonatorArr = entityData[15].asJsonArray
            val resonators = rawResonatorArr.map {
                context.deserialize<Resonator>(it, Resonator::class.java)
            }

            val energy = resonators.sumBy { it.energy }

            val owner = entityData[16].asString
            return PortalData(lat, lng, lvl, energy, pic, name, team, specials, mods, resonators, owner)
        }

        return PortalData(lat, lng, lvl, 0, pic, name, team, specials)
    }

}