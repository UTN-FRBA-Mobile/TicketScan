package com.example.ticketscan.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.ticketscan.ui.theme.TicketScanTheme
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun FormattedCurrencyText(
    label: String,
    amount: BigDecimal,
    modifier: Modifier = Modifier,
    labelStyle: SpanStyle = SpanStyle(),
    amountStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color = TicketScanTheme.colors.onSurface
) {
    Text(
        text = buildAnnotatedString {
            if (label.isNotEmpty()) {
                withStyle(style = labelStyle) {
                    append("$label: ")
                }
            }
            val formatted = amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
            withStyle(style = amountStyle) {
                append("$$formatted")
            }
        },
        modifier = modifier,
        style = textStyle,
        color = color
    )
}

