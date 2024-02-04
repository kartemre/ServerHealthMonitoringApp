package vtys.group.serverhealth.model

data class ServerDataModel(
    val serverid: Int,
    val servername: String,
    val serverip: String,
    val serveros: String,
    val server_ram: String,
    val serverStorageType: String,
    val serverStorageCapacity: String,
    val hospitalid: HospitalDataModel
)

data class InterruptDataModel(
    val interruptid: Int,
    val interruptdate: String,
    val interrupttime: String,
    val interruptstatus: Boolean,
    val serverid: ServerDataModel
)


data class ServerDataModelWithIntHospitalId(
    val serverid: Int,
    val servername: String,
    val serverip: String,
    val serveros: String,
    val server_ram: String,
    val serverStorageType: String,
    val serverStorageCapacity: String,
    val hospitalid: Int
)

data class HospitalDataModel(
    val hospitalid: Int,
    val hospitalname: String,
    val cityid: CityDataModel
) {
    override fun toString(): String {
        return hospitalname
    }
}

data class CityDataModel(
    val cityid: Int,
    val cityname: String
)