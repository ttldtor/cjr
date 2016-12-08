package ttldtor.parsers

import ttldtor.entities.*

class ParseResult(val enterEvents: List<EnterEvent>,
                  val exitEvents: List<ExitEvent>,
                  val messageEvents: List<SimpleMessageEvent>,
                  val thirdPersonMessageEvents: List<ThirdPersonMessageEvent>,
                  val allEvents: Map<Long, Event>)