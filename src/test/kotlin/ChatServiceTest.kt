import kotlin.test.Test
import kotlin.test.assertEquals

class ChatServiceTest {
    @Test
    fun deleteChat() {
        val testMessage = Message("Message 1")
        ChatService.sendMessage(1, testMessage)
        val result = ChatService.deleteChat(1)
        assertEquals(true, result)
    }

    @Test
    fun getUnreadChatsCount() {
        val testMessage = Message("Message 1")
        ChatService.sendMessage(1, testMessage)
        val result = ChatService.getUnreadChatsCount()
        assertEquals(1, result)
    }

    @Test
    fun getMessages() {
        val listMessages: MutableList<Message> = emptyList<Message>().toMutableList()
        val testMessage = Message("Message 1")
        val testMessage2 = Message("Message 2")

        listMessages.add(testMessage)
        listMessages.add(testMessage2)

        ChatService.sendMessage(1, testMessage)
        ChatService.sendMessage(1, testMessage2)

        val result = ChatService.getMessages(1, 2)
        assertEquals(listMessages, result)
    }

    @Test
    fun editMessage() {
        val testMessage = Message("Message 1")
        val editedMessage = Message("edited text")
        ChatService.sendMessage(1, testMessage)
        val result = ChatService.editMessage(1, testMessage, "edited text")
        assertEquals(editedMessage, result)
    }

    @Test
    fun deleteMessage() {
        val testMessage = Message("Message 1")
        ChatService.sendMessage(1, testMessage)
        val result = ChatService.deleteMessage(1, testMessage)
        assertEquals(true, result)
    }

    @Test(expected = MessageNotFoundException::class)
    fun shouldThrowEdit() {
        val testMessage = Message("Message 1")
        val testMessage2 = Message("Message 2")
        ChatService.sendMessage(1, testMessage)
        ChatService.editMessage(1, testMessage2, "edited message")
            ?: throw MessageNotFoundException("Message not found")
    }
}
