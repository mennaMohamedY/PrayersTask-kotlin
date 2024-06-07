package com.example.prayers_task.model

import com.google.gson.annotations.SerializedName

data class PrayersResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Location(

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null
)

data class Weekday(

	@field:SerializedName("en")
	val en: String? = null,

	@field:SerializedName("ar")
	val ar: String? = null
)

data class Hijri(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("month")
	val month: Month? = null,

	@field:SerializedName("holidays")
	val holidays: List<Any?>? = null,

	@field:SerializedName("year")
	val year: String? = null,

	@field:SerializedName("format")
	val format: String? = null,

	@field:SerializedName("weekday")
	val weekday: Weekday? = null,

	@field:SerializedName("designation")
	val designation: Designation? = null,

	@field:SerializedName("day")
	val day: String? = null
)

data class Gregorian(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("month")
	val month: Month? = null,

	@field:SerializedName("year")
	val year: String? = null,

	@field:SerializedName("format")
	val format: String? = null,

	@field:SerializedName("weekday")
	val weekday: Weekday? = null,

	@field:SerializedName("designation")
	val designation: Designation? = null,

	@field:SerializedName("day")
	val day: String? = null
)

data class Date(

	@field:SerializedName("readable")
	val readable: String? = null,

	@field:SerializedName("hijri")
	val hijri: Hijri? = null,

	@field:SerializedName("gregorian")
	val gregorian: Gregorian? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)

data class Method(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("params")
	val params: Params? = null
)

data class Params(

	@field:SerializedName("Isha")
	val isha: Double? = null,

	@field:SerializedName("Fajr")
	val fajr: Double? = null
)

//data class Meta(
//
//	@field:SerializedName("method")
//	val method: Method? = null,
//
//	@field:SerializedName("offset")
//	val offset: Offset? = null,
//
//	@field:SerializedName("school")
//	val school: String? = null,
//
//	@field:SerializedName("timezone")
//	val timezone: String? = null,
//
//	@field:SerializedName("midnightMode")
//	val midnightMode: String? = null,
//
//	@field:SerializedName("latitude")
//	val latitude: Any? = null,
//
//	@field:SerializedName("longitude")
//	val longitude: Any? = null,
//
//	@field:SerializedName("latitudeAdjustmentMethod")
//	val latitudeAdjustmentMethod: String? = null
//)

data class Timings(

	@field:SerializedName("Sunset")
	val sunset: String? = null,

	@field:SerializedName("Asr")
	val asr: String? = null,

	@field:SerializedName("Isha")
	val isha: String? = null,

	@field:SerializedName("Fajr")
	val fajr: String? = null,

	@field:SerializedName("Dhuhr")
	val dhuhr: String? = null,

	@field:SerializedName("Maghrib")
	val maghrib: String? = null,

	@field:SerializedName("Lastthird")
	val lastthird: String? = null,

	@field:SerializedName("Firstthird")
	val firstthird: String? = null,

	@field:SerializedName("Sunrise")
	val sunrise: String? = null,

	@field:SerializedName("Midnight")
	val midnight: String? = null,

	@field:SerializedName("Imsak")
	val imsak: String? = null
)

data class DataItem(

	@field:SerializedName("date")
	val date: Date? = null,

//	@field:SerializedName("meta")
//	val meta: Meta? = null,

	@field:SerializedName("timings")
	val timings: Timings? = null
)

data class Month(

	@field:SerializedName("number")
	val number: Int? = null,

	@field:SerializedName("en")
	val en: String? = null,

	@field:SerializedName("ar")
	val ar: String? = null
)

data class Designation(

	@field:SerializedName("expanded")
	val expanded: String? = null,

	@field:SerializedName("abbreviated")
	val abbreviated: String? = null
)

data class Offset(

	@field:SerializedName("Sunset")
	val sunset: Int? = null,

	@field:SerializedName("Asr")
	val asr: Int? = null,

	@field:SerializedName("Isha")
	val isha: Double? = null,

	@field:SerializedName("Fajr")
	val fajr: Double? = null,

	@field:SerializedName("Dhuhr")
	val dhuhr: Int? = null,

	@field:SerializedName("Maghrib")
	val maghrib: Int? = null,

	@field:SerializedName("Sunrise")
	val sunrise: Int? = null,

	@field:SerializedName("Midnight")
	val midnight: Int? = null,

	@field:SerializedName("Imsak")
	val imsak: Int? = null
)
