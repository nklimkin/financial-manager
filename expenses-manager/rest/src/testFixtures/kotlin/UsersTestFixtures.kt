const val VALID_USER_NAME = "test-user"
const val INVALID_USER_NAME = ""
const val TELEGRAM_CHAT_ID = 1

const val VALID_ADD_SIMPLE_USER_BODY = "{\"userName\":\"$VALID_USER_NAME\"}"
const val INVALID_ADD_SIMPLE_USER_BODY = "{\"userName\":\"$INVALID_USER_NAME\"}"
const val VALID_ADD_TELEGRAM_USER_BODY = "{\"userName\":\"$VALID_USER_NAME\", \"chatId\":\"$TELEGRAM_CHAT_ID\"}"
const val INVALID_ADD_TELEGRAM_USER_BODY = "{\"userName\":\"$INVALID_USER_NAME\", \"chatId\":\"$TELEGRAM_CHAT_ID\"}"