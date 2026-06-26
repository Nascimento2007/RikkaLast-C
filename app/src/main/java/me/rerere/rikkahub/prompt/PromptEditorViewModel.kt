package me.rere.rikkahub.ui.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.rere.rikkahub.data.local.entity.PromptField
import me.rere.rikkahub.data.model.CompiledPrompt
import me.rere.rikkahub.data.repository.PromptRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PromptEditorViewModel(
    private val repository: PromptRepository,
    private val assistantId: Long
) : ViewModel() {

    private val _promptField = MutableStateFlow<PromptField?>(null)
    val promptField: StateFlow<PromptField?> = _promptField.asStateFlow()

    val compiledPrompt: StateFlow<CompiledPrompt> = _promptField
        .filterNotNull()
        .flatMapLatest {
            repository.compilePrompt(assistantId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CompiledPrompt("", 0)
        )

    init {
        viewModelScope.launch {
            repository.getPromptFields(assistantId).collect { field ->
                if (field == null) {
                    val default = repository.createDefault(assistantId)
                    repository.savePromptFields(default)
                    _promptField.value = default
                } else {
                    _promptField.value = field
                }
            }
        }
    }

    fun updateField(
        fieldType: PromptFieldType,
        content: String,
        enabled: Boolean? = null
    ) {
        val current = _promptField.value ?: return
        val updated = when (fieldType) {
            PromptFieldType.NUCLEO_NARRATIVO -> current.copy(
                nucleoNarrativo = content,
                nucleoNarrativoEnabled = enabled ?: current.nucleoNarrativoEnabled
            )
            PromptFieldType.MUNDO_CENARIO -> current.copy(
                mundoCenario = content,
                mundoCenarioEnabled = enabled ?: current.mundoCenarioEnabled
            )
            PromptFieldType.PERSONA -> current.copy(
                personaPersonagem = content,
                personaPersonagemEnabled = enabled ?: current.personaPersonagemEnabled
            )
            PromptFieldType.ESTILO -> current.copy(
                estiloEscrita = content,
                estiloEscritaEnabled = enabled ?: current.estiloEscritaEnabled
            )
            PromptFieldType.CONTEUDO -> current.copy(
                camadaConteudo = content,
                camadaConteudoEnabled = enabled ?: current.camadaConteudoEnabled
            )
            PromptFieldType.ANCORAS -> current.copy(
                ancorasContinuidade = content,
                ancorasContinuidadeEnabled = enabled ?: current.ancorasContinuidadeEnabled
            )
            PromptFieldType.IDIOMA -> current.copy(
                idiomaFormato = content,
                idiomaFormatoEnabled = enabled ?: current.idiomaFormatoEnabled
            )
        }
        _promptField.value = updated
    }

    fun save() {
        viewModelScope.launch {
            _promptField.value?.let { repository.savePromptFields(it) }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            val default = repository.createDefault(assistantId)
            _promptField.value = default
            repository.savePromptFields(default)
        }
    }

    enum class PromptFieldType {
        NUCLEO_NARRATIVO,
        MUNDO_CENARIO,
        PERSONA,
        ESTILO,
        CONTEUDO,
        ANCORAS,
        IDIOMA

    }
}
