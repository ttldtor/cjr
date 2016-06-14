package ttldtor.parsers

import ttldtor.*
import ttldtor.entities.*

class ParseResult(val enterEvents: List<EnterEvent>,
                  val exitEvents: List<ExitEvent>,
                  val messageEvents: List<MessageEvent>,
                  val thirdPersonMessageEvents: List<ThirdPersonMessageEvent>,
                  val allEvents: Map<Long, IEvent>) {
}