data class Message(
    val text: String,
    var read: Boolean = false
)

data class Chat(
    val messages: MutableList<Message> = mutableListOf()
)

sealed interface ChatsResult {
    data class Content(val chats: List<Chat>) : ChatsResult
    object Empty : ChatsResult
}

interface ChatApi {
    fun getChats(): ChatsResult
    fun sendMessage(recipient: Int, message: Message)
    fun deleteMessage(recipient: Int, message: Message)
    fun getUnreadChatsCount(): Int
    fun deleteChat(recipient: Int)
    fun getMessages(recipient: Int, count: Int): List<Message>
    fun editMessage(recipient: Int, message: Message, newText: String)
}

object ChatService : ChatApi {
    private var chats = mutableMapOf<Int, Chat>()

    override fun getMessages(recipient: Int, count: Int): List<Message> {
        return chats[recipient]?.messages.orEmpty().takeLast(count).onEach { it.read = true }
    }

    override fun editMessage(recipient: Int, message: Message, newText: String) {
        val index = chats[recipient]?.messages?.indexOf(message)
        val editedMessage = index?.let { chats[recipient]?.messages?.get(it)?.copy(text = newText) }
        index?.let { chats[recipient]?.messages?.removeAt(it) }
        index?.let { editedMessage?.let { newMessage -> chats[recipient]?.messages?.add(it, newMessage) } }
    }

    override fun deleteChat(recipient: Int) {
        chats.remove(recipient)
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

    override fun deleteMessage(recipient: Int, message: Message) {
        chats[recipient]?.messages?.remove(message)
        if (chats[recipient]?.messages?.isEmpty() == true) {
            chats.remove(recipient)
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
    ChatService.deleteChat(1)
    ChatService.deleteMessage(1, message1)
    ChatService.deleteMessage(1, message2)
    ChatService.printChats()

    when (val loadResult = ChatService.getChats()) {
        is ChatsResult.Content -> printListChat(loadResult.chats)
        is ChatsResult.Empty -> println("No messages")
    }

    ChatService.editMessage(2, message3, "Edited message")
    ChatService.printChats()
}

fun printListChat(loadResult: List<Chat>) {
    println(loadResult)
}