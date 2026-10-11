package com.naukrisetu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.naukrisetu.data.SupabaseApi
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OtpScreen(email: String, onVerified: () -> Unit, onChangeEmail: () -> Unit) {
    val boxes = remember { List(6) { mutableStateOf("") } }
    val focusers = remember { List(6) { FocusRequester() } }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(60) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    fun code() = boxes.joinToString("") { it.value }

    fun submit() {
        val otp = code()
        if (otp.length < 6) { error = "Enter all 6 digits"; return }
        loading = true
        error = null
        scope.launch {
            try {
                withContext(Dispatchers.IO) { SupabaseApi.verifyOtp(email, otp) }
                loading = false
                onVerified()
            } catch (e: SupabaseApi.ApiException) {
                loading = false
                error = when (e.status) {
                    400 -> "Invalid or expired code — try again"
                    429 -> "Too many attempts — wait a moment"
                    else -> "Verification failed (${e.status})"
                }
            } catch (e: Exception) {
                loading = false
                error = "Network error — check your connection"
            }
        }
    }

    fun resend() {
        secondsLeft = 60
        error = null
        scope.launch {
            try { withContext(Dispatchers.IO) { SupabaseApi.sendOtp(email) } }
            catch (_: Exception) { error = "Failed to resend — check your connection" }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Text(Strings.otpTitle, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Spacer(Modifier.height(4.dp))
        Text("${Strings.otpSentTo} $email", fontSize = 14.sp, color = ThemeColors.InkMute, textAlign = TextAlign.Center)
        TextButton(onClick = onChangeEmail) { Text(Strings.otpUseDifferent, color = ThemeColors.Primary) }
        Spacer(Modifier.height(24.dp))

        // 6-box OTP entry
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            boxes.forEachIndexed { i, state ->
                OutlinedTextField(
                    value = state.value,
                    onValueChange = { v ->
                        val digit = v.filter { it.isDigit() }.takeLast(1)
                        state.value = digit
                        error = null
                        if (digit.isNotEmpty() && i < 5) focusers[i + 1].requestFocus()
                        if (digit.isNotEmpty() && i == 5) submit()
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    ),
                    isError = error != null,
                    modifier = Modifier.width(48.dp).focusRequester(focusers[i]),
                )
                if (i < 5) Spacer(Modifier.width(8.dp))
            }
        }
        LaunchedEffect(Unit) { focusers[0].requestFocus() }

        Spacer(Modifier.height(8.dp))
        if (error != null) {
            Text(error!!, fontSize = 13.sp, color = ThemeColors.Danger, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = ::submit,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = ThemeColors.OnPrimary,
            ),
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ThemeColors.OnPrimary, strokeWidth = 2.dp)
            } else {
                Text(Strings.otpVerify)
            }
        }
        Spacer(Modifier.height(12.dp))
        if (secondsLeft > 0) {
            Text(
                "Resend code in 0:${secondsLeft.toString().padStart(2, '0')}",
                fontSize = 13.sp,
                color = ThemeColors.InkMute,
            )
        } else {
            TextButton(onClick = ::resend) { Text("Resend code", color = ThemeColors.Primary) }
        }
    }
}
