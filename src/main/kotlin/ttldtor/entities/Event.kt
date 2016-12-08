package ttldtor.entities

enum class EventType {Message, ThirdPersonMessage, Enter, Exit}

interface Event {
    val type: EventType
    val timestamp: Long
    val who: String
}

interface MessageEvent : Event {
    val message: String
}

data class EnterEvent(override val type: EventType = EventType.Enter,
                      override val timestamp: Long,
                      override val who: String) : Event

data class ExitEvent(override val type: EventType = EventType.Exit,
                     override val timestamp: Long,
                     override val who: String) : Event

data class SimpleMessageEvent(override val message: String,
                              override val type: EventType = EventType.Message,
                              override val timestamp: Long,
                              override val who: String) : MessageEvent

data class ThirdPersonMessageEvent(override var message: String,
                                   override val type: EventType = EventType.ThirdPersonMessage,
                                   override val timestamp: Long,
                                   override var who: String) : MessageEvent