package omnicurekotlin.example.com.hospitalEndpoints.model

class HospitalListResponse {


    private var count: Int? = null
    private var errorId: Int? = null
    var errorMessage: String? = null
    var hospitalList: List<Hospital?>? = null
    var id: Long? = null
    private var status: Boolean? = null

    fun getCount(): Int? {
        return count
    }


    fun setCount(count: Int?): HospitalListResponse {
        this.count = count
        return this
    }


    fun getErrorId(): Int? {
        return errorId
    }


    fun setErrorId(errorId: Int?): HospitalListResponse {
        this.errorId = errorId
        return this
    }


    fun getErrorMessage(): String? {
        return errorMessage
    }


    fun setErrorMessage(errorMessage: String?): HospitalListResponse {
        this.errorMessage = errorMessage
        return this
    }


    fun getHospitalList(): List<Hospital?>? {
        return hospitalList
    }


    fun setHospitalList(hospitalList: List<Hospital?>?): HospitalListResponse {
        this.hospitalList = hospitalList
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): HospitalListResponse? {
        this.id = id
        return this
    }

    fun getStatus(): Boolean? {
        return status
    }


    fun setStatus(status: Boolean?): HospitalListResponse {
        this.status = status
        return this
    }

   /* operator fun set(fieldName: String?, value: Any?): HospitalListResponse? {
        return super.set(fieldName, value) as HospitalListResponse?
    }

    fun clone(): HospitalListResponse? {
        return super.clone() as HospitalListResponse?
    }*/

}



