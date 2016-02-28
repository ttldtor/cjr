package ttldtor

enum class EventType {Message, ThirdPersonMessage, Enter, Exit}

interface IEvent {
    val type: EventType
    val timestamp: Long
    val who: String
}

interface IMessageEvent: IEvent {
    val message: String
}

data class EnterEvent(override val type: EventType = EventType.Enter,
                      override val timestamp: Long,
                      override val who: String) : IEvent

data class ExitEvent(override val type: EventType = EventType.Exit,
                     override val timestamp: Long,
                     override val who: String) : IEvent

data class MessageEvent(override val message: String,
                        override val type: EventType = EventType.Message,
                        override val timestamp: Long,
                        override val who: String) : IMessageEvent

data class ThirdPersonMessageEvent(override val message: String,
                                   override val type: EventType = EventType.ThirdPersonMessage,
                                   override val timestamp: Long,
                                   override val who: String) : IMessageEvent