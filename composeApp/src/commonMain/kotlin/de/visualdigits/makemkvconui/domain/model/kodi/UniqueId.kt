package de.visualdigits.makemkvconui.domain.model.kodi

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class UniqueId(
    @XmlSerialName("type") @XmlElement(false) val type: String,
    @XmlSerialName("id") @XmlValue val id: String
)
