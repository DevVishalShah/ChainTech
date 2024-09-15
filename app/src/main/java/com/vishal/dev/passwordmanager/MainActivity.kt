package com.vishal.dev.passwordmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vishal.dev.passwordmanager.model.Passwords
import com.vishal.dev.passwordmanager.ui.theme.PasswordManagerTheme
import com.vishal.dev.passwordmanager.viewmodels.PasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: PasswordViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PassWordManager(viewModel)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PassWordManager(viewModel: PasswordViewModel) {
    var showSheet by remember { mutableStateOf(false) }
    var selectedPassword by remember { mutableStateOf<Passwords?>(null) }
    var showDetails by remember { mutableStateOf(false) }

    if (showSheet) {
        BottomSheetContent(viewModel) {
            showSheet = false
        }
    }
    if (showDetails) {
        selectedPassword?.let {
            ShowDetailsSheet(viewModel, it) {
                showDetails = false
            }
        }
    }
    PasswordManagerTheme {

        Scaffold(topBar = { TopBar() }, floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = Color.Blue, // Background color for FAB
                contentColor = Color.White, // Icon color
                elevation = FloatingActionButtonDefaults.elevation(8.dp) // Shadow/elevation
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Password",
                    modifier = Modifier.size(24.dp)
                )
            }
        }, floatingActionButtonPosition = FabPosition.End
        ) {
            // Main content of the screen goes here
            PasswordList(viewModel) { password ->
                // Show details sheet for the selected password
                selectedPassword = password
                showDetails = true
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Password Manager") }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, titleContentColor = Color.Black // Title color
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    viewModel: PasswordViewModel, onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        PassCreationSheet(viewModel) { onDismiss.invoke() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailsSheet(viewModel: PasswordViewModel, passwords: Passwords, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        PassDetailBottomSheet(passwords, onEditClick = {

        }, onDeleteClick = {
            viewModel.deletePass(passwords)
            onDismiss.invoke()
        })
    }
}

@Composable
fun PassCreationSheet(viewModel: PasswordViewModel, onDismiss: () -> Unit) {
    // State variables for the input fields and validation
    var accountName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Account Name TextField with border
            OutlinedTextField(value = accountName,
                onValueChange = { accountName = it },
                label = { Text("Account Name") },
                isError = accountName.isEmpty() && errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = Color.Red,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                supportingText = {
                    if (accountName.isEmpty() && errorMessage != null) {
                        Text(text = "Account Name cannot be empty.", color = Color.Red)
                    }
                })
            // Username/Email TextField with border
            OutlinedTextField(value = username,
                onValueChange = { username = it },
                label = { Text("Username/Email") },
                isError = username.isEmpty() && errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = Color.Red,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                supportingText = {
                    if (username.isEmpty() && errorMessage != null) {
                        Text(text = "Username/Email cannot be empty.", color = Color.Red)
                    }
                })
            // Password TextField with border
            OutlinedTextField(value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                isError = password.isEmpty() && errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = Color.Red,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                supportingText = {
                    if (password.isEmpty() && errorMessage != null) {
                        Text(text = "Password cannot be empty.", color = Color.Red)
                    }
                })

            Button(
                onClick = {
                    if (accountName.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                        // Save the data to the database
                        viewModel.addPassword(
                            accountType = accountName, username = username, password = password
                        )
                        onDismiss() // Close the sheet
                    } else {
                        errorMessage = "All fields are required." // Set general error message
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Black
                )
            ) {
                Text("Add New Account")
            }
        }
    }
}

@Composable
fun PasswordListItem(password: Passwords, onIconClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(16.dp)),
        onClick = { onIconClick.invoke() }) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = password.accountType, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "•".repeat(password.encryptedPassword.length), // Mask password
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details"
            )
        }
    }

}

@Composable
fun PasswordList(viewModel: PasswordViewModel, onItemClick: (Passwords) -> Unit) {
    // Collect passwords list as state from the ViewModel
    val passwordList by viewModel.passwords.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(passwordList) { password ->
            PasswordListItem(password = password, onIconClick = {
                onItemClick(password)
            })
        }
    }
}

@Composable
fun PassDetailBottomSheet(
    password: Passwords, // Password object from the database
    onEditClick: () -> Unit, // Handle Edit button click
    onDeleteClick: () -> Unit, // Handle Delete button click
) {
    var passwordVisible by remember { mutableStateOf(false) } // To toggle password visibility

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Blue),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Account Type
            Text(text = "Account Type", style = MaterialTheme.typography.labelSmall)
            Text(
                text = password.accountType,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Username/Email
            Text(text = "Username/ Email", style = MaterialTheme.typography.labelSmall)
            Text(
                text = password.username,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Password with toggle visibility icon
            Text(text = "Password", style = MaterialTheme.typography.labelSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = /*if (passwordVisible) PasswordEncryption.decryptPassword(password.encryptedPassword) else*/ "********",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.ThumbUp else Icons.Default.Edit,
                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                    )
                }
            }

            // Edit and Delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Edit Button
                Button(
                    onClick = { onEditClick() }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black, contentColor = Color.White
                    ), modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete Button
                Button(
                    onClick = { onDeleteClick() }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red, contentColor = Color.White
                    ), modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}