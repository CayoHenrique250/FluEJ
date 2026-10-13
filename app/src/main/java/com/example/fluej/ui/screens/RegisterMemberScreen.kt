package com.example.fluej.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.RegisterMemberViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.fluej.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMemberScreen(
    onBack: () -> Unit,
    onNavigateBottomBar: (String) -> Unit,
    viewModel: RegisterMemberViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val cargoOptions = listOf("Assessor", "Diretor", "Gerente", "Trainee")
    val setorOptions = listOf("Marketing", "Vendas", "Projetos", "RH")

    Scaffold(
        containerColor = SurfaceLight,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FluEJ", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestão de membros", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            )
        },
        bottomBar = { 
            BottomNavBar(
                selectedTab = Screen.RegisterMember.route,
                onNavigate = onNavigateBottomBar
            ) }
            
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CADASTRAR MEMBRO",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, GrayBorder, RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (selectedTab == 0) PrimaryDark else Color.Transparent)
                        .clickable { selectedTab = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Dados pessoais",
                        color = if (selectedTab == 0) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (selectedTab == 1) PrimaryDark else Color.Transparent)
                        .clickable { selectedTab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Cargo/setor",
                        color = if (selectedTab == 1) Color.White else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedTab == 0) {
                CustomTextField(
                    label = "Nome completo *",
                    value = viewModel.nome,
                    onValueChange = { viewModel.nome = it }
                )

                CustomTextField(
                    label = "CPF *",
                    value = viewModel.cpf,
                    onValueChange = { input ->
                        viewModel.cpf = input.filter { it.isDigit() }.take(11)
                    },
                    placeholder = "000.000.000-00",
                    keyboardType = KeyboardType.Number,
                    visualTransformation = CpfVisualTransformation()
                )

                DatePickerField(
                    label = "Data de nascimento *",
                    selectedDate = viewModel.dataNascimento,
                    onDateSelected = { viewModel.dataNascimento = it }
                )

                CustomTextField(
                    label = "E-mail *",
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    keyboardType = KeyboardType.Email
                )

                CustomTextField(
                    label = "Telefone *",
                    value = viewModel.telefone,
                    onValueChange = { input ->
                        viewModel.telefone = input.filter { it.isDigit() }.take(11)
                    },
                    placeholder = "(00) 00000-0000",
                    keyboardType = KeyboardType.Phone,
                    visualTransformation = PhoneVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text("Próximo", color = Color.White, fontWeight = FontWeight.Bold)
                }

            } else {
                DropdownSelectField(
                    label = "Cargo *",
                    selectedValue = viewModel.cargo,
                    options = cargoOptions,
                    placeholder = "Selecione o cargo",
                    onOptionSelected = { viewModel.cargo = it }
                )

                DropdownSelectField(
                    label = "Setor *",
                    selectedValue = viewModel.setor,
                    options = setorOptions,
                    placeholder = "Selecione o setor",
                    onOptionSelected = { viewModel.setor = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.cpf = formatCpf(viewModel.cpf)
                        viewModel.telefone = formatPhone(viewModel.telefone)
                        viewModel.saveMember {
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(placeholder, color = TextSecondary) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = GrayBorder,
                unfocusedBorderColor = GrayBorder
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("DD/MM/AAAA", color = TextSecondary) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendário", tint = TextSecondary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = GrayBorder,
                    unfocusedBorderColor = GrayBorder
                )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(formatMillisToDate(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectField(
    label: String,
    selectedValue: String,
    options: List<String>,
    placeholder: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = TextSecondary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = GrayBorder,
                    unfocusedBorderColor = GrayBorder
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(CardBackground)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextPrimary) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0, 11) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 5) out += "."
            if (i == 8) out += "-"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                if (offset <= 11) return offset + 3
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 11) return offset - 2
                if (offset <= 14) return offset - 3
                return trimmed.length
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 11) text.text.substring(0, 11) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            out += trimmed[i]
            if (i == 1) out += ") "
            if (i == 6) out += "-"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 1) return offset + 1
                if (offset <= 6) return offset + 3
                if (offset <= 11) return offset + 4
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return 0
                if (offset <= 2) return offset - 1
                if (offset <= 4) return 2
                if (offset <= 9) return offset - 3
                if (offset <= 10) return 6
                if (offset <= 15) return offset - 4
                return trimmed.length
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

fun formatCpf(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(11)
    return buildString {
        for (i in digits.indices) {
            append(digits[i])
            if (i == 2 || i == 5) append('.')
            if (i == 8) append('-')
        }
    }
}

fun formatPhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(11)
    return buildString {
        for (i in digits.indices) {
            if (i == 0) append('(')
            append(digits[i])
            if (i == 1) append(") ")
            if (i == 6) append('-')
        }
    }
}

fun formatMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(millis))
}