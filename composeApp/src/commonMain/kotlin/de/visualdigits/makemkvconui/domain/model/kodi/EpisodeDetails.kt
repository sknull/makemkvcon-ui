package de.visualdigits.makemkvconui.domain.model.kodi

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@OptIn(ExperimentalXmlUtilApi::class)
data class EpisodeDetails(
    @XmlSerialName("title") @XmlElement(true) val title: String,
    @XmlSerialName("showtitle") @XmlElement(true) val showTitle: String,
    @XmlSerialName("season") @XmlElement(true) val season: String,
    @XmlSerialName("episode") @XmlElement(true) val episode: String,
    @XmlSerialName("plot") @XmlElement(true) val plot: String,
    @XmlSerialName("uniqueid") @XmlElement(true) val uniqueId: UniqueId,
    @XmlSerialName("runtime") @XmlElement(true) val runTime: String,
    @XmlSerialName("director") @XmlElement(true) val director: String,
    @XmlSerialName("actor") @XmlElement(true) val actors: List<Actor> = listOf()
) {

    companion object {
        private val builder = DefaultXmlSerializationPolicy.Builder()

        init {
            builder.pedantic = false
            builder.autoPolymorphic = true
            builder.unknownChildHandler = { _, _, _, _, _ -> listOf() }
        }

        private val xmlMapper = XML {
            xmlVersion = XmlVersion.XML11
            xmlDeclMode = XmlDeclMode.Charset
            indent = 4
            repairNamespaces = true
            policy = DefaultXmlSerializationPolicy(builder)
        }
    }

    override fun toString(): String = encodeToSting()

    fun encodeToSting(): String {
        return xmlMapper.encodeToString(serializer(), this)
    }
}
