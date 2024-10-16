import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autores")
data class Author(
    @PrimaryKey(autoGenerate = true)
    val autorId: Int = 0,
    val nombre: String,
    val apellido: String,
    val nacionalidad: String
)