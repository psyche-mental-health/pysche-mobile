import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email
                    )
                    if (userId != null) {
                        firestore.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                _registerResult.value = true
                            }
                            .addOnFailureListener {
                                _registerResult.value = false
                            }
                    } else {
                        _registerResult.value = false
                    }
                } else {
                    _registerResult.value = false
                }
            }
    }
}