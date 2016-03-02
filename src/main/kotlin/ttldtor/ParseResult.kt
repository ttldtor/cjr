package ttldtor

class ParseResult(val enterEvents: List<EnterEvent>,
                  val exitEvents: List<ExitEvent>,
                  val messageEvents: List<MessageEvent>,
                  val thirdPersonMessageEvent: List<ThirdPersonMessageEvent>,
                  val allEvents: Map<Long, IEvent>) {
}