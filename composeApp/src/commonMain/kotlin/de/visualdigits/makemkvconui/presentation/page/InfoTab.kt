package de.visualdigits.makemkvconui.presentation.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.title_anthems
import de.visualdigits.generated.AppVersion
import de.visualdigits.makemkvconui.presentation.style.MarineBlueLighter
import de.visualdigits.makemkvconui.presentation.style.gap
import de.visualdigits.makemkvconui.presentation.util.routePlatformLink
import org.jetbrains.compose.resources.stringResource


@Composable
fun InfoTab(
    platformType: PlatformType
) {
    PlatformVerticalScrollbarBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = if (platformType == PlatformType.jvm) 20.dp else 0.dp),
        platformType = platformType,
        scrollbarModifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .width(10.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
    ) {
        listOf(Pair("info", @Composable {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small)
                    .padding(MaterialTheme.shapes.gap),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                val linkColor = MaterialTheme.colorScheme.onSurface

//                Image(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    painter = painterResource(Res.drawable.),
//                    contentDescription = null,
//                    contentScale = ContentScale.Fit,
//                )

                Text(
                    text = remember(linkColor) {
                        htmlToAnnotatedString(
                            html = """
                            <h1>MakeMKVCon UI</h1>
                            <h3>Version ${AppVersion().version}</h3>
                            <br/>
                            <div>© ${KmpOffsetDateTime.now().year} by <a href=\"mailto:s.knull@t-online.de\">Stephan Knull</a>.<div>
                            <div>Github <a href=\"https://github.com/sknull\">My GitHub</a>.<div>
                            """.trimIndent(),
                            style = HtmlStyle(
                                textLinkStyles = TextLinkStyles(style = SpanStyle(color = linkColor)),
                                isTextColorEnabled = true
                            ),
                            linkInteractionListener = { linkAnnotation -> routePlatformLink((linkAnnotation as LinkAnnotation.Url).url) }
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(50.dp))

                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .fillMaxWidth()
                        .background(MarineBlueLighter)
                        .padding(MaterialTheme.shapes.gap),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(Res.string.title_anthems),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }))
    }
}
