package de.visualdigits.makemkvconui.domain.model.kodi

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class TVShow(
    @XmlSerialName("title") @XmlElement(true) val title: String,
    @XmlSerialName("plot") @XmlElement(true) val plot: String,
    @XmlSerialName("uniqueid") @XmlElement(true) val uniqueId: UniqueId,
    @XmlSerialName("genre") @XmlElement(true) val genre: String,
    @XmlSerialName("actor") @XmlElement(true) val actors: List<Actor> = listOf()
)
