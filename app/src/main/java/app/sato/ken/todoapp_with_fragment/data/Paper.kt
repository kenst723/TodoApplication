package app.sato.ken.todoapp_with_fragment.data

import com.fasterxml.jackson.annotation.JsonProperty

class Paper(
    @JsonProperty("serial")      val serial: Long,
    @JsonProperty("id")          val id: String,
    @JsonProperty("title")       val title: String,
    @JsonProperty("description") val description: String
)