package com.example.phodo.PhotoGuide

import android.location.Location
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PhotoGuideItem (

    val photoGuideId : Int,
    val photo : Int, // bitmap 타입으로 변경 //oriPhoto
    val jsonData : String, //contourList로 변경
    val location : Location,
    val locationName : String
    //val mask
    //val proceessingImg
    //like

) : Parcelable


    //var contourArray : JSONArray

    //lateinit var maskImage : Uri
    //var tagArray =  emptyArray<String>()
    //var numberOfLike by Delegates.notNull<Int>()
    //
/*

data class PhotoGuideItem(val photoGuideId: Int, val photo: Int, val jsonData: String?, val location: Location?, val location_name : String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(photoGuideId)
        parcel.writeInt(photo)
        parcel.writeString(jsonData)
        parcel.writeParcelable(location,-1)
        parcel.writeString(location_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoGuideItem> {
        override fun createFromParcel(parcel: Parcel): PhotoGuideItem {
            return PhotoGuideItem(parcel)
        }

        override fun newArray(size: Int): Array<PhotoGuideItem?> {
            return arrayOfNulls(size)
        }
    }
}

 */

