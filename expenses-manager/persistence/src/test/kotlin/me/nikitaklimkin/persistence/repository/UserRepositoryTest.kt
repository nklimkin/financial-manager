package me.nikitaklimkin.persistence.repository

import com.mongodb.client.MongoCollection
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.INVALID_USER_NAME
import me.nikitaklimkin.domain.TELEGRAM_CHAT_ID
import me.nikitaklimkin.domain.buildUser
import me.nikitaklimkin.persistence.buildUserPersistenceModel
import me.nikitaklimkin.persistence.buildUserPersistenceModelWithTbInfo
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.model.UserPersistenceModel
import me.nikitaklimkin.persistence.model.toUserId
import me.nikitaklimkin.useCase.access.UserNotFound
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.save
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private const val NOT_EXISTED_CHAT_ID = -1L

@Testcontainers
class UserRepositoryTest {

    companion object {

        @Container
        private val mongoContainer = MongoDBContainer("mongo:4.0.10")

    }

    private lateinit var userRepository: UserRepository
    private lateinit var collection: MongoCollection<UserPersistenceModel>

    @BeforeEach
    fun setUp() {
        val client = KMongo.createClient(mongoContainer.replicaSetUrl)
        collection = client.getDatabase("test-user").getCollectionOfName("user")
        userRepository = UserRepository(
            client,
            DataBaseProperties("test-user")
        )
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()

    }

    @Test
    fun `when save new user then collection has new document`() {
        userRepository.save(buildUser())

        val results = collection.find().toCollection(mutableListOf())
        results.size shouldBe 1
    }

    @Test
    fun `when find existence user by user name then has match result`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val persisted = userRepository.findByUserName(model.userName!!)

        persisted.isRight() shouldBe true
        val persistedUser = persisted.getOrNull()
        persistedUser shouldNotBe null
        persistedUser?.userName()?.getValue() shouldBe model.userName
        persistedUser?.id shouldBe model.id.toUserId()
    }

    @Test
    fun `when try to find not existed user by user name then has error`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val persisted = userRepository.findByUserName(INVALID_USER_NAME)

        persisted.isLeft() shouldBe true
        persisted.leftOrNull() shouldNotBe null
        persisted.leftOrNull() shouldBe UserNotFound
    }

    @Test
    fun `when find user by telegram chat then has match result`() {
        val model = buildUserPersistenceModelWithTbInfo()
        collection.save(model)

        val persisted = userRepository.findByTelegramChatId(TELEGRAM_CHAT_ID)

        persisted.isRight() shouldBe true
        val persistedUser = persisted.getOrNull()
        persistedUser shouldNotBe null
        persistedUser?.id shouldBe model.id.toUserId()
        persistedUser?.userName()?.getValue() shouldBe model.userName
        persistedUser?.telegramUser()?.chatId shouldBe model.telegramUser?.chatId
    }

    @Test
    fun `when try to find not existed user by chat id then has error`() {
        val model = buildUserPersistenceModelWithTbInfo()
        collection.save(model)

        val persisted = userRepository.findByTelegramChatId(NOT_EXISTED_CHAT_ID)

        persisted.isLeft() shouldBe true
        persisted.leftOrNull() shouldNotBe null
        persisted.leftOrNull() shouldBe UserNotFound
    }
}