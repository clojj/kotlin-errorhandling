import Person.Companion.validPerson
import Validations.assertPositive
import Validations.assertValidName
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Validation

data class Exs(val exceptions: List<Exception>) : IllegalStateException()

fun <V, E : Exception> Validation<E>.valid(block: () -> Result<V, E>) =
    if (!this.hasFailure) block()
    else {
        Result.error(Exs(this.failures))
    }

class Person private constructor(val id: Int, val firstName: String, val lastName: String) {

    companion object {

        fun validPerson(id: Int?, firstName: String?, lastName: String?): Result<Person, Exception> {
            val positive = assertPositive(id)
            val firstname = assertValidName(firstName)
            val lastname = assertValidName(lastName)

            return Validation(positive, firstname, lastname).valid {
                Result.of(
                    Person(
                        positive.get(),
                        firstname.get(),
                        lastname.get()
                    )
                )
            }
        }
    }

    override fun toString(): String {
        return "Person(id=$id, firstName='$firstName', lastName='$lastName')"
    }
}

object Validations {
    fun assertPositive(i: Int?): Result<Int, Exception> {
        val message = "null or negative id"
        return when {
            i == null -> Result.error(IllegalStateException(message))
            i > 0 -> Result.of(i)
            else -> Result.error(IllegalStateException(message))
        }
    }

    fun assertValidName(name: String?): Result<String, Exception> {
        val message = "invalid name"
        return when {
            name == null || name.isEmpty() || name[0].toInt() < 65 || name[0].toInt() > 91 -> Result.error(
                IllegalStateException(message)
            )
            else -> Result.of(name)
        }
    }
}

fun main() {
    val personResult: Result<Person, Exception> = validPerson(-1, "first", lastName = "A")
    when (personResult) {
        is Result.Success -> println(personResult.value)
        is Result.Failure -> println((personResult.error as Exs).exceptions.map { it.message })
    }
}

