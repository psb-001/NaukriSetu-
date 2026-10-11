package com.naukrisetu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.ui.auth.OtpState
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(email: String, onVerified: () -> Unit, onChangeEmail: () -> Unit) {
    val boxes = remember { List(4) { mutableStateOf("") } }
    val focusers = remember { List(4) { FocusRequester() } }
    var error by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(30) }
    val otp = remember { OtpState() }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    fun code() = boxes.joinToString("") { it.value }
    fun submit() {
        if (otp.verify(code())) onVerified() else error = true
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Text(Strings.otpTitle, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Text("${Strings.otpSentTo} $email", fontSize = 14.sp, color = ThemeColors.InkMute)
        TextButton(onClick = onChangeEmail) { Text(Strings.otpUseDifferent) }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            boxes.forEachIndexed { i, state ->
                OutlinedTextField(
                    value = state.value,
                    onValueChange = { v ->
                        val digit = v.filter { it.isDigit() }.takeLast(1)
                        state.value = digit
                        error = false
                        if (digit.isNotEmpty()) {
                            if (i < 3) focusers[i + 1].requestFocus() else submit()
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                    isError = error,
                    modifier = Modifier.width(56.dp).focusRequester(focusers[i]),
                )
                if (i < 3) Spacer(Modifier.width(12.dp))
            }
        }
        LaunchedEffect(Unit) { focusers[0].requestFocus() }
        Spacer(Modifier.height(8.dp))
        Text(Strings.otpDemoNote, fontSize = 12.sp, color = ThemeColors.InkMute2)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = ::submit,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = ThemeColors.OnPrimary,
            ),
        ) { Text(Strings.otpVerify) }
        Spacer(Modifier.height(8.dp))
        if (secondsLeft > 0) {
            Text("Resend in 0:${secondsLeft.toString().padStart(2, '0')}", fontSize = 13.sp, color = ThemeColors.InkMute)
        } else {
            TextButton(onClick = { secondsLeft = 30 }) { Text("Resend code") }
        }
    }
}
