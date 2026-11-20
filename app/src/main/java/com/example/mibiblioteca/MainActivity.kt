package com.example.mibiblioteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

// ---------- 1. Destinos (5 pantallas) ----------
@Serializable data object Inicio : NavKey
@Serializable data object Catalogo : NavKey
@Serializable data class Detalle(val libroId: Int) : NavKey   // recibe argumento
@Serializable data object Favoritos : NavKey
@Serializable data object Perfil : NavKey

// ---------- 2. Datos de ejemplo ----------
data class Libro(val id: Int, val titulo: String, val autor: String, val sinopsis: String)

val libros = listOf(
    Libro(1, "Cien años de soledad", "Gabriel García Márquez", "La historia de la familia Buendía en Macondo."),
    Libro(2, "El amor en los tiempos del cólera", "Gabriel García Márquez", "Una historia de amor que dura más de medio siglo."),
    Libro(3, "Rayuela", "Julio Cortázar", "Una novela que se puede leer en varios órdenes."),
    Libro(4, "Pedro Páramo", "Juan Rulfo", "Un hombre viaja a Comala a buscar a su padre."),
    Libro(5, "La vorágine", "José Eustasio Rivera", "Novela colombiana ambientada en la selva.")
)

// ---------- 3. Activity ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

// ---------- 4. Navegación con Navigation 3 ----------
@Composable
fun AppNavigation() {
    // El back stack es una lista que TÚ controlas
    val backStack = rememberNavBackStack(Inicio)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Inicio> {
                PantallaInicio(
                    onVerCatalogo = { backStack.add(Catalogo) },
                    onVerFavoritos = { backStack.add(Favoritos) },
                    onVerPerfil = { backStack.add(Perfil) }
                )
            }
            entry<Catalogo> {
                PantallaCatalogo(
                    onLibroClick = { id -> backStack.add(Detalle(id)) },
                    onVolver = { backStack.removeLastOrNull() }
                )
            }
            entry<Detalle> { key ->
                PantallaDetalle(
                    libro = libros.first { it.id == key.libroId },
                    onVolver = { backStack.removeLastOrNull() }
                )
            }
            entry<Favoritos> {
                PantallaFavoritos(onVolver = { backStack.removeLastOrNull() })
            }
            entry<Perfil> {
                PantallaPerfil(onVolver = { backStack.removeLastOrNull() })
            }
        }
    )
}

// ---------- 5. Pantallas ----------
@Composable
fun PantallaInicio(
    onVerCatalogo: () -> Unit,
    onVerFavoritos: () -> Unit,
    onVerPerfil: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📚 Mi Biblioteca", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("Bienvenido, explora tus libros favoritos")
        Spacer(Modifier.height(32.dp))
        Button(onClick = onVerCatalogo, modifier = Modifier.fillMaxWidth()) { Text("Ver catálogo") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onVerFavoritos, modifier = Modifier.fillMaxWidth()) { Text("Mis favoritos") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onVerPerfil, modifier = Modifier.fillMaxWidth()) { Text("Mi perfil") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(onLibroClick: (Int) -> Unit, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("← Atrás") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(libros) { libro ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onLibroClick(libro.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(libro.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(libro.autor, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(libro: Libro, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("← Atrás") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp)) {
            Text(libro.titulo, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Autor: ${libro.autor}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Text(libro.sinopsis)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFavoritos(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("← Atrás") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp)) {
            Text("⭐ Tus libros favoritos", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            libros.take(2).forEach { Text("• ${it.titulo}") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("← Atrás") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp)) {
            Text("👤 Estudiante", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Libros leídos: 12")
            Text("Género favorito: Realismo mágico")
        }
    }
}
