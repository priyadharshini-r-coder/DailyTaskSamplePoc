package omnicurekotlin.example.com.userEndpoints.model

class RedirectRequest {

    private var email:String?=null
    private var password:String?=null
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

}