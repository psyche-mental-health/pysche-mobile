import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.UUID

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _userData = MutableLiveData<FirebaseUser?>()
    val userData: LiveData<FirebaseUser?> get() = _userData

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginResult.value = true
                    _userData.value = auth.currentUser
                } else {
                    _loginResult.value = false
                }
            }
    }

    fun generateToken(): String {
        return UUID.randomUUID().toString()
    }
}