data class Message(
    val text: String,
    var read: Boolean = false
)

data class Chat(
    val messages: MutableList<Message> = mutableListOf()
)

class MessageNotFoundException(message: String) : RuntimeException(message)

sealed interface ChatsResult {
    data class Content(val chats: List<Chat>) : ChatsResult
    object Empty : ChatsResult
}

interface ChatApi {
    fun getChats(): ChatsResult
    fun sendMessage(recipient: Int, message: Message)
    fun deleteMessage(recipient: Int, message: Message): Boolean
    fun getUnreadChatsCount(): Int
    fun deleteChat(recipient: Int): Boolean
    fun getMessages(recipient: Int, count: Int): List<Message>
    fun editMessage(recipient: Int, message: Message, newText: String): Message?
}

object ChatService : ChatApi {
    private var chats = mutableMapOf<Int, Chat>()

    override fun getMessages(recipient: Int, count: Int): List<Message> {
        return chats[recipient]?.messages.orEmpty().takeLast(count).onEach { it.read = true }
    }

    override fun editMessage(recipient: Int, message: Message, newText: String): Message? {
        return chats
            .filter { it.value.messages.contains(message) }
            .let {
                it[recipient]?.messages?.set(
                    chats[recipient]?.messages!!.indexOf(message),
                    message.copy(text = newText)
                )
            }
    }

    override fun deleteChat(recipient: Int): Boolean {
        return when {
            chats.containsValue(chats[recipient]) -> {
                chats.remove(recipient)
                true
            }

            else -> false
        }
    }

    override fun getChats(): ChatsResult {
        return when {
            chats.isEmpty() -> ChatsResult.Empty
            else -> ChatsResult.Content(chats.values.toList())
        }
    }

    override fun sendMessage(recipient: Int, message: Message) {
        chats.getOrPut(recipient) { Chat() }.messages.add(message)
    }

    override fun deleteMessage(recipient: Int, message: Message): Boolean {
        return when {
            chats[recipient]?.messages?.contains(message) == true -> {
                chats[recipient]?.messages?.remove(message)
                if (chats[recipient]?.messages?.isEmpty() == true) {
                    chats.remove(recipient)
                }
                true
            }

            else -> false
        }
    }

    override fun getUnreadChatsCount() = chats.values.count { chat -> chat.messages.any { !it.read } }

    fun printChats() {
        println(chats)
    }
}

fun main() {
    val message1 = Message("Message 1")
    val message2 = Message("Message 2")
    val message3 = Message("Message 3")

    ChatService.sendMessage(1, message1)
    ChatService.sendMessage(1, message2)
    ChatService.sendMessage(2, message3)
    ChatService.printChats()
    println(ChatService.getUnreadChatsCount())
    println(ChatService.deleteChat(1))
    ChatService.deleteMessage(1, message1)
    ChatService.deleteMessage(1, message2)
    ChatService.printChats()

    when (val loadResult = ChatService.getChats()) {
        is ChatsResult.Content -> printListChat(loadResult.chats)
        is ChatsResult.Empty -> println("No messages")
    }

    ChatService.editMessage(2, message3, "Edited message") ?: throw MessageNotFoundException("Message not found")
    ChatService.printChats()
}

fun printListChat(loadResult: List<Chat>) {
    println(loadResult)
}