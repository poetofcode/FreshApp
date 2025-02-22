package presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    title: String = "Button",
    textColor: Color? = null,
    solidColor: Color? = null,
    borderColor: Color? = null,
    shape: Shape? = null,
    content: (@Composable BoxScope.() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.clickable {
        onClick()
    }.then(RoundedButtonDefaults.defaultRoundedModifier(
        solidColor = solidColor,
        borderColor = borderColor,
        shape = shape,
    )).then(modifier)) {
        if (content == null) {
            RoundedButtonDefaults.defaultContent(
                title = title,
                textColor = textColor
            )
        } else {
            content()
        }
    }
}

object RoundedButtonDefaults {

    @Composable
    fun defaultRoundedModifier(
        solidColor: Color? = null,
        borderColor: Color? = null,
        shape: Shape? = null,
    ) = Modifier
        .border(
            width = 1.dp,
            color = borderColor ?: defaultBorderColor(),
            shape = shape ?: defaultShape()
        )
        .background(
            color = solidColor ?: defaultSolidColor(),
            shape = shape ?: defaultShape()
        ).padding(
            horizontal = 8.dp,
            vertical = 4.dp
        )

    @Composable
    fun defaultBorderColor() = MaterialTheme.colorScheme.onSurface.copy(
        alpha = 0.5f
    )

    @Composable
    fun defaultSolidColor() = MaterialTheme.colorScheme.surfaceBright

    @Composable
    fun defaultTextColor() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun defaultShape() = RoundedCornerShape(size = 8.dp)

    @Composable
    fun defaultContent(title: String, textColor: Color?) {
        Text(
            text = title,
            color = textColor ?: defaultTextColor()
        )
    }
}