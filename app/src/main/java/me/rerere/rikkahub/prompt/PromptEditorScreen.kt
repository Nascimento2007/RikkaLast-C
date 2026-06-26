package me.rere.rikkahub.ui.prompt

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rere.rikkahub.ui.prompt.components.CompiledPromptPreview
import me.rere.rikkahub.ui.prompt.components.PromptFieldCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptEditorScreen(
    viewModel: PromptEditorViewModel,
    onNavigateBack: () -> Unit
) {
    val promptField by viewModel.promptField.collectAsStateWithLifecycle()
    val compiledPrompt by viewModel.compiledPrompt.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showPreview by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Editor de Prompts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showPreview = !showPreview }) {
                        Icon(
                            if (showPreview) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            "Preview"
                        )
                    }
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Default.Save, "Salvar")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.save() },
                icon = { Icon(Icons.Default.Save, null) },
                text = { Text("Salvar Prompts") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = showPreview,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                CompiledPromptPreview(
                    compiledPrompt = compiledPrompt,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val fields = listOf(
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.NUCLEO_NARRATIVO,
                        title = "1. Nucleo Narrativo",
                        subtitle = "Contrato de autoria - quem escreve o que",
                        icon = Icons.Default.Book,
                        content = promptField?.nucleoNarrativo ?: "",
                        enabled = promptField?.nucleoNarrativoEnabled ?: true
                    ),
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.MUNDO_CENARIO,
                        title = "3. Mundo e Cenario",
                        subtitle = "Contexto operacional da narrativa",
                        icon = Icons.Default.Public,
                        content = promptField?.mundoCenario ?: "",
                        enabled = promptField?.mundoCenarioEnabled ?: true
                    ),
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.PERSONA,
                        title = "2. Persona/Personagem",
                        subtitle = "Quem o modelo encarna",
                        icon = Icons.Default.Person,
                        content = promptField?.personaPersonagem ?: "",
                        enabled = promptField?.personaPersonagemEnabled ?: true
                    ),
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.ESTILO,
                        title = "4. Estilo de Escrita",
                        subtitle = "O COMO, nao o QUE",
                        icon = Icons.Default.Edit,
                        content = promptField?.estiloEscrita ?: "",
                        enabled = promptField?.estiloEscritaEnabled ?: true
                    ),
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.CONTEUDO,
                        title = "5. Camada de Conteudo",
                        subtitle = "Permissoes e restricoes narrativas",
                        icon = Icons.Default.Warning,
                        content = promptField?.camadaConteudo ?: "",
                        enabled = promptField?.camadaConteudoEnabled ?: true
                    ),
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.ANCORAS,
                        title = "6. Ancoras de Continuidade",
                        subtitle = "Fatos que nunca podem ser esquecidos",
                        icon = Icons.Default.Anchor,
                        content = promptField?.ancorasContinuidade ?: "",
                        enabled = promptField?.ancorasContinuidadeEnabled ?: true
                    ),
                    FieldConfig(
                        type = PromptEditorViewModel.PromptFieldType.IDIOMA,
                        title = "7. Idioma e Formato",
                        subtitle = "Regras de output",
                        icon = Icons.Default.Language,
                        content = promptField?.idiomaFormato ?: "",
                        enabled = promptField?.idiomaFormatoEnabled ?: true
                    )
                )

                items(fields) { field ->
                    PromptFieldCard(
                        title = field.title,
                        subtitle = field.subtitle,
                        icon = field.icon,
                        content = field.content,
                        enabled = field.enabled,
                        onContentChange = {
                            viewModel.updateField(field.type, it)
                        },
                        onEnabledChange = {
                            viewModel.updateField(
                                field.type,
                                field.content,
                                enabled = it
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { viewModel.resetToDefaults() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Restaurar Templates Padrao")
                    }
                }
            }
        }
    }
}

private data class FieldConfig(
    val type: PromptEditorViewModel.PromptFieldType,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val content: String,
    val enabled: Boolean
)
