package com.example.phodo.PhotoGuide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import org.json.JSONArray
import java.io.Serializable
import kotlin.properties.Delegates

data class PhotoGuideItem (

    private var photoGuideId : Int,
    var photo : Int,
    var jsonData : String

) : Serializable

    //var contourArray : JSONArray

    //lateinit var maskImage : Uri
    //var tagArray =  emptyArray<String>()
    //var numberOfLike by Delegates.notNull<Int>()
    //lateinit var location : Location 