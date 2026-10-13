package com.example.fluej.ui.screens
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluej.ui.theme.FluEJTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.fluej.R
import androidx.compose.ui.layout.ContentScale
@Composable
fun LoginScreen() {

    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var isEmailError by remember { mutableStateOf(value = false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_fluej),
            contentDescription = "Logo FluEJ",
            modifier = Modifier.fillMaxWidth(0.7f),
            contentScale = ContentScale.FillWidth
        )

        Spacer(
            modifier = Modifier.height(64.dp)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = {
                email = it
                isEmailError = false
            },
            shape = MaterialTheme.shapes.medium,
            label = {Text("E-mail")},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            supportingText = {
                if (isEmailError) Text(text = "O email não pode ser vazio!")
            },
            isError = isEmailError
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = {
                password = it
            },
            shape = MaterialTheme.shapes.medium,
            label = { Text("Senha") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null
                )

            },
            trailingIcon = {
                IconButton(
                    onClick = {isPasswordVisible = !isPasswordVisible}
                ) {
                    Icon(
                        imageVector =
                            if(isPasswordVisible) {
                                Icons.Outlined.Visibility
                            } else {
                                Icons.Outlined.VisibilityOff
                            },
                        contentDescription = null
                    )
                }
            },
            visualTransformation =
                if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    isEmailError = true
                } else {
                    Toast.makeText(context, "Login realizado com sucesso", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.75f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Entrar", modifier = Modifier.padding(vertical = 8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { }) {
            Text("Esqueceu sua senha?")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FluEJTheme {
        LoginScreen()
    }
}