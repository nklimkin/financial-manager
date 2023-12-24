import me.nikitaklimkin.*
import java.util.*

const val VALID_USER_NAME = "unique-user-1"
const val INVALID_USER_NAME = ""
const val TELEGRAM_CHAT_ID = 1L

fun buildValidAddSimpleUserRequest() = AddSimpleUserRequest(VALID_USER_NAME)

fun buildInvalidAddSimpleUserRequest() = AddSimpleUserRequest(INVALID_USER_NAME)

fun buildValidAddTelegramUserRequest() = AddTelegramUserRequest(TELEGRAM_CHAT_ID, VALID_USER_NAME)

fun buildInvalidAddTelegramUserRequest() = AddTelegramUserRequest(TELEGRAM_CHAT_ID, INVALID_USER_NAME)

fun buildUser(): User {
    val user = User.build(
        id = UserId(UUID.randomUUID()),
        userName = UserName.create(VALID_USER_NAME).getOrNull()!!,
        telegramUser = buildTelegramUser(),
        active = true
    )
    return user.getOrNull()!!
}

fun buildTelegramUser() = TelegramUser(
    chatId = TELEGRAM_CHAT_ID,
    userName = UserName.create(VALID_USER_NAME).getOrNull()!!
)