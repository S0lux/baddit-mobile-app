package com.example.baddit.presentation.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.unit.Constraints

class InvalidatingPlacementModifierElement :
    ModifierNodeElement<InvalidatingPlacementModifier>() {
    override fun create() = InvalidatingPlacementModifier()

    override fun hashCode(): Int = 10

    override fun equals(other: Any?): Boolean = other === this

    override fun update(node: InvalidatingPlacementModifier) {
        node.invalidatePlacement()
    }
}

class InvalidatingPlacementModifier: Modifier.Node(), LayoutModifierNode {

    override val shouldAutoInvalidate: Boolean = false

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}