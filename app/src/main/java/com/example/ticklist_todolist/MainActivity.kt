package com.example.ticklist_todolist

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.content.Context
import android.graphics.Paint
import android.text.BoringLayout
import kotlin.math.roundToInt


import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.layout.PaddingValues


import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext


import com.example.ticklist_todolist.ui.theme.TickListTODOLISTTheme
import java.nio.file.WatchEvent


data class Task(
    val text: String,
    val isCompleted: Boolean,
    val category: String = "Personal"
)

data class Category(
    val name: String,
    val color: Color,
    val icon: String,
    val id: String = name
)

object DefaultCategories {
    val categories = listOf(
        Category("Personal", Color(0xFF6366F1), "\uD83C\uDFE0"),

        Category("Work", Color(0xFFEF4444), "\uD83D\uDCBC"),

        Category("Shopping", Color(0xFF10B981), "\uD83D\uDED2"),

        Category("Health", Color(0xFFF59E0B), "\uD83C\uDFE5"),

        Category("Learning", Color(0xFF8B5CF6), "\uD83D\uDCDA")
    )

    fun getCategoryColor(categoryName: String): Color {
        return categories.find { it.name == categoryName }?.color ?: Color(0xFF6366F1)
    }

    fun getCategoryIcon(categoryName: String): String {
        return categories.find { it.name == categoryName }?.icon ?: "\uD83D\uDCDD"
    }

}

object AppTheme {

    interface Colors {
        val background: Color
        val headerBackground: Color
        val surface: Color
        val primary: Color
        val onPrimary: Color
        val textPrimary: Color
        val textSecondary: Color
        val cardBackground: Color
        val addTaskBackground: Color
        val taskCounterBackground: Color
    }
    //Light Mode Colors ( My current Beautiful UI)

    data class LightColors(
        override val background: Color = Color.White, //Current White Background
        override val headerBackground: Color = Color(0xFF6366F1),  //current Purple Background
        override val surface: Color = Color.White,
        override val primary: Color = Color(0xFF6366F1),
        override val onPrimary: Color = Color.White,
        override val textPrimary: Color = Color.Black,
        override val textSecondary: Color = Color.Gray,
        override val cardBackground: Color = Color.White,
        override val addTaskBackground: Color = Color(0xFFE5E7EB),
        override val taskCounterBackground: Color = Color(0xFFF3F4F6)
    ) : Colors

    data class DarkColors(
        override val background: Color = Color(0xFF0F172A),              // Dark background
        override val headerBackground: Color = Color(0xFF1E293B),        // Dark header
        override val surface: Color = Color(0xFF334155),                 // Dark cards
        override val primary: Color = Color(0xFF6366F1),                // Keep same purple
        override val onPrimary: Color = Color.White,                     // White text on purple
        override val textPrimary: Color = Color.White,                   // White text in dark mode
        override val textSecondary: Color = Color(0xFF94A3B8),          // Light gray text
        override val cardBackground: Color = Color(0xFF475569),          // Dark card background
        override val addTaskBackground: Color = Color(0xFF374151),       // Dark "Add task" card
        override val taskCounterBackground: Color = Color(0xFF374151)    // Dark task counter
    ) : Colors

