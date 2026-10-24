package de.visualdigits.makemkvconui.domain.model.kodi

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Actor(
    @XmlSerialName("name") @XmlElement(true) val name: String,
    @XmlSerialName("role") @XmlElement(true) val role: String? = null,
    @XmlSerialName("order") @XmlElement(true) val order: String? = null,
    @XmlSerialName("thumb") @XmlElement(true) val thumb: String? = null,
)
