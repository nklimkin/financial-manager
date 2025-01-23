package me.nikitaklimkin.persistence.user.repository

import com.mongodb.client.MongoCollection
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.nikitaklimkin.domain.USER_ID
import me.nikitaklimkin.domain.USER_ID_2
import me.nikitaklimkin.domain.USER_NAME_2
import me.nikitaklimkin.domain.buildUser
import me.nikitaklimkin.domain.user.UserName
import me.nikitaklimkin.persistence.buildUserPersistenceModel
import me.nikitaklimkin.persistence.configuration.DataBaseProperties
import me.nikitaklimkin.persistence.user.model.UserPersistenceModel
import me.nikitaklimkin.persistence.user.model.toUserId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.save
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
        val result = userRepository.save(buildUser())

        result.isRight() shouldBe true

        val results = collection.find().toCollection(mutableListOf())
        results.size shouldBe 1
    }

    @Test
    fun `when save duplicate user then has left value`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val result = userRepository.save(buildUser(id = USER_ID))

        result.isLeft() shouldBe true
    }

    @Test
    fun `when update existed user then has match result`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val newDate = OffsetDateTime.of(
            LocalDateTime.of(1999, 5, 5, 10, 10, 10),
            ZoneOffset.UTC
        )
        val result = userRepository.update(buildUser(id = USER_ID, created = newDate))

        result.isRight() shouldBe true

        val persisted = collection.find().toList()
            .filter { it.id.toUserId().toUuid() == USER_ID.toUuid() }

        persisted.size shouldBe 1
        persisted.first()?.created shouldBe newDate
    }

    @Test
    fun `when update not existed user then has match result`() {
        val newDate = OffsetDateTime.of(
            LocalDateTime.of(1999, 5, 5, 10, 10, 10),
            ZoneOffset.UTC
        )
        val result = userRepository.update(buildUser(created = newDate))

        result.isLeft() shouldBe true
    }

    @Test
    fun `when find existence user by user id then has match result`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val persisted = userRepository.findByUserId(USER_ID)

        persisted shouldNotBe null
        persisted!!.userName().getValue() shouldBe model.userName
        persisted.id shouldBe model.id.toUserId()
    }

    @Test
    fun `when try to find not existed user by id name then has empty`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val persisted = userRepository.findByUserId(USER_ID_2)

        persisted shouldBe null
    }

    @Test
    fun `when find existence user by user name then has match result`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val persisted = userRepository.findByUserName(UserName.from(model.userName).getOrNull()!!)

        persisted shouldNotBe null
        persisted!!.userName().getValue() shouldBe model.userName
        persisted.id shouldBe model.id.toUserId()
    }

    @Test
    fun `when try to find not existed user by user name then has empty`() {
        val model = buildUserPersistenceModel()
        collection.save(model)

        val persisted = userRepository.findByUserName(USER_NAME_2)

        persisted shouldBe null
    }
}