    val lightColors = LightColors()
    val darkColors = DarkColors()

}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TickListTODOLISTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ToDoScreen(

                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    taskCount: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentColors: AppTheme.Colors
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                category.color.copy(alpha = 0.15f)
            else
                currentColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Category Icon with Colored Background

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = category.color.copy(alpha = 0.2f)),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.icon,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = category.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentColors.textPrimary
                    )
                    Text(
                        text = "$taskCount tasks",
                        fontSize = 14.sp,
                        color = currentColors.textSecondary
                    )
                }
            }

            //Arrow indicator

            Text(
                text = "→",
                fontSize = 20.sp,
                color = category.color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun ToDoScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    fun saveTasks(taskList: List<Task>) {

        //Helper Function
        val sharedPrefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        //convert tasks to simple string

        val taskString = taskList.map { "${it.text}|${it.isCompleted}|${it.category}" }
        val tasksSting = taskString.joinToString("###")

        editor.putString("tasks", tasksSting)
        editor.apply()

    }

    fun saveCategories(categoryList: List<Category>) {
        val sharedPrefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

        val editor = sharedPrefs.edit()

        val categoryString = categoryList.map {
            "${it.name}|${it.color.value}|${it.icon}"
        }

        val categoriesString = categoryString.joinToString("###")

        editor.putString("categories", categoriesString)
        editor.apply()
    }

    fun loadTasks(): List<Task> {
        val sharedPrefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

        val tasksString = sharedPrefs.getString("tasks", "") ?: ""

        if (tasksString.isEmpty()) return emptyList()

        return tasksString.split("###").map { taskString ->
            val parts = taskString.split("|")
            when (parts.size) {
                2 -> Task(parts[0], parts[1].toBoolean(), "Personal")
                3 -> Task(parts[0], parts[1].toBoolean(), parts[2])
                else -> Task(parts[0], false, "Personal")
            }
        }
    }

    fun loadCategories(): List<Category> {
        val sharedPrefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

        val categoriesString = sharedPrefs.getString("categories", "") ?: ""

        if (categoriesString.isEmpty()) return DefaultCategories.categories

        return categoriesString.split("###").map { categoryString ->
            val parts = categoryString.split("|")
            Category(parts[0], Color(parts[1].toULong()), parts[2])
        }
    }

    fun saveDarkMode(isDark: Boolean) {
        val sharedPrefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("dark_mode", isDark)
        editor.apply()
    }

    fun loadDarkMode(): Boolean {
        val sharedPrefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

        return sharedPrefs.getBoolean("dark_mode", false)
    }


    var tasks by remember {
        mutableStateOf(loadTasks().ifEmpty {
            listOf(
                Task("Buy groceries", false),
                Task("Finish project presentation", true),
                Task("Call Dentist for appointment ", false),
                Task("Review monthly budget", true)
            )
        })

    }

    var newTaskText by remember { mutableStateOf("") }
    var showAddTask by remember { mutableStateOf(false) }

    var showEditTask by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editTaskText by remember { mutableStateOf("") }

    var categories by remember { mutableStateOf(loadCategories()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var newTaskCategory by remember { mutableStateOf("Personal") }
    var editTaskCategory by remember { mutableStateOf("Personal") }
    var showCategoryManager by remember { mutableStateOf(false) }
    var showCreateCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryIcon by remember { mutableStateOf("\uD83D\uDCDD") }
    var newCategoryColor by remember { mutableStateOf(Color(0xFF6366F1)) }
    var isDarkMode by remember { mutableStateOf(loadDarkMode()) }
    val currentColors = if (isDarkMode) AppTheme.darkColors else AppTheme.lightColors


    Column(modifier = modifier
        .fillMaxSize()
        .background(currentColors.background)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(currentColors.headerBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Tasks",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Stay Organized and Productive",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                //Dark Mode Toggle Button
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            isDarkMode = !isDarkMode
                            saveDarkMode(isDarkMode)
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDarkMode) "☀\uFE0F" else "\uD83C\uDF19",
                            fontSize = 20.sp
                        )
                    }
                }
            }

        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = currentColors.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Categories List

        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            //"All" Category

            item {
                CategoryCard(

                    category = Category("All", Color(0xFF6366F1), "\uD83D\uDCCB"),
                    taskCount = tasks.size,
                    isSelected = selectedCategory == "All",
                    onClick = { selectedCategory = "All" },
                    currentColors = currentColors
                )
            }

            // User Categories

            items(categories) { category ->
                val categoryTaskCount = tasks.count { it.category == category.name }

                CategoryCard(
                    category = category,
                    taskCount = categoryTaskCount,
                    isSelected = selectedCategory == category.name,
                    onClick = { selectedCategory = category.name },
                    currentColors = currentColors
                )
            }

            //Create new category Card

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { showCreateCategory = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB).copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF6366F1).copy(
                                    alpha = 0.2f
                                )
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "➕",
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Create New Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6366F1)

                        )

                    }
                }
            }
        }


        //Add this new section

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showAddTask = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = currentColors.addTaskBackground)
        ) {
            Text(
                text = "Add a new task",
                modifier = Modifier.padding(16.dp),
                color = currentColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Dynamic task list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(
                items = if (selectedCategory == "All") tasks else tasks.filter { it.category == selectedCategory },
                key = { task -> task.text }
            ) { task ->
                TaskItem(
                    taskText = task.text,
                    isCompleted = task.isCompleted,
                    onCheckedChange = { isChecked ->
                        tasks = tasks.map {
                            if (it == task) it.copy(isCompleted = isChecked)
                            else it
                        }
                        saveTasks(tasks)
                    },
                    onDelete = {
                        tasks = tasks.filter { it !== task }
                        saveTasks(tasks)
                    },
                    onLongPress = {
                        editingTask = task
                        editTaskText = task.text
                        showEditTask = true
                    },
                    currentColors = currentColors
                )
            }
        }


        //Task Counter

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = currentColors.taskCounterBackground)
        ) {

            val filteredTask =
                if (selectedCategory == "All") tasks else tasks.filter { it.category == selectedCategory }
            val completedTasks = filteredTask.count { it.isCompleted }
            val totalTasks = filteredTask.size

            Text(
                text = "$completedTasks of $totalTasks tasks completed ",
                modifier = Modifier.padding(16.dp),
                color = currentColors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        //popup code

        if (showAddTask) {
            AlertDialog(
                onDismissRequest = {
                    showAddTask = false
                    newTaskText = ""
                },
                title = {
                    Text(
                        text = "Add New Task",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                },
                text = {
                    Column {

                        TextField(
                            value = newTaskText,
                            onValueChange = { newTaskText = it },
                            placeholder = { Text("Enter Your task...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Category :",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6366F1)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.height(120.dp)
                        ) {
                            items(categories) { category ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable { newTaskCategory = category.name },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (newTaskCategory == category.name)
                                            category.color.copy(alpha = 0.2f)
                                        else
                                            Color.Gray.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = category.icon, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = category.name,
                                            color = if (newTaskCategory == category.name)
                                                category.color
                                            else
                                                Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                },

                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskText.isNotBlank()) {
                                tasks = tasks + Task(newTaskText, false, newTaskCategory)
                                saveTasks(tasks)
                                newTaskText = ""
                                newTaskCategory = "Personal"
                                showAddTask = false
                            }
                        }
                    ) {
                        Text("Save Task")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddTask = false
                        newTaskText = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }


        //Edit Task Dialog
        if (showEditTask && editingTask != null) {

            AlertDialog(
                onDismissRequest = {
                    showEditTask = false
                    editingTask = null
                    editTaskText = ""
                },
                title = {
                    Text(
                        text = "Edit Task",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                },
                text = {
                    TextField(
                        value = editTaskText,
                        onValueChange = { editTaskText = it },
                        placeholder = { Text("Edit Yout task...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editTaskText.isNotBlank() && editingTask != null) {
                                tasks = tasks.map {
                                    if (it == editingTask) it.copy(text = editTaskText)
                                    else it
                                }
                                saveTasks(tasks)
                                editingTask = null
                                editTaskText = ""
                                showEditTask = false

                            }
                        }
                    ) {
                        Text("Update Task")
                    }

                },
                dismissButton = {
                    TextButton(
                        onClick =
                            {
                                showEditTask = false
                                editingTask = null
                                editTaskText = ""
                            }) {
                        Text("Cancel")
                    }
                }

            )
        }
    }


}


@Composable
fun TaskItem(
    taskText: String,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    currentColors: AppTheme.Colors
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isBeingDeleted by remember { mutableStateOf(false) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isBeingDeleted) -1000f else offsetX,
        animationSpec = tween(150),
        finishedListener = {
            if (isBeingDeleted) {
                onDelete() // Delete after fast animation
            }
        },
        label = ""
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) } //New:Detects Swipe Gesture
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX < -200) {
                            //if swiped left enough
                            isBeingDeleted = true
                        } else {
                            offsetX = 0f
                        }
                    }
                ) { _, dragAmount ->
                    offsetX += dragAmount.x //Track finger moment
                    if (offsetX > 0) offsetX = 0f //Don't allow right swipe
                    if (offsetX < -400) offsetX = -400f

                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            },

        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = currentColors.cardBackground)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCheckedChange

            )
            Text(
                text = taskText,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                color = if (isCompleted) currentColors.textSecondary else currentColors.textPrimary
            )
        }
    }
}
